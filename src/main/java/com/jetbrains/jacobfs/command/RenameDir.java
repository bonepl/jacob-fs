package com.jetbrains.jacobfs.command;

import com.jetbrains.jacobfs.tree.TreeLocator;

import java.io.IOException;
import java.nio.file.Path;

public class RenameDir implements VoidCommand {
    private final Path source;
    private final String newDirName;

    public RenameDir(String source, String newDirName) {
        this.source = Path.of(source);
        this.newDirName = newDirName;
    }

    @Override
    public void execute(TreeLocator treeLocator) throws IOException {
        Path destination = source.getParent().resolve(newDirName);
        new MoveDir(source, destination).execute(treeLocator);
    }
}
