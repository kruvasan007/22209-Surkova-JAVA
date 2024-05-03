package org.example.core;

import org.example.models.ConnectionTags;
import org.example.models.MessageType;
import org.example.models.PeerConfig;
import org.example.util.ColorLogger;
import org.example.util.Observer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Pattern;

public class Torrent implements Runnable {
    private static final int MAX_UNCHOKED = 5;
    private org.example.util.Observer observer;
    private final TrackerConnection tracker;
    private final TorrentHeader header;
    private final ArrayList<Piece> pieces;
    private final String infoHashSHA1;
    private RandomAccessFile dataFile;
    private final ConcurrentLinkedQueue<PeerMessage> messages = new ConcurrentLinkedQueue<>();
    private final String peerId;
    private final HashMap<Integer, Peer> peers = new HashMap<>();
    private BitSet downloadPieces = null;
    private boolean isRunning = true;
    private final int port = 6881;
    private int maxPeersCount = 80;
    private int uploaded = 0;
    private Long needToDownload = 0L;
    private Long downloaded = 0L;
    private final ColorLogger logger = new ColorLogger();

    public Torrent(TorrentHeader head, String fName, String path) {
        header = head;
        tracker = new TrackerConnection(header.getAnnounceUrl());
        infoHashSHA1 = encodeInfoHash(header.getInfoHash());

        peerId = generateId();
        pieces = generatePieces();
        needToDownload = head.getFileLength();
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
        checkDownloadedPieces();
    }

    public void setObserver(Observer obrv) {
        observer = obrv;
    }

