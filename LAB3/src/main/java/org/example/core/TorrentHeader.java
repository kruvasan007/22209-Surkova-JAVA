package org.example.core;

import org.example.util.Encoder;

import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class TorrentHeader {
    private final ByteBuffer INFO_TAG = ByteBuffer.wrap(new byte[]
            {'i', 'n', 'f', 'o'});
    private final ByteBuffer LENGTH_TAG = ByteBuffer.wrap(new byte[]
            {'l', 'e', 'n', 'g', 't', 'h'});
    private final ByteBuffer PIECES_TAG = ByteBuffer.wrap(new byte[]
            {'p', 'i', 'e', 'c', 'e', 's'});
    private final ByteBuffer NAME_TAG = ByteBuffer.wrap(new byte[]
            {'n', 'a', 'm', 'e'});
    private final ByteBuffer PIECE_LENGTH_TAG = ByteBuffer.wrap(new byte[]
            {'p', 'i', 'e', 'c', 'e', ' ', 'l', 'e', 'n', 'g', 't', 'h'});
    private static ByteBuffer ANNOUNCE_TAG = ByteBuffer.wrap(new byte[]
            {'a', 'n', 'n', 'o', 'u', 'n', 'c', 'e'});

    private final ByteBuffer infoHash;
    private final URL announceUrl;
    private final String fileName;
    private final Long fileLength;
    private final int pieceLength;
    private final ByteBuffer[] pieceHashes;

    public TorrentHeader(byte[] file) throws Exception {
        Encoder encoder = new Encoder();

        Map<ByteBuffer, Object> fileDict = (Map<ByteBuffer, Object>) encoder.decode(file);

        ByteBuffer urlBytes = (ByteBuffer) fileDict.get(ANNOUNCE_TAG);
        if (urlBytes == null)
            throw new Exception("ERROR URL");

        String url_string = new String(urlBytes.array(), StandardCharsets.US_ASCII);
        announceUrl = new URI(url_string).toURL();

        ByteBuffer info = encoder.getInfoBytes(file);

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(info.array());
            byte[] infoSHA1 = digest.digest();
            infoHash = ByteBuffer.wrap(infoSHA1);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception(e.getLocalizedMessage());
        }

        Map<ByteBuffer, Object> byteInfoDict = (Map<ByteBuffer, Object>) fileDict.get(INFO_TAG);
        if (byteInfoDict == null)
            throw new Exception("ERROR INFO DICT");

        fileLength = (Long) byteInfoDict.get(LENGTH_TAG);
        if (fileLength == null)
            throw new Exception("ERROR FILE LENGTH");

        pieceLength = Math.toIntExact((Long) byteInfoDict.get(PIECE_LENGTH_TAG));

        ByteBuffer byteFileName = (ByteBuffer) byteInfoDict.get(NAME_TAG);
        if (byteFileName == null)
            throw new Exception("ERROR FILE NAME");
        fileName = new String(byteFileName.array(), StandardCharsets.US_ASCII);

        ByteBuffer bytePiesHashes = (ByteBuffer) byteInfoDict.get(PIECES_TAG);
        if (bytePiesHashes == null)
            throw new Exception("ERROR PIECE HASHES");
        byte[] piesHashesArray = bytePiesHashes.array();

        if (piesHashesArray.length % 20 != 0)
            throw new Exception("PIECE HASHES DIVIDE 20 ERROR");
        int num_pieces = piesHashesArray.length / 20;

        pieceHashes = new ByteBuffer[num_pieces];
        for (int i = 0; i < num_pieces; i++) {
            byte[] tmpBuff = new byte[20];
            System.arraycopy(piesHashesArray, i * 20, tmpBuff, 0, 20);
            pieceHashes[i] = ByteBuffer.wrap(tmpBuff);
        }
    }

    public ByteBuffer[] getPieceHashes() {
        return pieceHashes;
    }

    public Long getFileLength() {
        return fileLength;
    }

    public ByteBuffer getInfoHash() {
        return infoHash;
    }

    public URL getAnnounceUrl() {
        return announceUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public int getPieceLength() {
        return pieceLength;
    }
}
