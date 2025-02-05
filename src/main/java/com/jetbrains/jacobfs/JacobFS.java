package com.jetbrains.jacobfs;

import com.jetbrains.jacobfs.command.Command;
import com.jetbrains.jacobfs.command.VoidCommand;
import com.jetbrains.jacobfs.tree.TreeLocator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JacobFS {
    private TreeLocator treeLocator;

    public <T> T executeCommand(Command<T> command) throws IOException {
        requireOpenContainer();
        return command.execute(treeLocator);
    }

    public void executeCommand(VoidCommand command) throws IOException {
        requireOpenContainer();
        command.execute(treeLocator);
    }

    public void openContainer(String path) throws IOException {
        treeLocator = new TreeLocator(Path.of(path));
    }

    public void closeContainer() {
        treeLocator = null;
    }

    public void deleteContainer(String path) throws IOException {
        Files.deleteIfExists(Path.of(path));
    }

    private void requireOpenContainer() {
        if (treeLocator == null) {
            throw new IllegalStateException("Container needs to be open to run commands");
        }
    }
}

