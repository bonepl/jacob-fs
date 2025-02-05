package com.jetbrains.jacobfs.tree;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        FileNode fileNode = (FileNode) o;
        return offset == fileNode.offset
                && length == fileNode.length
                && Objects.equals(fileName, fileNode.fileName);
    }
}
