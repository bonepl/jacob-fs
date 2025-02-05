package com.jetbrains.jacobfs.tree;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

public class TreeLocatorMetadata {
    public static final int DEFAULT_OFFSET = 12;
    public static final int DEFAULT_RESERVED = 1024 * 1024;
    private final File file;
    private int treeLocatorOffset;
    private int treeLocatorLength;
    private int treeLocatorReservedSpace;

    public TreeLocatorMetadata(Path path) throws IOException {
        file = path.toFile();
        if (file.exists()) {
            loadTreeLocatorMetadata();
        } else {
            createNewTreeLocatorMetadata();
        }
    }

    private void createNewTreeLocatorMetadata() throws IOException {
        treeLocatorOffset = DEFAULT_OFFSET;
        treeLocatorLength = 0;
        treeLocatorReservedSpace = DEFAULT_RESERVED;
        reserveSpaceForTreeLocator();
        saveState();
    }

    private void loadTreeLocatorMetadata() throws IOException {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            treeLocatorOffset = randomAccessFile.readInt();
            treeLocatorLength = randomAccessFile.readInt();
            treeLocatorReservedSpace = randomAccessFile.readInt();
        }
    }

    private void reserveSpaceForTreeLocator() throws IOException {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw")) {
            randomAccessFile.seek(this.getTreeLocatorOffset());
            randomAccessFile.write(new byte[this.getTreeLocatorReservedSpace()]);
        }
    }

    public void saveState() throws IOException {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw")) {
            randomAccessFile.writeInt(treeLocatorOffset);
            randomAccessFile.writeInt(treeLocatorLength);
            randomAccessFile.writeInt(treeLocatorReservedSpace);
        }
    }

    public int getTreeLocatorOffset() {
        return treeLocatorOffset;
    }

    public int getTreeLocatorLength() {
        return treeLocatorLength;
    }

    public void setTreeLocatorLength(int treeLocatorLength) {
        this.treeLocatorLength = treeLocatorLength;
    }

    public int getTreeLocatorReservedSpace() {
        return treeLocatorReservedSpace;
    }

    public int getPayloadSpaceOffset() {
        return treeLocatorOffset + treeLocatorReservedSpace;
    }

    public File getFile() {
        return file;
    }
}
