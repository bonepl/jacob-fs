package com.jetbrains.jacobfs.tree;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Path;

public final class TreeLocatorFileBlockDecoder {

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
                    long headerOffset = byteBuffer.getLong();
                    int pathLength = byteBuffer.getInt();
                    long payloadOffset = byteBuffer.getLong();
                    int payloadLength = byteBuffer.getInt();
                    byte[] pathBytes = new byte[pathLength];
                    byteBuffer.get(pathBytes, 0, pathLength);
                    Path path = Path.of(new String(pathBytes));
                    FileNode fileNode = new FileNode(headerOffset, path.getFileName().toString(),
                            payloadOffset, payloadLength);
                    DirNode dirNode = rootNode.makeDirNodesPath(path);
                    dirNode.addFileNode(fileNode);
                }
            }
        }
        return rootNode;
    }

}
