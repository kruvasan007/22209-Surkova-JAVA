package org.example.core;

import org.example.models.ConnectionTags;
import org.example.util.ColorLogger;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PeerConnection implements Runnable {
    private static final int ID_SIZE = 4;
    private static final int PEER_CONNECTION_TIMEOUT = 4000;
    private static final int HANDSHAKE_SIZE = 68;


    private final MessageConstructor messageConstructor;
    private final ColorLogger logger = new ColorLogger();
    private final Torrent torrent;
    private final String ip;
    private InputStream inputStream;
    private ByteBuffer bufferForMessages;
    private final int port;
    private boolean running = true;
    private boolean handshakeDone = false;
    private final ByteBuffer peerId;
    private final byte[] buff = new byte[2 << 14];
    private final Integer connectionId;
    private final ConcurrentLinkedQueue<ByteBuffer> messages = new ConcurrentLinkedQueue<>();

    public PeerConnection(Torrent t, String i, int p, ByteBuffer pId, int conId) {
        torrent = t;
        ip = i;
        peerId = pId.duplicate();
        port = p;
        connectionId = conId;
        messageConstructor = new MessageConstructor();
    }

    @Override
    public void run() {
        InetSocketAddress address = new InetSocketAddress(ip, port);

        logger.logInfo(ip + " " + connectionId);

        try (Socket sock = new Socket()) {
            try {
                sock.connect(address, PEER_CONNECTION_TIMEOUT);
            } catch (IOException e) {
                //torrent.peerDying(connectionId);
            }
            if (!sock.isConnected()) return;

            OutputStream outputStream = sock.getOutputStream();
            inputStream = sock.getInputStream();
            bufferForMessages = ByteBuffer.wrap(buff);

            while (running) {
                Thread.sleep(10);

                ByteBuffer msg = messages.poll();
                try {
                    if (msg != null) {
                        outputStream.write(msg.array());
                        outputStream.flush();
                    }
                } catch (Exception e) {
                    messages.clear();
                }

                if (inputStream.available() > 0) {
                    if (!handshakeDone) {
                        readHandshake();
                    } else {
                        read();
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            logger.logError("Peer connection thread error");
        }
    }

    private void readHandshake() {
        byte[] message = new byte[HANDSHAKE_SIZE];
        try {
            int readCount = 0;
            while (readCount < HANDSHAKE_SIZE) {
                readCount += inputStream.read(message, readCount,
                        HANDSHAKE_SIZE - readCount);
            }
        } catch (IOException ex) {
            logger.logError("Error: failed to read entire handshake");
            return;
        }

        PeerMessage peerMessage = messageConstructor.wrapHandshake(connectionId, peerId, ByteBuffer.wrap(message));
        if (peerMessage != null) {
            torrent.receiveMessage(peerMessage);
        }
        handshakeDone = true;
    }

    private void read() {
        int readCount;
        try {
            byte[] buff = new byte[1024];
            readCount = inputStream.read(buff);
            bufferForMessages.put(buff, 0, readCount);
        } catch (IOException e) {
            logger.logError("Error reading message len");
            return;
        }

        int messageLen = ByteBuffer.wrap(buff).getInt() + ID_SIZE;
        while (bufferForMessages.position() >= ID_SIZE && bufferForMessages.position() >= messageLen) {
            int commonLen = bufferForMessages.position();
            ByteBuffer msgBuf = ByteBuffer.allocate(messageLen);
            bufferForMessages.position(messageLen).flip();
            msgBuf.put(bufferForMessages);
            msgBuf.flip();
            bufferForMessages.limit(commonLen);
            bufferForMessages.compact();

            PeerMessage peerMessage = processMessage(msgBuf);
            if (peerMessage != null) {
                torrent.receiveMessage(peerMessage);
            }
            messageLen = ByteBuffer.wrap(buff).getInt() + ID_SIZE;
        }
    }

    private PeerMessage processMessage(ByteBuffer msg) {
        int messageLen = msg.getInt();
        if (messageLen == 0)
            return null;

        byte type = msg.get();
        switch (type) {
            case 0 -> {
                return messageConstructor.wrapChoke(connectionId, peerId);
            }
            case 1 -> {
                return messageConstructor.wrapUnchoke(connectionId, peerId);
            }
            case 2 -> {
                return messageConstructor.wrapInterested(connectionId, peerId);
            }
            case 3 -> {
                return messageConstructor.wrapNotInterested(connectionId, peerId);
            }
            case 4 -> {
                return messageConstructor.wrapHave(connectionId, peerId, msg.getInt());
            }
            case 5 -> {
                messageLen -= 1;
                BitSet pcs = new BitSet(messageLen * 8);
                byte b = 0;
                for (int j = 0; j < messageLen * 8; ++j) {
                    if (j % 8 == 0) b = msg.get();
                    pcs.set(j, ((b << (j % 8)) & 0x80) != 0);
                }
                return messageConstructor.wrapBitfield(connectionId, peerId, pcs);
            }
            case 6 -> {
                int index = msg.getInt();
                int begin = msg.getInt();
                int length = msg.getInt();
                return messageConstructor.wrapRequest(connectionId, peerId, index, begin, length);
            }
            case 7 -> {
                int index = msg.getInt();
                int begin = msg.getInt();
                return messageConstructor.wrapPiece(connectionId, peerId, index, begin, msg.compact().flip());
            }
            default -> {
                return null;
            }
        }
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

    public void shutdown() {
        running = false;
    }
}