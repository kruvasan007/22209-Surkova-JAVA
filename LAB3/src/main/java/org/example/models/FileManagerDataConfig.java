package org.example.models;

public class FileManagerDataConfig {
    private long position;
    private byte[] data;

    public FileManagerDataConfig(long pos, byte[] d) {
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
