package org.example.util;

import java.nio.ByteBuffer;
import java.util.*;

public class Encoder {
    private final int DICTIONARY = 0;
    private final int INTEGER = 1;
    private final int STRING = 2;
    private final int LIST = 3;
    private ColorLogger logger = new ColorLogger();

    public ByteBuffer getInfoBytes(byte[] torrent_file_bytes) throws Exception {
        Object[] decodeDictionary = decodeDictionary(torrent_file_bytes, 0);
        return (ByteBuffer) decodeDictionary[2];
    }

    private int nextObject(byte[] bytes, int offset) {
        return switch (bytes[offset]) {
            case (byte) 'd' -> DICTIONARY;
            case (byte) 'i' -> INTEGER;
            case (byte) 'l' -> LIST;
            case (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9' ->
                    STRING;
            default -> -1;
        };
    }

    public Object decode(byte[] bytes) throws Exception {
        return Objects.requireNonNull(decode(bytes, 0))[1];
    }

    private Object[] decode(byte[] bytes, int offset) throws Exception {
        return switch (nextObject(bytes, offset)) {
            case DICTIONARY -> decodeDictionary(bytes, offset);
            case LIST -> decodeList(bytes, offset);
            case INTEGER -> decodeInteger(bytes, offset);
            case STRING -> decodeString(bytes, offset);
            default -> null;
        };
    }

    private Object[] decodeInteger(byte[] bytes, int offset) throws Exception {
        var curString = new StringBuilder();
        offset++;
        while (bytes[offset] != (byte) 'e') {
            if ((bytes[offset] < 48 || bytes[offset] > 57) && bytes[offset] != 45) {
                logger.logError("Not integer character error");
                throw new Exception("Not integer character error");
            }
            curString.append((char) bytes[offset++]);
        }

        try {
            offset++;
            return new Object[]{offset, Long.parseLong(curString.toString())};
        } catch (NumberFormatException nfe) {
            logger.logError("Error parse integer");
            throw new Exception("Error parse integer");
        }
    }

    private Object[] decodeString(byte[] bytes, int offset) throws Exception {
        var string = new StringBuilder();
        while (bytes[offset] > '/' && bytes[offset] < ':') {
            string.append((char) bytes[offset++]);
        }
        if (bytes[offset] != ':') {
            logger.logError("Error: Invalid character (string parse)");
            throw new Exception("Error parse integer (string parse)");
        }
        offset++;
        int length = Integer.parseInt(string.toString());
        byte[] byte_string = new byte[length];
        System.arraycopy(bytes, offset, byte_string, 0, byte_string.length);
        return new Object[]{offset + length, ByteBuffer.wrap(byte_string)};
    }

    private Object[] decodeList(byte[] bytes, int offset) throws Exception {
        var list = new ArrayList<>();
        offset++;
        Object[] vals;
        while (bytes[offset] != (byte) 'e') {
            vals = decode(bytes, offset);
            offset = (Integer) vals[0];
            list.add(vals[1]);
        }
        offset++;
        return new Object[]{offset, list};
    }

    private Object[] decodeDictionary(byte[] bytes, int offset) throws Exception {
        var map = new TreeMap<>();
        ++offset;
        ByteBuffer hash_bytes = null;
        while (bytes[offset] != (byte) 'e') {
            Object[] vals = decodeString(bytes, offset);
            ByteBuffer key = (ByteBuffer) vals[1];
            offset = (Integer) vals[0];
            var match = true;
            for (int i = 0; i < key.array().length && i < 4; i++) {
                if (!key.equals(ByteBuffer.wrap(new byte[]{'i', 'n', 'f', 'o'}))) {
                    match = false;
                    break;
                }
            }
            int info_offset = -1;
            if (match)
                info_offset = offset;
            vals = decode(bytes, offset);
            offset = ((Integer) vals[0]);
            if (match) {
                hash_bytes = ByteBuffer.wrap(new byte[offset - info_offset]);
                hash_bytes.put(bytes, info_offset, hash_bytes.array().length);
            } else if (vals[1] instanceof HashMap) {
                hash_bytes = (ByteBuffer) vals[2];
            }
            if (vals[1] != null)
                map.put(key, vals[1]);
        }
        return new Object[]{++offset, map, hash_bytes};
    }

}
