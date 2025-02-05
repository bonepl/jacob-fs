package com.jetbrains.jacobfs.tree;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public class TreeLocatorMetadata {
    public static int DEFAULT_OFFSET = 12;
    public static int DEFAULT_RESERVED = 1024 * 1024;
    private final File file;
    private int treeLocatorOffset;
    private int treeLocatorLength;
    private int treeLocatorReservedSpace;

    private TreeLocatorMetadata(Path path) {
        file = path.toFile();
    }

    public static TreeLocatorMetadata init(Path path) throws IOException {
        TreeLocatorMetadata tlm = new TreeLocatorMetadata(path);
        if(tlm.file.exists()){
            throw new FileAlreadyExistsException(String.format("Can't create JFS Container at %s", path));
        }
        tlm.treeLocatorOffset = DEFAULT_OFFSET;
        tlm.treeLocatorLength = 0;
        tlm.treeLocatorReservedSpace = DEFAULT_RESERVED;
        tlm.reserveSpaceForTreeLocator();
        tlm.saveState();
        return tlm;
    }

    public static TreeLocatorMetadata load(Path path) throws IOException {
        TreeLocatorMetadata tlm = new TreeLocatorMetadata(path);
        if (!tlm.file.exists()) {
            throw new NoSuchFileException(String.format("Can't load JFS Container from %s", path));
        }
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
