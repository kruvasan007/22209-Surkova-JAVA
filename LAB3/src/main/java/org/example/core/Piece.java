package org.example.core;

import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.Timer;
import java.util.TimerTask;

public class Piece {
    public enum PieceState {
        DOWNLOAD, WAIT
    }
    public static final int MAX_CHUNK_SIZE = 2 << 13;
    private final int index;
    private final int size;
    private final byte[] hash;
    private final BitSet chunks;
    private final BitSet loadingChunk;
    private final int countOfCHunks;
    private byte[] data = null;
    private PieceState state = PieceState.WAIT;
    private final Timer timeToLoad;
    private final TimerTask[] loadingTasks;

    public Piece(int index, int size, ByteBuffer hash) {
        this.hash = hash.array();
        this.index = index;
        this.size = size;
        countOfCHunks = (size + (MAX_CHUNK_SIZE) - 1) / (MAX_CHUNK_SIZE);
        chunks = new BitSet(countOfCHunks);
        chunks.clear();
        this.loadingChunk = new BitSet(countOfCHunks);
        timeToLoad = new Timer("Piece timer", true);
        loadingTasks = new TimerTask[countOfCHunks];
    }
    public int getCountOfCHunks() {
        return countOfCHunks;
    }

    public int getIndex() {
        return index;
    }

    public int getSize() {
        return size;
    }

    public byte[] getHash() {
        return hash;
    }

    public PieceState getState() {
        return state;
    }

    public void setState(PieceState st) {
        state = st;
    }

    public void createByteBuffer() {
        data = new byte[size];
    }

    public boolean isDataCreate() {
        return data != null;
    }

    public ByteBuffer getByteBuffer() {
        return ByteBuffer.wrap(data);
    }

    public void clearSlices() {
        this.chunks.clear();
        this.loadingChunk.clear();
    }

    public void setChunkDownloaded(int position) {
        int index = position / MAX_CHUNK_SIZE;
        chunks.set(index, true);
        loadingChunk.set(index, false);
        loadingTasks[index].cancel();
    }

    public int getNextChunk() {
        int chunkIndex = chunks.nextClearBit(0);
        while (loadingChunk.get(chunkIndex))
            chunkIndex = chunks.nextClearBit(chunkIndex + 1);

        if (chunkIndex >= countOfCHunks) return -1;
        startChunkLoading(chunkIndex);

        return chunkIndex;
    }

    private void startChunkLoading(int chunkIndex) {
        loadingChunk.set(chunkIndex);
        final int chunkToLoad = chunkIndex;
        loadingTasks[chunkIndex] = new TimerTask() {
            @Override
            public void run() {
                loadingChunk.clear(chunkToLoad);
            }
        };
        timeToLoad.schedule(loadingTasks[chunkIndex], 45000);
    }

    public int getBeginOfChunk(int chunkIndex) {
        return chunkIndex * MAX_CHUNK_SIZE;
    }

    public int getChunkSize(int chunkIndex) {
        return Math.min(MAX_CHUNK_SIZE, size - chunkIndex * MAX_CHUNK_SIZE);
    }

    public boolean allChunkIsLoaded() {
        return loadingChunk.isEmpty();
    }
}
