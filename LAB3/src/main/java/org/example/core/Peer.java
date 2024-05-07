package org.example.core;

import org.example.ThreadPool.ReadTask;
import org.example.ThreadPool.ReaderThreadPool;
import org.example.models.ConnectionTags;
import org.example.util.ColorLogger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.BitSet;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Peer {
    private final InetSocketAddress address;
    public boolean handshook = false;
    private final ColorLogger logger = new ColorLogger();
    ByteBuffer bufferIn;
    ByteBuffer bufferOut;
    private boolean isLock = false;
    public boolean interested = false;
    public boolean ourInterested = false;
    public boolean choked = true;
    public boolean choking = true;
    public int requestsQueue;
    private final ByteBuffer peedId;
    private final SocketChannel channel;
    private final SelectionKey key;
    private final int connectionId;
    private final ReaderThreadPool reader;
    private final byte[] buffIn = new byte[2 << 14];
    private final Torrent torrent;
    private BitSet availablePieces = new BitSet();
    private final ConcurrentLinkedQueue<ByteBuffer> messages = new ConcurrentLinkedQueue<>();

    public Peer(SocketChannel socket, SelectionKey key, InetSocketAddress addressToConnect, int connectionId, ReaderThreadPool reader, Torrent t, ByteBuffer peedId) {
        this.channel = socket;
        this.key = key;
        this.connectionId = connectionId;
        this.address = addressToConnect;
        this.reader = reader;
        this.torrent = t;
        this.peedId = peedId;
        requestsQueue = 0;

        bufferIn = ByteBuffer.wrap(buffIn);
        bufferOut = ByteBuffer.allocate(1024);
    }

    public byte[] getBuff() {
        return buffIn;
    }

    public void setAvailablePieces(BitSet availablePieces) {
        this.availablePieces = availablePieces;
    }

    public BitSet getAvailablePieces() {
        return availablePieces;
    }

    public void setPieceAvailable(int index) {
        this.availablePieces.set(index);
    }

    public Integer getConnectionId() {
        return connectionId;
    }

    private void sendMessage(ByteBuffer msg) {
        messages.add(msg);
    }

    public void sendUnchoke() {
        sendMessage(ByteBuffer.wrap(ConnectionTags.UNCHOKE));
    }

    public void sendHave(int index) {
        ByteBuffer msg = ByteBuffer.allocate(9);
        msg.put(ConnectionTags.HAVE);
        msg.putInt(index);
        msg.flip();
        sendMessage(msg);
    }

    public void sendPiece(int index, int begin, int length, ByteBuffer pieceData) {
        ByteBuffer msg = ByteBuffer.allocate(13 + length);
        msg.put(ConnectionTags.PIECE);
        msg.putInt(0, 9 + length);
        msg.putInt(index);
        msg.putInt(begin);
        msg.put(pieceData.array(), begin, length);
        msg.flip();
        sendMessage(msg);
    }

    public void sendRequest(int index, int begin, int length) {
        ByteBuffer bb = ByteBuffer.allocate(17);
        bb.put(ConnectionTags.REQUEST);
        bb.putInt(index);
        bb.putInt(begin);
        bb.putInt(length);
        bb.flip();
        sendMessage(bb);
    }

    public void sendInterested() {
        sendMessage(ByteBuffer.wrap(ConnectionTags.INTERESTED));
    }

    public void sendNotInterested() {
        sendMessage(ByteBuffer.wrap(ConnectionTags.NOT_INTERESTED));
    }

    public void sendBitfield(ByteBuffer bitfield) {
        ByteBuffer msg = ByteBuffer.allocate(bitfield.limit() + 5);
        msg.put(ConnectionTags.BITFIELD);
        msg.putInt(0, bitfield.limit() + 1);
        msg.put(bitfield);
        msg.flip();
        sendMessage(msg);
    }

    public void shutdown() {
        try {
            channel.close();
            key.cancel();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendHandshake(ByteBuffer infoHash, ByteBuffer peerId) {
        ByteBuffer handshake = ByteBuffer.allocate(68);
        infoHash.position(0);
        peerId.position(0);

        handshake.put((byte) 19);
        handshake.put(ConnectionTags.PROTOCOL_HEADER);

        handshake.putInt(0);
        handshake.putInt(0);

        handshake.put(infoHash);
        handshake.put(peerId);

        sendMessage(handshake);
    }

    public int handleRead() {
        int readCount;
        byte[] tbuf;
        try {
            tbuf = new byte[1024];
            readCount = channel.read(ByteBuffer.wrap(tbuf));
        } catch (IOException e) {
            shutdown();
            logger.logError("Shutdown read");
            return -1;
        }
        if (readCount > 0) {
            bufferIn.put(tbuf, 0, readCount);
            if (!isLock) {
                reader.execute(new ReadTask(torrent, bufferIn, peedId, this));
            }
            return readCount;
        } else {
            return 0;
        }
    }

    public int handleWrite() {
        ByteBuffer msg = messages.poll();
        int r = 0;
        if (msg != null) {
            try {
                msg.flip();
                byte[] msgArr = msg.array();
                r = channel.write(ByteBuffer.wrap(msgArr));
                msg.clear();
            } catch (IOException e) {
                shutdown();
                logger.logError("Shutdown write");
                return -1;
            }
        }
        return r;
    }

    public void lock() {
        isLock = true;
    }

    public void unlock() {
        isLock = false;
    }

    public InetSocketAddress getAddress() {
        return address;
    }
}