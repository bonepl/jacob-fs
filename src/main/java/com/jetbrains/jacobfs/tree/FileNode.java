package com.jetbrains.jacobfs.tree;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileNode {
    public static final String TAG = "f";
    public static final String SEPARATOR = ":";
    public static final Pattern fileNodePattern = Pattern.compile(TAG + "(\\d+):(\\d+):([^\\n]+)\\n?");
    private final long offset;
    private final int length;
    private String fileName;

    public FileNode(String fileName, long offset, int length) {
        this.fileName = fileName;
        this.offset = offset;
        this.length = length;
    }

    public static FileNode fromString(String encoded) {
        Matcher matcher = fileNodePattern.matcher(encoded);
        if (matcher.find()) {
            return new FileNode(matcher.group(3), Long.parseLong(matcher.group(1)), Integer.parseInt(matcher.group(2)));
        }
        throw new RuntimeException(String.format("Improper FileNode string format: %s", encoded));
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

    @Override
    public int hashCode() {
        int result = Objects.hashCode(fileName);
        result = 31 * result + Long.hashCode(offset);
        result = 31 * result + length;
        return result;
    }

    @Override
    public String toString() {
        return TAG + offset + SEPARATOR
                + length + SEPARATOR
                + fileName;
    }
}
