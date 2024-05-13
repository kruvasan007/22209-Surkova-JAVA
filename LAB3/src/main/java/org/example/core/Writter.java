package org.example.core;

import org.example.models.WritterConfig;
import org.example.util.ColorLogger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;

public class Writter implements Runnable {
    private final ColorLogger logger = new ColorLogger();
    private RandomAccessFile dataFile;
    private final LinkedBlockingQueue<WritterConfig> queue = new LinkedBlockingQueue<>();
    private boolean isRunning = true;

    public Writter(String fName, String path) {
        var newPath = path.split(Pattern.quote(File.separator));
        var len = newPath.length;
        newPath[len - 1] = fName;
        var resPath = new StringBuilder(newPath[0]);
        for (int i = 1; i < len; i++) {
            resPath.append("/").append(newPath[i]);
        }
        try {
            dataFile = new RandomAccessFile(resPath.toString(), "rw");
        } catch (FileNotFoundException e) {
            logger.logError("Error open data file " + e.getMessage());
        }
    }

    public long checkDownloadedPieces(ArrayList<Piece> pieces, Long fileLength, int pieceLength, BitSet downloadPieces) {
        long needToDownload = fileLength;
        try {
            MessageDigest md;
            byte[] sha1;
            md = MessageDigest.getInstance("SHA-1");
            for (Piece pc : pieces) {
                var array = new byte[pc.getSize()];

                try {
                    dataFile.seek((long) pc.getIndex() * pieceLength);
                    dataFile.read(array);
                } catch (IOException e) {
                    logger.logError("Error read data file");
                }
                sha1 = md.digest(array);
                if (Arrays.equals(sha1, pc.getHash())) {
                    needToDownload -= pc.getSize();
                    pc.setState(Piece.PieceState.DOWNLOAD);
                    downloadPieces.set(pc.getIndex());
                }
            }
            logger.logInfo("NEED TO DOWNLOAD: " + needToDownload);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return needToDownload;
    }

    public void stop() {
        isRunning = false;
    }

    @Override
    public void run() {

        while (isRunning) {
            try {
                var conf = queue.take();
                dataFile.seek(conf.getPosition());
                dataFile.write(conf.getData());
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            if (dataFile != null) dataFile.close();
        } catch (IOException e) {
            logger.logError("Datafile close error " + e.getMessage());
        }
    }

    public void put(WritterConfig config) {
        try {
            queue.put(config);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] getData(int length, long position) {
        var array = new byte[length];
        synchronized (dataFile) {
            try {
                dataFile.seek(position);
                dataFile.read(array);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return array;
    }
}
