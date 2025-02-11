package com.jetbrains.jacobfs.command;

import com.jetbrains.jacobfs.tree.DirNode;
import com.jetbrains.jacobfs.tree.FileNode;
import com.jetbrains.jacobfs.tree.PathValidator;
import com.jetbrains.jacobfs.tree.TreeLocator;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

public class MoveDir implements VoidCommand {
    private final Path source;
    private final Path destination;

    public MoveDir(Path source, Path destination) {
        this.source = source;
        this.destination = destination;
    }

    public MoveDir(String source, String destination) {
        this(Path.of(source), Path.of(destination));
    }

    @Override
    public void execute(TreeLocator treeLocator) throws IOException {
        PathValidator.validatePath(source);
        PathValidator.validatePath(destination);
        DirNode sourceParentDirNode = treeLocator.getDirNode(source)
                .orElseThrow(() -> new NoSuchFileException(String.format("Directory %s does not exist", source)));
        DirNode movedDirNode = sourceParentDirNode.getDirNodeByName(source.getFileName().toString())
                .orElseThrow(() -> new NoSuchFileException(String.format("Directory %s does not exist", source)));
        DirNode destinationDirNode = treeLocator.makeDirNodesPath(destination);

        String newDirName = destination.getFileName().toString();
        Optional<DirNode> existingDestinationDirNode = destinationDirNode.getDirNodeByName(newDirName);
        if (existingDestinationDirNode.isPresent()) {
            throw new FileAlreadyExistsException(destination.toString());
        }

        sourceParentDirNode.removeDirNodeByName(movedDirNode.getName());

        movedDirNode.setName(newDirName);
        destinationDirNode.addDirNode(movedDirNode);

        treeLocator.removeEmptyDirNodesFromPath(source);
        updateFilesRecursively(destination, movedDirNode, treeLocator);
    }

    public void updateFilesRecursively(Path destinationPath, DirNode dirNode, TreeLocator treeLocator) throws IOException {
        if (Objects.nonNull(dirNode.getFileNodes())) {
            for (FileNode fn : dirNode.getFileNodes().values()) {
                treeLocator.saveFileNodeToFile(destinationPath.resolve(fn.getFileName()), fn);
            }
        }
        if (Objects.nonNull(dirNode.getDirNodes())) {
            for (DirNode dn : dirNode.getDirNodes().values()) {
                Path newPath = destinationPath.resolve(dn.getName());
                updateFilesRecursively(newPath, dn, treeLocator);
            }
        }
    }
}
