package org.example.core;

import org.example.ThreadPool.ReaderThreadPool;
import org.example.models.PeerConfig;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Connection implements Runnable {
    private final HashMap<Integer, Peer> peers = new HashMap<>();
    private int maxPeersCount = 80;
    private final ByteBuffer infoHash;
    private boolean running = true;
    private final ByteBuffer peerId;
    private final ReaderThreadPool reader;
    private Selector selector;
    private final ArrayList<PeerConfig> peersConfig;
    private final ArrayList<SocketChannel> channelArrayList = new ArrayList<>();

    private final Torrent torrent;

    public Connection(ArrayList<PeerConfig> config, Torrent t, ByteBuffer peerId, ByteBuffer iH) {
        peersConfig = config;
        reader = new ReaderThreadPool();
        infoHash = iH;
        this.peerId = peerId;
        torrent = t;
    }

    public void shutdown() {
        running = false;
        reader.interrupt();
        for (Peer p : peers.values()) {
            p.shutdown();
        }
    }

    public synchronized HashMap<Integer, Peer> getPeers() {
        return peers;
    }

    @Override
    public void run() {
        try {
            selector = SelectorProvider.provider().openSelector();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (PeerConfig peerConfig : peersConfig) {

            InetSocketAddress address = new InetSocketAddress(peerConfig.ip, peerConfig.port);

            try {
                doConnect(address, maxPeersCount);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (--maxPeersCount == 0)
                break;
        }

        reader.start();

        while (running) {
            int numReadyChannels = 0;
            try {
                numReadyChannels = selector.select();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (numReadyChannels == 0) {
                continue;
            }

            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();

                Peer peer = (Peer) key.attachment();
                SocketChannel sc = (SocketChannel) key.channel();
                if (sc.isConnected()) {
                    if (key.isWritable()) {
                        if (peer.handleWrite() == -1) {
                            (new Thread(() -> {
                                try {
                                    doConnect(peer.getAddress(), peer.getConnectionId());
                                } catch (IOException e) {
                                    System.out.println("Cannot reconnect");
                                }
                            })).start();
                            continue;
                        }
                    }
                    if (key.isReadable()) {
                        if (peer.handleRead() == -1) {
                            (new Thread(() -> {
                                try {
                                    doConnect(peer.getAddress(), peer.getConnectionId());
                                } catch (IOException e) {
                                    System.out.println("Cannot reconnect");
                                }
                            })).start();
                        }
                    }
                }
            }
            try {
                Thread.sleep(25);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void doConnect(InetSocketAddress addressToConnect, int connectionId) throws IOException {
        SocketChannel channel = SocketChannel.open();
        channel.socket().connect(addressToConnect, 250);
        if (channel.isConnected()) {
            System.out.println("Connected peer " + addressToConnect);
            channel.configureBlocking(false);
            SelectionKey k = channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            Peer pr = new Peer(channel, k, addressToConnect, connectionId, reader, torrent, peerId);
            pr.sendHandshake(infoHash, peerId);
            k.attach(pr);
            if (peers.containsKey(connectionId)) {
                peers.replace(connectionId, pr);
            } else {
                peers.put(connectionId, pr);
            }
            channelArrayList.add(channel);
        }
    }
}
