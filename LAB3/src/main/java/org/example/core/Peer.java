package org.example.core;

import java.util.BitSet;

public class Peer {
    public boolean handshook = false;
    public boolean interested = false;
    public boolean ourInterested = false;
    public boolean choked = true;
    public boolean choking = true;
    public int requestsQueue;
    private final PeerConnection peerConnection;
    private BitSet availablePieces = new BitSet();

    public Peer(PeerConnection peerConnection) {
        requestsQueue = 0;
        this.peerConnection = peerConnection;
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
    public PeerConnection getPeerConnection() {
        return this.peerConnection;
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