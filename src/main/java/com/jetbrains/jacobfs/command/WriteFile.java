package com.jetbrains.jacobfs.command;

import com.jetbrains.jacobfs.tree.*;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

public class WriteFile implements VoidCommand {
    public static long makeDirTimes = 0L;
    public static long addFileTime = 0L;
    public static long persistTreeTimes = 0L;
    public static long lastMakeDirTime = 0L;
    public static long lastAddFileTime = 0L;
    public static long lastPersistTreeTime = 0L;
    public static long timer;
    public static long counter = 0;
    private final Path path;
    private final byte[] contents;

    public WriteFile(String path, byte[] contents) {
        this.path = Path.of(path);
        this.contents = contents;
    }

    public WriteFile(String path, String contents) {
        this(path, contents.getBytes());
    }

    @Override
    public void execute(TreeLocator treeLocator) throws IOException {
        counter++;
        PathValidator.validatePath(path);
        RootNode rootNode = treeLocator.getRootNode();
        FileNode fileNode = new FileNode(path.getFileName().toString(),
                rootNode.getPayloadSpaceEndOffset(), contents.length);
        rootNode.setPayloadSpaceEndOffset(fileNode.getEndOffset());
        timer = System.nanoTime();
        DirNode dirNode = treeLocator.makeDirNodesPath(path);
        lastMakeDirTime = System.nanoTime() - timer;
        if (counter > 10) {
            makeDirTimes += lastMakeDirTime;
        }
        timer = System.nanoTime();
        dirNode.addFileNode(fileNode);
        lastAddFileTime = System.nanoTime() - timer;
        if (counter > 10) {
            addFileTime += lastAddFileTime;
        }

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(treeLocator.getFile(), "rw")) {
            randomAccessFile.seek(fileNode.getPayloadOffset());
            randomAccessFile.write(contents);
        }
        timer = System.nanoTime();
        treeLocator.saveFileNodeToFile(path, fileNode);

        lastPersistTreeTime = System.nanoTime() - timer;
        if (counter > 10) {
            persistTreeTimes += lastPersistTreeTime;
        }

        if (counter % 500 == 0) {
            System.out.println("Sample make dirs/add file/persist tree: " + lastMakeDirTime + " " + lastAddFileTime + " " + lastPersistTreeTime + "ns");
        }
    }
}
