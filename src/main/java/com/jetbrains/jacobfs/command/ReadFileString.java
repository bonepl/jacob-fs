package com.jetbrains.jacobfs.command;

import com.jetbrains.jacobfs.tree.TreeLocator;

import java.io.IOException;

public class ReadFileString implements Command<String> {
    private final String path;

    public ReadFileString(String path) {
        this.path = path;
    }

    @Override
    public String execute(TreeLocator treeLocator) throws IOException {
        return new String(new ReadFileBytes(path).execute(treeLocator));
    }
}
