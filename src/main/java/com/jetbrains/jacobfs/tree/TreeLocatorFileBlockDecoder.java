package com.jetbrains.jacobfs.tree;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.Queue;

public final class TreeLocatorFileBlockDecoder {
    private final Queue<Long> reusableOffsets = new LinkedList<>();

    public RootNode loadTreeLocatorFromFile(TreeLocatorMetadata treeLocatorMetadata) throws IOException {
        if (treeLocatorMetadata.getTreeLocatorLength() % 128 != 0) {
            throw new IOException("tree locator length should be multiplication of 128");
        }
        RootNode rootNode = new RootNode();

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(treeLocatorMetadata.getFile(), "r")) {
            for (long i = treeLocatorMetadata.getTreeLocatorOffset(); i < treeLocatorMetadata.getTreeLocatorLength(); i += 128) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(128);
                randomAccessFile.seek(i);
                randomAccessFile.read(byteBuffer.array());
                byte isActive = byteBuffer.get();
                if (isActive == 1) {
                    int pathLength = byteBuffer.getInt();
                    long payloadOffset = byteBuffer.getLong();
                    int payloadLength = byteBuffer.getInt();
                    byte[] pathBytes = new byte[pathLength];
                    byteBuffer.get(pathBytes, 0, pathLength);
                    Path path = Path.of(new String(pathBytes));
                    FileNode fileNode = new FileNode(i, path.getFileName().toString(),
                            payloadOffset, payloadLength);
                    DirNode dirNode = rootNode.makeDirNodesPath(path);
                    dirNode.addFileNode(fileNode);
                } else {
                    reusableOffsets.offer(i);
                }
            }
        }
        return rootNode;
    }

    public Queue<Long> getReusableOffsets() {
        return reusableOffsets;
    }
}
