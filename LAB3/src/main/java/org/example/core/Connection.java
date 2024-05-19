package org.example.core;

import org.example.ThreadPool.ReadTask;
import org.example.models.PeerConfig;
import org.example.util.ColorLogger;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Connection implements Runnable {

    private int MAX_PEER_COUNT = 80;
    private int COUNT_THREAD_FOR_PARSING = 8;

    private final HashMap<Integer, Peer> peers = new HashMap<>();
    private final ArrayList<PeerConfig> peersConfig;

    private final ByteBuffer peerId;
    private final ByteBuffer infoHash;
    private final Torrent torrent;

    private int welcomePort;
    private boolean running = false;
    private final ThreadPoolExecutor executor;
    private Selector selector;
    private final ArrayList<SocketChannel> channelArrayList = new ArrayList<>();

    private final ColorLogger logger = new ColorLogger();

    public Connection(ArrayList<PeerConfig> config, Torrent t, ByteBuffer peerId, ByteBuffer iH, int port) {
        peersConfig = config;

        /* FOR LOCAL TEST */

        // peersConfig.clear();
        var local = new PeerConfig();
        local.port = 6799;
        local.ip = "192.168.0.10";
        peersConfig.add(local);

        /* FOR LOCAL TEST */

        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(COUNT_THREAD_FOR_PARSING);
        infoHash = iH;
        this.peerId = peerId;
        torrent = t;
        welcomePort = port;
        try {
            logger.logInfo("Port : " + welcomePort + " " + InetAddress.getLocalHost());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public void shutdown() {
        running = false;
        executor.shutdown();
        for (SelectionKey key : selector.selectedKeys()) {
            try {
                key.channel().close();
            } catch (IOException e) {
                logger.logError("Error close channel");
            }
            key.cancel();
        }
        try {
            selector.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public HashMap<Integer, Peer> getPeers() {
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
                doConnect(address, MAX_PEER_COUNT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (--MAX_PEER_COUNT == 0)
                break;
        }

        try {
            createSocketForExternalConnections();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        running = true;
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

                if (key.isAcceptable()) {
                    logger.logInfo("Try to connect");
                    if (MAX_PEER_COUNT > 0) {
                        (new Thread(() -> incomingConnection(key))).start();
                    }
                    continue;
                } else if (key.isConnectable()) {
                    SocketChannel sc = (SocketChannel) key.channel();
                    if (!sc.isConnected()) {
                        try {
                            logger.logInfo("Successfully connect");
                            sc.finishConnect();
                        } catch (IOException e) {
                            logger.logError("Error connected " + key.attachment());
                        }
                    }
                }

                SocketChannel sc = (SocketChannel) key.channel();
                if (sc.isConnected()) {
                    Peer peer = (Peer) key.attachment();
                    if (key.isWritable()) {
                        var flag = peer.handleWrite(sc);
                        if (flag == -1) {
                            SocketAddress address;
                            try {
                                address = sc.getRemoteAddress();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            shutdownChanel(sc, key);
                            if (address != null) {
                                reconnect(peer, address);
                            }
                            continue;
                        }
                    }
                    if (key.isReadable()) {
                        int flagRead = peer.handleRead(sc);
                        if (flagRead == -1) {
                            SocketAddress address;
                            try {
                                address = sc.getRemoteAddress();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            shutdownChanel(sc, key);
                            if (address != null) {
                                reconnect(peer, address);
                            }
                        } else if (flagRead > 0) {
                            executor.execute(new ReadTask(torrent, peer, flagRead, peerId));
                        }
                    }
                }
            }
        }
    }

    private void reconnect(Peer peer, SocketAddress inetAddress) {
        try {
            SocketChannel channel = SocketChannel.open();

            logger.logInfo("Try reconnect");
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE | SelectionKey.OP_CONNECT, peer);
            channel.connect(inetAddress);

            peer.handshook = false;
            peer.sendHandshake(infoHash, peerId);

            channelArrayList.add(channel);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void doConnect(InetSocketAddress addressToConnect, int connectionId) throws IOException {
        SocketChannel channel = SocketChannel.open();
        Peer pr = new Peer(connectionId);

        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE | SelectionKey.OP_CONNECT, pr);
        channel.connect(addressToConnect);

        pr.sendHandshake(infoHash, peerId);

        synchronized (peers) {
            if (peers.containsKey(connectionId)) {
                peers.replace(connectionId, pr);
            } else {
                peers.put(connectionId, pr);
            }
        }
        channelArrayList.add(channel);
    }

    private void createSocketForExternalConnections() throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ServerSocket ss = ssc.socket();
        ss.bind(new InetSocketAddress(welcomePort));
        ss.setReuseAddress(true);
        ssc.configureBlocking(false);
        SelectionKey selectionKey = ssc.register(selector, SelectionKey.OP_ACCEPT);
        selectionKey.interestOps(SelectionKey.OP_ACCEPT);
    }

    private void shutdownChanel(SocketChannel channel, SelectionKey key) {
        try {
            channel.close();
            key.cancel();
        } catch (IOException e) {
            logger.logError("Error shutdown");
        }
    }

    private void incomingConnection(SelectionKey key) {
        try {
            SocketChannel sc = ((ServerSocketChannel) key.channel()).accept();
            if (sc != null) {
                sc.configureBlocking(false);
                Peer pr = new Peer(--MAX_PEER_COUNT);
                sc.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE | SelectionKey.OP_CONNECT, pr);

                pr.sendHandshake(infoHash, peerId);
                pr.sendUnchoke();
                pr.sendBitfield(torrent.getBitField());

                peers.put(MAX_PEER_COUNT, pr);

                channelArrayList.add(sc);
            }
        } catch (IOException e) {
            logger.logError("Cannot accept connection");
        }
    }
}
