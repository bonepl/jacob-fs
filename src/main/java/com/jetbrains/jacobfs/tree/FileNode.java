package com.jetbrains.jacobfs.tree;

import java.io.Serializable;
import java.util.Objects;

public class FileNode implements Serializable {
    private long fileBlockOffset;
    private final long payloadOffset;
    private final int payloadLength;
    private String fileName;

    public FileNode(long fileBlockOffset, String fileName, long payloadOffset, int payloadLength) {
        this.fileBlockOffset = fileBlockOffset;
        this.fileName = fileName;
        this.payloadOffset = payloadOffset;
        this.payloadLength = payloadLength;
    }

    public FileNode(String fileName, long payloadOffset, int payloadLength) {
        this(0, fileName, payloadOffset, payloadLength);
    }

    public long getFileBlockOffset() {
        return fileBlockOffset;
    }

    public void setFileBlockOffset(long fileBlockOffset) {
        this.fileBlockOffset = fileBlockOffset;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getPayloadOffset() {
        return payloadOffset;
    }

    public int getPayloadLength() {
        return payloadLength;
    }

    public long getEndOffset() {
        return payloadOffset + payloadLength;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        FileNode fileNode = (FileNode) o;
        return payloadOffset == fileNode.payloadOffset
                && payloadLength == fileNode.payloadLength
                && Objects.equals(fileName, fileNode.fileName);
    }
}
