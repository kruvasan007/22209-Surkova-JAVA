package org.example.ThreadPool;

import org.example.core.MessageConstructor;
import org.example.core.Peer;
import org.example.core.PeerMessage;
import org.example.core.Torrent;

import java.nio.ByteBuffer;
import java.util.BitSet;

public class ReadTask implements Runnable {
    private final Torrent torrent;
    private final ByteBuffer msgBuf;
    private final ByteBuffer peerId;
    private final Peer peer;
    private final int connectionId;

    public ReadTask(Torrent t, Peer peer, int size, ByteBuffer pId) {
        torrent = t;
        this.peer = peer;
        this.msgBuf = ByteBuffer.allocate(size);
        byte[] msg = new byte[size];
        System.arraycopy(peer.getBuffer().array(), 0, msg, 0, size);
        this.msgBuf.put(msg);
        this.msgBuf.position(0);
        this.peerId = pId;
        this.connectionId = peer.getConnectionId();
    }

    @Override
    public void run() {
        if (!peer.handshook) {
            PeerMessage peerMessage = MessageConstructor.wrapHandshake(connectionId, peerId, msgBuf);
            if (peerMessage != null) {
                torrent.receiveMessage(peerMessage);
            }
            peer.handshook = true;
        } else {

            PeerMessage peerMessage = processMessage(msgBuf, connectionId);
            if (peerMessage != null) {
                torrent.receiveMessage(peerMessage);
            }
            peer.handshook = true;
        }
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
                    if (j % 8 == 0){
                        b = msg.get();
                    }
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
