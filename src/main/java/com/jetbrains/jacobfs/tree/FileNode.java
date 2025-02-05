package com.jetbrains.jacobfs.tree;

public class FileNode {
    private final long offset;
    private final int length;
    private String fileName;

    public FileNode(String fileName, long offset, int length) {
        this.fileName = fileName;
        this.offset = offset;
        this.length = length;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getOffset() {
        return offset;
    }

    public int getLength() {
        return length;
    }

    public long getEndOffset() {
        return offset + length;
    }
}
