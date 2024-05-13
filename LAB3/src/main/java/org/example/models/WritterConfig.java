package org.example.models;

public class WritterConfig {
    private long position;
    private byte[] data;

    public WritterConfig(long pos, byte[] d) {
        data = d;
        position = pos;
    }

    public byte[] getData() {
        return data;
    }

    public long getPosition() {
        return position;
    }
}
