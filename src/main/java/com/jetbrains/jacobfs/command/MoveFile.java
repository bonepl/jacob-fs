package com.jetbrains.jacobfs.command;

import com.jetbrains.jacobfs.tree.DirNode;
import com.jetbrains.jacobfs.tree.FileNode;
import com.jetbrains.jacobfs.tree.PathValidator;
import com.jetbrains.jacobfs.tree.TreeLocator;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Optional;

public class MoveFile implements VoidCommand {
    private final Path source;
    private final Path destination;

    public MoveFile(Path source, Path destination) {
        this.source = source;
        this.destination = destination;
    }

    public MoveFile(String source, String destination) {
        this(Path.of(source), Path.of(destination));
    }

    @Override
    public void execute(TreeLocator treeLocator) throws IOException {
        PathValidator.validatePath(source);
        PathValidator.validatePath(destination);
        DirNode sourceDirNode = treeLocator.getDirNode(source)
                .orElseThrow(() -> new NoSuchFileException(String.format("File %s does not exist", source)));
        FileNode fileNode = sourceDirNode.getFileNode(source.getFileName().toString())
                .orElseThrow(() -> new NoSuchFileException(String.format("File %s does not exist", source)));
        DirNode destinationDirNode = treeLocator.addDirNodes(destination);

        String newFileName = destination.getFileName().toString();
        Optional<FileNode> destinationFile = destinationDirNode.getFileNode(newFileName);
        if (destinationFile.isPresent()) {
            throw new FileAlreadyExistsException(destination.toString());
        }

        sourceDirNode.removeFileNode(fileNode.getFileName());

        fileNode.setFileName(newFileName);
        destinationDirNode.addFileNode(fileNode);

        treeLocator.removeEmptyDirNodes(source);
        treeLocator.saveState();
    }
}
