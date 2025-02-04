package com.jetbrains.jacobfs.tree;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

public class TreeLocatorMetadata {
    private final File file;
    private int treeLocatorOffset = 12;
    private int treeLocatorLength = 0;
    private int treeLocatorReservedSpace = 1024 * 1024;

    //TODO test for too big locator

    private TreeLocatorMetadata(Path path) {
        file = path.toFile();
    }

    public static TreeLocatorMetadata init(Path path) throws IOException {
        TreeLocatorMetadata tlm = new TreeLocatorMetadata(path);
        tlm.saveState();
        tlm.reserveSpaceForTreeLocator();
        return tlm;
    }

    public static TreeLocatorMetadata load(Path path) throws IOException {
        TreeLocatorMetadata tlm = new TreeLocatorMetadata(path);
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(tlm.file, "r")) {
            tlm.treeLocatorOffset = randomAccessFile.readInt();
            tlm.treeLocatorLength = randomAccessFile.readInt();
            tlm.treeLocatorReservedSpace = randomAccessFile.readInt();
        }
        return tlm;
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
