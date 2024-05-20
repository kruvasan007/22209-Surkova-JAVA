package org.example.models;

public class ConnectionTags {
    public final static byte[] PROTOCOL_HEADER = new byte[]{'B', 'i', 't', 'T', 'o', 'r', 'r', 'e', 'n', 't', ' ', 'p', 'r', 'o', 't', 'o', 'c', 'o', 'l'};
    public final static byte[] KEEP_ALIVE = new byte[]{0, 0, 0, 0};
    public final static byte[] CHOKE = new byte[]{0, 0, 0, 1, 0};
    public final static byte[] UNCHOKE = new byte[]{0, 0, 0, 1, 1};
    public final static byte[] INTERESTED = new byte[]{0, 0, 0, 1, 2};
    public final static byte[] NOT_INTERESTED = new byte[]{0, 0, 0, 1, 3};
    public final static byte[] HAVE = new byte[]{0, 0, 0, 5, 4};
    public final static byte[] BITFIELD = new byte[]{0, 0, 0, 0, 5};
    public final static byte[] REQUEST = new byte[]{0, 0, 0, 13, 6};
    public final static byte[] PIECE = new byte[]{0, 0, 0, 0, 7};
}
