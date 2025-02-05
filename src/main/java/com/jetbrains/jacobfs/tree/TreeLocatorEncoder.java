package com.jetbrains.jacobfs.tree;

import java.io.IOException;
import java.io.RandomAccessFile;

public final class TreeLocatorEncoder {
    public static final String DIR_NODE_TAG = "d";
    public static final String FILE_NODE_TAG = "f";
    public static final String FILE_NODE_SEPARATOR = ":";
    public static final Character NO_MORE_CHILDREN_TAG = '/';
    public static final String END_LINE_TAG = "\n";

    public void saveTreeLocatorToFile(RootNode rootNode, TreeLocatorMetadata treeLocatorMetadata) throws IOException {
        String string = encode(rootNode);
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(treeLocatorMetadata.getFile(), "rw")) {
            randomAccessFile.seek(treeLocatorMetadata.getTreeLocatorOffset());
            randomAccessFile.writeBytes(string);
        }
        treeLocatorMetadata.setTreeLocatorLength(string.length());
        treeLocatorMetadata.saveState();
    }

    public String encode(DirNode rootNode) {
        StringBuilder sb = new StringBuilder();
        appendToStringBuilder(rootNode, sb);
        return sb.toString();
    }

    private void appendToStringBuilder(DirNode node, StringBuilder sb) {
        if (node.getDirNodes() == null && node.getFileNodes() == null) {
            return;
        }
        if (node.getFileNodes() != null) {
            for (FileNode fileNode : node.getFileNodes()) {
                sb.append(stringFromFileNode(fileNode)).append(END_LINE_TAG);
            }
            if (node.getDirNodes() == null) {
                return;
            }
        }

        for (DirNode dirNode : node.getDirNodes()) {
            sb.append(stringFromDirNode(dirNode)).append(END_LINE_TAG);
            appendToStringBuilder(dirNode, sb);
            sb.append(NO_MORE_CHILDREN_TAG).append(END_LINE_TAG);
        }
    }

    public static String stringFromDirNode(DirNode dirNode) {
        return DIR_NODE_TAG + dirNode.getName();
    }

    public static String stringFromFileNode(FileNode fileNode) {
        return FILE_NODE_TAG + fileNode.getOffset()
                + FILE_NODE_SEPARATOR + fileNode.getLength()
                + FILE_NODE_SEPARATOR + fileNode.getFileName();
    }
}
