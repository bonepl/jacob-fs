package com.jetbrains.jacobfs.tree;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Path;

public class TreeLocatorFileBlockEncoder {

    public void saveFileNodeToFile(Path path, FileNode fileNode, TreeLocatorMetadata treeLocatorMetadata) throws IOException {
        if (treeLocatorMetadata.getTreeLocatorLength() + 128 > treeLocatorMetadata.getTreeLocatorReservedSpace()) {
            throw new RuntimeException("TreeLocator is too big to be saved! Last command has not been persisted");
        }
        byte[] fileBlock = fileNodeToFileBlock(path, fileNode);
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(treeLocatorMetadata.getFile(), "rw")) {
            randomAccessFile.seek(fileNode.getFileBlockOffset());
            randomAccessFile.write(fileBlock);
        }
        if (treeLocatorMetadata.getTreeLocatorOffset() + treeLocatorMetadata.getTreeLocatorLength() == fileNode.getFileBlockOffset()) {
            treeLocatorMetadata.setTreeLocatorLength(treeLocatorMetadata.getTreeLocatorLength() + 128);
        }
        treeLocatorMetadata.saveState();
    }

    public void removeFileNodeFromFile(FileNode fileNode, TreeLocatorMetadata treeLocatorMetadata) throws IOException {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(treeLocatorMetadata.getFile(), "rw")) {
            randomAccessFile.seek(fileNode.getFileBlockOffset());
            randomAccessFile.writeByte(0);
        }
    }

    //int String long int
    //1 8 4 8 4 100
    //1 9 13 21 25
    //isAlive headeroffset pathlength payloadoffset payloadlength
    public byte[] fileNodeToFileBlock(Path path, FileNode fileNode) throws IOException {
        if (path.getParent().toString().length() > 100) {
            throw new IOException("Max 100 characters - file name path too big for FileBlock storage" + path.getParent());
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(128);
        byteBuffer.put((byte) 1);
        byteBuffer.putInt(path.toString().length());
        byteBuffer.putLong(fileNode.getPayloadOffset());
        byteBuffer.putInt(fileNode.getPayloadLength());
        byteBuffer.put(path.toString().getBytes());
        return byteBuffer.array();
    }
}
