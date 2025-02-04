package com.jetbrains.jacobfs.tree;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.FileAlreadyExistsException;
import java.util.Objects;
import java.util.Scanner;
import java.util.Stack;

import static com.jetbrains.jacobfs.tree.TreeLocatorWriter.NO_MORE_CHILDREN_TAG;

public final class TreeLocatorReader {

    public static RootNode loadTreeLocatorFromFile(TreeLocatorMetadata treeLocatorMetadata) throws IOException {
        byte[] bytes = new byte[treeLocatorMetadata.getTreeLocatorLength()];
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(treeLocatorMetadata.getFile(), "r")) {
            randomAccessFile.seek(treeLocatorMetadata.getTreeLocatorOffset());
            randomAccessFile.read(bytes);
        }
        return deserialize(new String(bytes));
    }

    public static RootNode deserialize(String encoded) throws FileAlreadyExistsException {
        RootNode rootNode = new RootNode();
        if (Objects.equals(NO_MORE_CHILDREN_TAG + TreeLocatorWriter.END_LINE_TAG, encoded)) {
            return rootNode;
        }

        Stack<DirNode> dirNodes = new Stack<>();

        DirNode currentDirNode = rootNode;
        Scanner scanner = new Scanner(encoded);
        while (scanner.hasNextLine()) {
            String nextToken = scanner.nextLine();
            if (nextToken.startsWith(DirNode.TAG)) {
                dirNodes.push(currentDirNode);
                currentDirNode = currentDirNode.addDirNode(DirNode.fromString(nextToken));
            } else if (nextToken.startsWith(FileNode.TAG)) {
                FileNode fileNode = FileNode.fromString(nextToken);
                if (fileNode.getEndOffset() > rootNode.getPayloadSpaceEndOffset()) {
                    rootNode.setPayloadSpaceEndOffset(fileNode.getEndOffset());
                }
                currentDirNode.addFileNode(fileNode);
            } else if (nextToken.equals(NO_MORE_CHILDREN_TAG.toString())) {
                currentDirNode = dirNodes.pop();
            } else {
                throw new RuntimeException(String.format("Corrupted file: Can't read token: %s", nextToken));
            }
        }
        return rootNode;
    }
}
