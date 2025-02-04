package com.jetbrains.jacobfs.command;

import com.jetbrains.jacobfs.tree.PathValidator;
import com.jetbrains.jacobfs.tree.TreeLocator;

import java.io.IOException;

public class AppendFile implements VoidCommand {
    private final String path;
    private final byte[] contents;

    public AppendFile(String path, byte[] contents) {
        this.path = path;
        this.contents = contents;
    }

    public AppendFile(String path, String contents) {
        this(path, contents.getBytes());
    }

    private static byte[] combineByteArrays(byte[] first, byte[] second) {
        byte[] newContents = new byte[first.length + second.length];
        System.arraycopy(first, 0, newContents, 0, first.length);
        System.arraycopy(second, 0, newContents, first.length, second.length);
        return newContents;
    }

    @Override
    public void execute(TreeLocator treeLocator) throws IOException {
        PathValidator.validatePath(path);
        if (contents.length == 0) {
            throw new IllegalArgumentException(String.format("Nothing to append for %s", path));
        }
        byte[] currentFile = new ReadFileBytes(path).execute(treeLocator);
        new DeleteFile(path).execute(treeLocator);
        byte[] newContents = combineByteArrays(currentFile, contents);
        new WriteFile(path, newContents).execute(treeLocator);
    }

}
