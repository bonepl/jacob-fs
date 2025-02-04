package com.jetbrains.jacobfs.command;

import com.jetbrains.jacobfs.tree.TreeLocator;

import java.io.IOException;

@FunctionalInterface
public interface Command<T> {
    T execute(TreeLocator treeLocator) throws IOException;
}
