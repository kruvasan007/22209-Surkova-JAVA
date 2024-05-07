package org.example.core;

import org.example.models.MessageType;

import java.nio.ByteBuffer;
import java.util.BitSet;

public class MessageConstructor {

    public static PeerMessage wrapHandshake(Integer connectionId, ByteBuffer peerId, ByteBuffer msg) {
        PeerMessage m = new PeerMessage(peerId, connectionId);
        m.setType(MessageType.Handshake);
        m.getData().put("bytes", msg);
        return m;
    }

    public static PeerMessage wrapChoke(Integer connectionId, ByteBuffer peerId) {
        PeerMessage m = new PeerMessage(peerId, connectionId);
        m.setType(MessageType.Choke);
        return m;
    }

    public static PeerMessage wrapUnchoke(Integer connectionId, ByteBuffer peerId) {
        PeerMessage m = new PeerMessage(peerId, connectionId);
        m.setType(MessageType.Unchoke);
        return m;
    }

    public static PeerMessage wrapInterested(Integer connectionId, ByteBuffer peerId) {
        PeerMessage m = new PeerMessage(peerId, connectionId);
        m.setType(MessageType.Interested);
        return m;
    }

    public static PeerMessage wrapNotInterested(Integer connectionId, ByteBuffer peerId) {
        PeerMessage m = new PeerMessage(peerId, connectionId);
        m.setType(MessageType.NotInterested);
        return m;
    }

    public static PeerMessage wrapHave(Integer connectionId, ByteBuffer peerId, int index) {
        PeerMessage m = new PeerMessage(peerId, connectionId);
        m.setType(MessageType.Have);
        m.getData().put("index", index);
        return m;
    }

    public static PeerMessage wrapBitfield(Integer connectionId, ByteBuffer peerId, BitSet bitfield) {
        PeerMessage m = new PeerMessage(peerId, connectionId);
        m.setType(MessageType.Bitfield);
        m.getData().put("bitfield", bitfield);
        return m;
    }

    public static PeerMessage wrapRequest(Integer connectionId, ByteBuffer peerId, int index, int begin, int length) {
        PeerMessage m = new PeerMessage(peerId, connectionId);
        m.setType(MessageType.Request);
        m.getData().put("index", index);
        m.getData().put("begin", begin);
        m.getData().put("length", length);
        return m;
    }

    public static PeerMessage wrapPiece(Integer connectionId, ByteBuffer peerId, int index, int begin, ByteBuffer bytes) {
        PeerMessage m = new PeerMessage(peerId, connectionId);
        m.setType(MessageType.Piece);
        m.getData().put("index", index);
        m.getData().put("begin", begin);
        m.getData().put("bytes", bytes);
        return m;
    }

    public static PeerMessage wrapCancel(Integer connectionId, ByteBuffer peerId, int index, int begin, int length) {
        PeerMessage m = new PeerMessage(peerId, connectionId);
        m.setType(MessageType.Cancel);
        m.getData().put("index", index);
        m.getData().put("begin", begin);
        m.getData().put("length", length);
        return m;
    }
}
