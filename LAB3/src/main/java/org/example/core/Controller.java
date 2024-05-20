package org.example.core;

import org.example.util.Observer;
import org.example.util.ColorLogger;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

public class Controller {
    private final ColorLogger logger = new ColorLogger();
    private Torrent torrent;
    private TorrentHeader torrentHeader;

    public void startTorrentProcess(String path) {
        File torrentFile = new File(path);
        if (!torrentFile.canRead()) {
            logger.logError("Can't read .torrent file");
        }
        try (DataInputStream file = new DataInputStream(new FileInputStream(torrentFile))) {
            byte[] byteFile = new byte[(int) torrentFile.length()];
            file.readFully(byteFile);
            file.close();

            torrentHeader = new TorrentHeader(byteFile);
            torrent = new Torrent(torrentHeader, torrentHeader.getFileName(), path);
            (new Thread(torrent)).start();
        } catch (Exception e) {
            logger.logError("Error while process " + e.getMessage());
        }
    }

    public double getCurrentProgress() {
        return torrent.getProgress();
    }

    public void setObserver(Observer observer) {
        torrent.setObserver(observer);
    }

    public String getTorrentFileName() {
        return torrentHeader.getFileName();
    }

    public String getAlivePeers() {
        return String.valueOf(torrent.getAlivePeers());
    }

    public void stop() {
        torrent.stop();
    }
}
