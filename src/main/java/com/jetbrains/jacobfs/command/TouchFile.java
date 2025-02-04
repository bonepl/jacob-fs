package com.jetbrains.jacobfs.command;

import com.jetbrains.jacobfs.tree.TreeLocator;

import java.io.IOException;

public class TouchFile implements VoidCommand {
    private final String path;

    public TouchFile(String path) {
        this.path = path;
    }

    @Override
    public void execute(TreeLocator treeLocator) throws IOException {
        new WriteFile(path, new byte[0]).execute(treeLocator);
    }
}