    private void checkDownloadedPieces() {
        try {
            MessageDigest md;
            byte[] sha1;
            md = MessageDigest.getInstance("SHA-1");
            for (Piece pc : pieces) {
                var array = new byte[pc.getSize()];
                synchronized (dataFile) {
                    try {
                        dataFile.seek((long) pc.getIndex() * header.getPieceLength());
                        dataFile.read(array);
                    } catch (IOException e) {
                        logger.logError("Error read data file");
                    }
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
    }

    @Override
    public void run() {
        try {
            var trackerResponse = tracker.start(peerId, port, uploaded, downloaded, header.getFileLength(), infoHashSHA1);
            connectToPeers(trackerResponse);

            var unchokeTimer = new Timer("Unchoke timer", true);
            unchokeTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Torrent.this.unchokePeers();
                }
            }, 0, 60000);

            while (isRunning) {
                processingMessagesPull();
                sendRequests();

                try {
                    Thread.sleep(350);
                } catch (InterruptedException e) {
                    logger.logError("Thread sleep error " + e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.logError(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (dataFile != null) dataFile.close();
            } catch (IOException e) {
                logger.logError("Datafile close error " + e.getMessage());
            }
            try {
                tracker.stop(peerId, port, uploaded, downloaded, header.getFileLength(), infoHashSHA1);
            } catch (Exception e) {
                logger.logError("Tracker stop error " + e.getMessage());
            }
            logger.logInfo("Download complete!");

            for (Peer pr : peers.values()) {
                pr.getPeerConnection().shutdown();
            }
        }
    }

    private void connectToPeers(TreeMap<ByteBuffer, Object> trackerResponse) {
        var peersConf = decodeCompressedPeers(((ByteBuffer) trackerResponse.get(TrackerConnection.PEERS)));

        logger.logInfo("Count of available peers: " + peersConf.size());

        for (PeerConfig peerConfig : peersConf) {
            PeerConnection connection = new PeerConnection(this, peerConfig.ip,
                    peerConfig.port, ByteBuffer.wrap(peerId.getBytes()), maxPeersCount);
            Peer pr = new Peer(connection);
            connection.sendHandshake(header.getInfoHash(), ByteBuffer.wrap(peerId.getBytes()));
            peers.put(maxPeersCount, pr);
            (new Thread(connection)).start();
            if (--maxPeersCount == 0)
                break;
        }
    }

    private void sendRequests() {
        for (Peer p : peers.values()) {
            if (!p.handshook)
                continue;
            if (!p.choked && p.requestsQueue < 3) {
                int index = choosePiece(p);
                if (index == -1) continue;
                Piece pc = pieces.get(index);
                int chunk = pc.getNextChunk();
                if (chunk != -1 && !downloadPieces.get(index)) {
                    p.requestsQueue++;
                    p.getPeerConnection()
                            .sendRequest(pc.getIndex(), pc.getBeginOfChunk(chunk), pc.getChunkSize(chunk));
                }
            }
        }
    }

    private void unchokePeers() {
        int i = MAX_UNCHOKED;
        for (Peer peer : peers.values()) {
            peer.getPeerConnection().sendUnchoke();
            peer.choking = false;
            if (--i == 0) break;
        }

        if (needToDownload == 0) {
            logger.logInfo("File is already complete.  Seeding.");
        }
    }

    private int choosePiece(Peer pr) {
        int[] pieceRanks = new int[pieces.size()];
        int j = 0;
        for (int i = 0; i < pieces.size(); ++i) {
            if (!downloadPieces.get(i) && pr.getAvailablePieces().get(i)) {
                pieceRanks[j++] = i;
            }
        }
        if (j == 0) {
            return -1;
        }
        Random random = new Random(System.currentTimeMillis());
        return pieceRanks[random.nextInt(j)];
    }

    public void processingMessagesPull() {
        PeerMessage msg;
        while ((msg = messages.poll()) != null) {
            handleMessage(peers.get(msg.getConnectionId()), msg);
        }
    }

    public ByteBuffer getBitField() {
        synchronized (dataFile) {
            byte[] bf = new byte[(pieces.size() + 8 - 1) / 8];
            for (int i = 0; i < pieces.size(); ++i) {
                bf[i / 8] |= (byte) (pieces.get(i).getState() == Piece.PieceState.DOWNLOAD
                        ? 0x80 >> (i % 8) : 0);
            }
            boolean hasPiece = false;
            for (int i = 0; i < pieces.size() / 8; ++i) {
                hasPiece = (bf[i] != 0);
                if (hasPiece) break;
            }
            if (hasPiece)
                return ByteBuffer.wrap(bf);
            return null;
        }
    }

    private void handleMessage(Peer pr, PeerMessage msg) {
        if (!pr.handshook && msg.getType() == MessageType.Handshake) {
            checkHandshake(msg, pr);
        } else {
            switch (msg.getType()) {
                case MessageType.Choke -> {
                    logger.logInfo("CHOKE Message " + msg.getConnectionId());
                    pr.choked = true;
                    pr.requestsQueue = 0;
                }
                case Unchoke -> {
                    logger.logInfo("UNCHOKE Message " + msg.getConnectionId());
                    pr.choked = false;
                }
                case Interested -> {
                    logger.logInfo("INTERESTED Message" + msg.getConnectionId());
                    pr.interested = true;
                }
                case NotInterested -> {
                    logger.logInfo("UNINTERESTED Message" + msg.getConnectionId());
                    pr.interested = false;
                }
                case Have -> {
                    pr.setPieceAvailable(msg.getIndex());
                    logger.logInfo("PEER " + msg.getConnectionId()
                            + " HAS PIECE " + msg.getIndex());
                    if (!downloadPieces.get(msg.getIndex())) {
                        pr.ourInterested = true;
                        pr.getPeerConnection().sendInterested();
                    }
                }
                case Bitfield -> {
                    logger.logInfo("BITFIELD : PEER " + msg.getConnectionId());
                    pr.setAvailablePieces(msg.getBitfield());
                    BitSet tmp = ((BitSet) downloadPieces.clone());
                    tmp.flip(0, downloadPieces.size());
                    if (!tmp.intersects(pr.getAvailablePieces())) {
                        pr.ourInterested = false;
                        pr.getPeerConnection().sendNotInterested();
                    } else {
                        pr.ourInterested = true;
                        pr.getPeerConnection().sendInterested();
                    }
                }
                case Request -> {
                    logger.logInfo("REQUEST Message" + msg.getConnectionId());
                    if (!pr.choking) {
                        var array = new byte[msg.getLength()];
                        synchronized (dataFile) {
                            try {
                                dataFile.seek((long) msg.getIndex() * header.getPieceLength() + msg.getBegin());
                                dataFile.read(array);
                            } catch (IOException e) {
                                logger.logError("Error read data file");
                            }
                        }
                        pr.getPeerConnection().sendPiece(msg.getIndex(), msg.getBegin(), msg.getLength(), ByteBuffer.wrap(array));
                        uploaded += msg.getLength();
                    }
                }
                case Piece -> {
                    Piece pc = pieces.get(msg.getIndex());
                    pr.requestsQueue--;

                    logger.logInfo("GET " + msg.getIndex() + " FROM " + msg.getConnectionId() + " SLICE " + msg.getBegin() / Piece.MAX_CHUNK_SIZE + "/" + pc.getCountOfCHunks());

                    if (!pc.isDataCreate()) pc.createByteBuffer();
                    pc.getByteBuffer().position(msg.getBegin()).put(msg.getBytes());
                    pc.setChunkDownloaded(msg.getBegin());

                    int chunk = pc.getNextChunk();
                    if (chunk == -1 && pc.allChunkIsLoaded() && !downloadPieces.get(msg.getIndex())) {
                        putPiece(pc);
                    } else {
                        pr.getPeerConnection().sendRequest(pc.getIndex(), pc.getBeginOfChunk(chunk), pc.getChunkSize(chunk));
                    }
                }
                default -> {
                }
            }
        }
    }

    private void checkHandshake(PeerMessage msg, Peer pr) {
        ByteBuffer message = msg.getBytes();
        if (message.get() != 19 || message.slice().limit(19).compareTo(ByteBuffer.wrap(ConnectionTags.PROTOCOL_HEADER)) != 0) {
            pr.getPeerConnection().shutdown();
            return;
        }

        if (message.slice().position(19 + 8).limit(20).compareTo(header.getInfoHash()) != 0) {
            pr.getPeerConnection().shutdown();
            return;
        }

        ByteBuffer bf = getBitField();
        if (bf != null) {
            pr.getPeerConnection().sendBitfield(bf);
        }
        pr.handshook = true;
    }

    public void putPiece(Piece piece) {
        MessageDigest md;
        byte[] sha1 = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
            sha1 = md.digest(piece.getByteBuffer().array());
        } catch (NoSuchAlgorithmException e) {
            logger.logError("Error getting piece's SHA");
        }

        synchronized (peers) {
            synchronized (dataFile) {
                if (Arrays.equals(sha1, piece.getHash())) {
                    downloadPieces.set(piece.getIndex());
                    piece.setState(Piece.PieceState.DOWNLOAD);

                    try {
                        dataFile.seek((long) piece.getIndex() * header.getPieceLength());
                        dataFile.write(piece.getByteBuffer().array());
                    } catch (IOException e) {
                        logger.logError("Data file write error");
                    }

                    for (Peer p : peers.values()) {
                        p.getPeerConnection().sendHave(piece.getIndex());

                        BitSet tmp = ((BitSet) downloadPieces.clone());
                        tmp.flip(0, downloadPieces.size());
                        if (!tmp.intersects(p.getAvailablePieces())) {
                            p.ourInterested = false;
                            p.getPeerConnection().sendNotInterested();
                        }
                    }
                    downloaded += piece.getSize();
                    needToDownload -= piece.getSize();
                    logger.logInfo(100 * downloaded / header.getFileLength() + "% download");
                    observer.onNotified();
                } else {
                    piece.clearSlices();
                    piece.setState(Piece.PieceState.WAIT);
                    return;
                }
            }
        }

        if (downloadPieces.nextClearBit(0) == pieces.size() && isRunning) {
            isRunning = false;
            try {
                tracker.complete(peerId, port, uploaded, downloaded, needToDownload, infoHashSHA1);
            } catch (Exception e) {
                logger.logError("Tracker complete error");
            }
        }
    }

    private String encodeInfoHash(ByteBuffer infoHash) {
        StringBuilder sb = new StringBuilder();
        for (byte b : infoHash.array()) {
            sb.append(String.format("%%%02X", b));
        }
        return sb.toString();
    }

    private ArrayList<Piece> generatePieces() {
        var piecesList = new ArrayList<Piece>();
        Long total = header.getFileLength();
        for (int i = 0; i < header.getPieceHashes().length; ++i) {
            int pieceLength = header.getPieceLength();
            if (total < header.getPieceLength()) pieceLength = Math.toIntExact(total);
            piecesList.add(new Piece(i, pieceLength, header.getPieceHashes()[i]));
            total -= header.getPieceLength();
        }
        downloadPieces = new BitSet(piecesList.size());
        logger.logInfo("COUNT OF PIECES " + piecesList.size());
        return piecesList;
    }

    public ArrayList<PeerConfig> decodeCompressedPeers(ByteBuffer peers) {
        ArrayList<PeerConfig> peerConfList = new ArrayList<>();
        try {
            for (int i = 0; i < peers.array().length / 6; i++) {
                var peerConf = new PeerConfig();
                var b0 = peers.get();
                var b1 = peers.get();
                var b2 = peers.get();
                var b3 = peers.get();
                peerConf.ip = String.format("%d.%d.%d.%d",
                        b0 & 0xff,
                        b1 & 0xff,
                        b2 & 0xff,
                        b3 & 0xff);
                b1 = peers.get();
                b2 = peers.get();
                int i1 = 0xFF & b1;
                int i2 = 0xFF & b2;
                peerConf.port = (i1 << 8) + i2;
                peerConfList.add(peerConf);
            }
        } catch (BufferUnderflowException e) {
            logger.logInfo("Error decode peers list");
        }
        return peerConfList;
    }

    private String generateId() {
        var stringBuilder = new StringBuilder(20);
        stringBuilder.append("-UT1820-");
        var myRandom = new Random(System.currentTimeMillis());
        for (int i = 0; i < 12; ++i) {
            stringBuilder.append((char) (myRandom.nextInt(26) + 65));
        }
        logger.logInfo("MY PEER ID: " + stringBuilder + "\n");
        return stringBuilder.toString();
    }

    public void receiveMessage(PeerMessage message) {
        messages.add(message);
    }

    public void peerDying(Integer id) {
        peers.remove(id);
        logger.logInfo("Peer " + id + " died :(; Peers alive now: " + peers.size());
        observer.onNotified();
    }

    public double getProgress() {
        return (double) downloaded / header.getFileLength();
    }

    public int getAlivePeers() {
        return peers.size();
    }

    public void stop() {
        isRunning = false;
    }
}
