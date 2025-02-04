package com.jetbrains.jacobfs.command;

import com.jetbrains.jacobfs.tree.TreeLocator;

import java.io.IOException;

@FunctionalInterface
public interface VoidCommand {
    void execute(TreeLocator treeLocator) throws IOException;
}
