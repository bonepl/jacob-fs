package com.jetbrains.jacobfs.tree;

import java.io.IOException;
import java.io.RandomAccessFile;

public final class TreeLocatorWriter {
    public static final Character NO_MORE_CHILDREN_TAG = '/';
    public static final String END_LINE_TAG = "\n";

    public static void saveTreeLocatorToFile(RootNode rootNode, TreeLocatorMetadata treeLocatorMetadata) throws IOException {
        String string = serialize(rootNode);
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(treeLocatorMetadata.getFile(), "rw")) {
            randomAccessFile.seek(treeLocatorMetadata.getTreeLocatorOffset());
            randomAccessFile.writeBytes(string);
        }
        treeLocatorMetadata.setTreeLocatorLength(string.length());
        treeLocatorMetadata.saveState();
    }

    public static String serialize(DirNode rootNode) {
        StringBuilder sb = new StringBuilder();
        appendToStringBuilder(rootNode, sb);
        return sb.toString();
    }

    private static void appendToStringBuilder(DirNode node, StringBuilder sb) {
        if (node.getDirNodes() == null && node.getFileNodes() == null) {
            sb.append(NO_MORE_CHILDREN_TAG).append(END_LINE_TAG);
            return;
        }
        if (node.getFileNodes() != null) {
            for (FileNode fileNode : node.getFileNodes()) {
                sb.append(fileNode.toString()).append(END_LINE_TAG);
            }
            if (node.getDirNodes() == null) {
                return;
            }
        }

        for (DirNode dirNode : node.getDirNodes()) {
            sb.append(dirNode.toString()).append(END_LINE_TAG);
            appendToStringBuilder(dirNode, sb);
            sb.append(NO_MORE_CHILDREN_TAG).append(END_LINE_TAG);
        }
    }


}
