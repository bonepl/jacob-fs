package com.jetbrains.jacobfs.command;

import com.jetbrains.jacobfs.tree.*;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

public class WriteFile implements VoidCommand {
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
        PathValidator.validatePath(path);
        RootNode rootNode = treeLocator.getRootNode();
        FileNode fileNode = new FileNode(path.getFileName().toString(),
                rootNode.getPayloadSpaceEndOffset(), contents.length);
        rootNode.setPayloadSpaceEndOffset(fileNode.getEndOffset());

        DirNode dirNode = treeLocator.makeDirNodesPath(path);
        dirNode.addFileNode(fileNode);

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(treeLocator.getFile(), "rw")) {
            randomAccessFile.seek(fileNode.getOffset());
            randomAccessFile.write(contents);
        }
        treeLocator.saveState();
    }
}
