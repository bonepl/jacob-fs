package com.jetbrains.jacobfs.command;

import com.jetbrains.jacobfs.tree.DirNode;
import com.jetbrains.jacobfs.tree.FileNode;
import com.jetbrains.jacobfs.tree.PathValidator;
import com.jetbrains.jacobfs.tree.TreeLocator;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public class DeleteFile implements VoidCommand {
    private final Path path;

    public DeleteFile(String path) {
        this.path = Path.of(path);
    }

    @Override
    public void execute(TreeLocator treeLocator) throws IOException {
        PathValidator.validatePath(path);
        DirNode dirNode = treeLocator.getDirNode(path)
                .orElseThrow(() -> new NoSuchFileException(path.toString()));
        FileNode fileNode = dirNode.getFileNodeByName(path.getFileName().toString())
                .orElseThrow(() -> new NoSuchFileException(path.toString()));
        dirNode.removeFileNodeByName(fileNode.getFileName());
        treeLocator.removeEmptyDirNodesFromPath(path);
        treeLocator.saveState();
    }
}
