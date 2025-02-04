package com.jetbrains.jacobfs.command;

import com.jetbrains.jacobfs.tree.TreeLocator;

import java.io.IOException;
import java.nio.file.Path;

public class RenameFile implements VoidCommand {
    private final Path source;
    private final String newFileName;

    public RenameFile(String source, String newFileName) {
        this.source = Path.of(source);
        this.newFileName = newFileName;
    }

    @Override
    public void execute(TreeLocator treeLocator) throws IOException {
        Path destination = source.getParent().resolve(newFileName);
        new MoveFile(source, destination).execute(treeLocator);
    }
}
