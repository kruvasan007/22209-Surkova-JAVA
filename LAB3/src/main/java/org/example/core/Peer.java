package org.example.core;

import org.example.models.ConnectionTags;
import org.example.util.ColorLogger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.BitSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Peer {
    private static final int HANDSHAKE_SIZE = 68;
    private int msgLen = -1;
    private final int BUFFER_CAPACITY = 2 << 14;
    private final ColorLogger logger = new ColorLogger();
    public boolean handshook = false;
    public boolean interested = false;
    public boolean ourInterested = false;
    public boolean choked = true;
    public boolean choking = true;
    private final int connectionId;
    public int requestsQueue;
    private BitSet availablePieces = new BitSet();
    private final ConcurrentLinkedQueue<ByteBuffer> messages = new ConcurrentLinkedQueue<>();


    private int readCount = 0;
    private ByteBuffer lenBuf = ByteBuffer.allocate(4);
    private ByteBuffer msgBuf;


    public Peer(int connectionId) {
        this.connectionId = connectionId;
        requestsQueue = 0;
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

    public ByteBuffer getBuffer() {
        return msgBuf;
    }

    public int handleRead(SocketChannel channel) {
        if (!handshook) return receiveHandshake(channel);

        if (msgLen == -1) {
            try {
                readCount += channel.read(lenBuf);
            } catch (IOException e) {
                logger.logError("Shutdown read " + connectionId);
                return -1;
            }

            if (readCount != 4) {
                return 0;
            }

            lenBuf.flip();
            msgLen = lenBuf.getInt();

            msgBuf = ByteBuffer.allocate(msgLen + 4);
            msgBuf.put(lenBuf.array(), 0, 4);
            readCount = 0;
        }

        try {
            readCount += channel.read(msgBuf);
        } catch (IOException e) {
            logger.logError("Shutdown read " + connectionId);
            return -1;
        }

        if (readCount != msgLen) {
            return 0;
        }

        lenBuf.clear();
        msgLen = -1;
        int toSend = readCount;
        readCount = 0;
        return toSend;
    }

    private int receiveHandshake(SocketChannel channel) {
        if (readCount == 0)
            msgBuf = ByteBuffer.allocate(HANDSHAKE_SIZE);

        try {
            readCount += channel.read(msgBuf);
        } catch (IOException e) {
            logger.logError("Shutdown read " + connectionId);
            return -1;
        }

        if (readCount != HANDSHAKE_SIZE)
            return 0;

        int toSend = readCount;
        readCount = 0;
        return toSend;
    }

    public int handleWrite(SocketChannel channel) {
        ByteBuffer msg = messages.poll();
        int r = 0;
        if (msg != null) {
            try {
                msg.flip();
                byte[] msgArr = msg.array();
                r = channel.write(ByteBuffer.wrap(msgArr));
                msg.clear();
            } catch (IOException e) {
                logger.logError("Shutdown write " + connectionId);
                return -1;
            }
        }
        return r;
    }

    public boolean canGetPiece(int index) {
        try {
            return availablePieces.get(index);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return false;
        }
    }

}