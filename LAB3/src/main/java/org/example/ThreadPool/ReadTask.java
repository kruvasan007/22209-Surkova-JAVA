package org.example.ThreadPool;

import org.example.core.MessageConstructor;
import org.example.core.Peer;
import org.example.core.PeerMessage;
import org.example.core.Torrent;

import java.nio.ByteBuffer;
import java.util.BitSet;

public class ReadTask implements Runnable {
    private static final int ID_SIZE = 4;
    private final Torrent torrent;
    private final ByteBuffer bufferForMessages;
    private final ByteBuffer peerId;
    private final Peer peer;

    public ReadTask(Torrent t, ByteBuffer bufferForMessages, ByteBuffer pId, Peer peer) {
        torrent = t;
        this.peer = peer;
        this.bufferForMessages = bufferForMessages;
        peerId = pId;
    }

    @Override
    public void run() {
        peer.lock();
        if (!peer.handshook) {
            int ol = bufferForMessages.position();
            bufferForMessages.position(68).flip();
            ByteBuffer msgBuf = ByteBuffer.allocate(68);
            msgBuf.put(bufferForMessages);
            msgBuf.flip();
            msgBuf.position(0);
            bufferForMessages.limit(ol);
            bufferForMessages.compact();

            PeerMessage peerMessage = MessageConstructor.wrapHandshake(peer.getConnectionId(), peerId, msgBuf);
            if (peerMessage != null) {
                torrent.receiveMessage(peerMessage);
            }
            peer.handshook = true;
        } else {
            int messageLen = ByteBuffer.wrap(peer.getBuff()).getInt() + ID_SIZE;
            while (bufferForMessages.position() >= ID_SIZE && bufferForMessages.position() >= messageLen) {
                int commonLen = bufferForMessages.position();
                ByteBuffer msgBuf = ByteBuffer.allocate(messageLen);
                bufferForMessages.position(messageLen).flip();
                msgBuf.put(bufferForMessages);
                msgBuf.flip();
                bufferForMessages.limit(commonLen);
                bufferForMessages.compact();

                PeerMessage peerMessage = processMessage(msgBuf, peer.getConnectionId());
                if (peerMessage != null) {
                    torrent.receiveMessage(peerMessage);
                }
                messageLen = ByteBuffer.wrap(peer.getBuff()).getInt() + ID_SIZE;
            }
        }
        peer.unlock();
    }

    private PeerMessage processMessage(ByteBuffer msg, int connectionId) {
        int messageLen = msg.getInt();
        if (messageLen == 0)
            return null;

        byte type = msg.get();
        switch (type) {
            case 0 -> {
                return MessageConstructor.wrapChoke(connectionId, peerId);
            }
            case 1 -> {
                return MessageConstructor.wrapUnchoke(connectionId, peerId);
            }
            case 2 -> {
                return MessageConstructor.wrapInterested(connectionId, peerId);
            }
            case 3 -> {
                return MessageConstructor.wrapNotInterested(connectionId, peerId);
            }
            case 4 -> {
                return MessageConstructor.wrapHave(connectionId, peerId, msg.getInt());
            }
            case 5 -> {
                messageLen -= 1;
                BitSet pcs = new BitSet(messageLen * 8);
                byte b = 0;
                for (int j = 0; j < messageLen * 8; ++j) {
                    if (j % 8 == 0) b = msg.get();
                    pcs.set(j, ((b << (j % 8)) & 0x80) != 0);
                }
                return MessageConstructor.wrapBitfield(connectionId, peerId, pcs);
            }
            case 6 -> {
                int index = msg.getInt();
                int begin = msg.getInt();
                int length = msg.getInt();
                return MessageConstructor.wrapRequest(connectionId, peerId, index, begin, length);
            }
            case 7 -> {
                int index = msg.getInt();
                int begin = msg.getInt();
                return MessageConstructor.wrapPiece(connectionId, peerId, index, begin, msg.compact().flip());
            }
            default -> {
                return null;
            }
        }
    }
}
