package com.jetbrains.jacobfs.tree;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.FileAlreadyExistsException;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.jetbrains.jacobfs.tree.TreeLocatorEncoder.*;

public final class TreeLocatorDecoder {
    public static final Pattern DIR_NODE_PATTERN
            = Pattern.compile(DIR_NODE_TAG + "([^\\n]+)\\n?");
    public static final Pattern FILE_NODE_PATTERN
            = Pattern.compile(FILE_NODE_TAG + "(\\d+):(\\d+):([^\\n]+)\\n?");

    public RootNode loadTreeLocatorFromFile(TreeLocatorMetadata treeLocatorMetadata) throws IOException {
        byte[] bytes = new byte[treeLocatorMetadata.getTreeLocatorLength()];
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(treeLocatorMetadata.getFile(), "r")) {
            randomAccessFile.seek(treeLocatorMetadata.getTreeLocatorOffset());
            randomAccessFile.read(bytes);
        }
        return decode(new String(bytes));
    }

    public RootNode decode(String encoded) throws FileAlreadyExistsException {
        RootNode rootNode = new RootNode();
        if (encoded.isBlank()) {
            return rootNode;
        }

        Stack<DirNode> dirNodes = new Stack<>();

        DirNode currentDirNode = rootNode;
        Scanner scanner = new Scanner(encoded);
        while (scanner.hasNextLine()) {
            String nextToken = scanner.nextLine();
            if (nextToken.startsWith(DIR_NODE_TAG)) {
                dirNodes.push(currentDirNode);
                currentDirNode = currentDirNode.addDirNode(dirNodeFromString(nextToken));
            } else if (nextToken.startsWith(FILE_NODE_TAG)) {
                FileNode fileNode = fileNodeFromString(nextToken);
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

    public static DirNode dirNodeFromString(String encoded) {
        Matcher matcher = DIR_NODE_PATTERN.matcher(encoded);
        if (matcher.find()) {
            return new DirNode(matcher.group(1));
        }
        throw new RuntimeException(String.format("Improper DirNode string format: %s", encoded));
    }

    public static FileNode fileNodeFromString(String encoded) {
        Matcher matcher = FILE_NODE_PATTERN.matcher(encoded);
        if (matcher.find()) {
            return new FileNode(matcher.group(3),
                    Long.parseLong(matcher.group(1)), Integer.parseInt(matcher.group(2)));
        }
        throw new RuntimeException(String.format("Improper FileNode string format: %s", encoded));
    }
}
