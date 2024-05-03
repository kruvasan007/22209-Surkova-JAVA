package org.example.core;

import org.example.models.MessageType;

import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.HashMap;

public class PeerMessage implements Comparable<PeerMessage> {
    private final ByteBuffer peerId;
    private final Integer connectionId;
    private MessageType type;
    private final HashMap<String, Object> data;

    public PeerMessage(ByteBuffer peerId, Integer connectionId) {
        this.peerId = peerId;
        this.connectionId = connectionId;
        data = new HashMap<>();
    }
    public HashMap<String, Object> getData(){
        return data;
    }

    public void setType(MessageType t){
        type = t;
    }

    public MessageType getType() {
        return type;
    }

    public int getIndex() {
        return (Integer) data.get("index");
    }

    public int getBegin() {
        return (Integer) data.get("begin");
    }

    public int getLength() {
        return (Integer) data.get("length");
    }

    public BitSet getBitfield() {
        return (BitSet) data.get("bitfield");
    }

    public ByteBuffer getBytes() {
        return (ByteBuffer) data.get("bytes");
    }
    public Integer getConnectionId() {
        return connectionId;
    }
    @Override
    public int compareTo(PeerMessage peerMessage) {
        return peerId.compareTo(peerMessage.peerId);
    }

}