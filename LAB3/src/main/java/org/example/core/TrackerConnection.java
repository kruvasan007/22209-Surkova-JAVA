package org.example.core;

import org.example.util.Encoder;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.TreeMap;

public class TrackerConnection {
    public static final ByteBuffer MIN_INTERVAL = ByteBuffer.wrap(new byte[] {'m','i','n',' ','i','n','t','e','r','v','a','l'});
    public static final ByteBuffer INTERVAL = ByteBuffer.wrap(new byte[] {'i','n','t','e','r','v','a','l'});
    public static final ByteBuffer PEERS = ByteBuffer.wrap(new byte[]{'p', 'e', 'e', 'r', 's'});
    private final URL trackerURL;
    private final Encoder encoder = new Encoder();

    public TrackerConnection(URL trackerURL) {
        this.trackerURL = trackerURL;
    }

    public TreeMap<ByteBuffer, Object> start(String peerId, int port, int uploaded, Long downloaded, Long left, String infoHash) throws Exception {
        return announce("started", peerId, port, uploaded, downloaded, left, infoHash);
    }

    public void stop(String peerId, int port, int uploaded, Long downloaded, Long left, String infoHash) throws Exception {
        announce("stopped", peerId, port, uploaded, downloaded, left, infoHash);
    }

    public void complete(String peerId, int port, int uploaded, Long downloaded, Long left, String infoHash) throws Exception {
        announce("completed", peerId, port, uploaded, downloaded, left, infoHash);
    }

    public void announce(String peerId, int port, int uploaded, Long downloaded, Long left, String infoHash) throws Exception {
        announce(null, peerId, port, uploaded, downloaded, left, infoHash);
    }

    private TreeMap<ByteBuffer, Object> announce(String event, String peerId, int port, int uploaded, Long downloaded, Long left, String infoHash) throws Exception {
        var url = URI.create(this.trackerURL.toString() +
                "?info_hash=" + infoHash +
                "&peer_id=" + peerId +
                "&port=" + port +
                "&uploaded=" + uploaded +
                "&downloaded=" + downloaded +
                "&left=" + left +
                "&event=" + (event != null ? event : "") +
                "&&corrupt=0&key=D554A693&numwant=200&compact=1&no_peer_id=1&supportcry").toURL();

        var connection = (HttpURLConnection) url.openConnection();
        try (DataInputStream dataInputStream = new DataInputStream(connection.getInputStream())) {
            var outputStream = new ByteArrayOutputStream();
            int reads;
            while ((reads = dataInputStream.read()) != -1) {
                outputStream.write(reads);
            }
            var res = (TreeMap<ByteBuffer, Object>) encoder.decode(outputStream.toByteArray());
            outputStream.close();
            return res;
        }
    }
}
