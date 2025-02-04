package com.jetbrains.jacobfs.command;

import com.jetbrains.jacobfs.tree.FileNode;
import com.jetbrains.jacobfs.tree.PathValidator;
import com.jetbrains.jacobfs.tree.TreeLocator;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public class ReadFileBytes implements Command<byte[]> {
    private final Path path;

    public ReadFileBytes(String path) {
        this.path = Path.of(path);
    }

    @Override
    public byte[] execute(TreeLocator treeLocator) throws IOException {
        PathValidator.validatePath(path);
        FileNode fileNode = treeLocator.getFileNode(path)
                .orElseThrow(() -> new NoSuchFileException(path.toString()));
        byte[] bytes = new byte[fileNode.getLength()];
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(treeLocator.getFile(), "r")) {
            randomAccessFile.seek(fileNode.getOffset());
            randomAccessFile.read(bytes);
        }
        return bytes;
    }
}
