package com.jetbrains.jacobfs.tree;

import java.io.Serializable;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class DirNode implements Serializable {
    private String name;
    private List<DirNode> dirNodes;
    private List<FileNode> fileNodes;

    public DirNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DirNode addDirNode(DirNode dirNode) {
        if (dirNodes == null) {
            dirNodes = new LinkedList<>();
        }

        Optional<DirNode> existingDirNode = getDirNodeByName(dirNode.getName());
        if (existingDirNode.isPresent()) {
            return existingDirNode.get();
        }

        dirNodes.add(dirNode);
        return dirNode;
    }

    public Optional<DirNode> getDirNodeByName(String name) {
        if (dirNodes == null) {
            return Optional.empty();
        }
        return dirNodes.stream()
                .filter(pn -> pn.getName().equals(name))
                .findAny();
    }

    public void removeDirNodeByName(String name) throws NoSuchFileException {
        DirNode dirNode = getDirNodeByName(name)
                .orElseThrow(() -> new NoSuchFileException(name));
        dirNodes.remove(dirNode);
        if (dirNodes.isEmpty()) {
            dirNodes = null;
        }
    }

    public void addFileNode(FileNode fileNode) throws FileAlreadyExistsException {
        if (fileNodes == null) {
            fileNodes = new LinkedList<>();
        }

        Optional<FileNode> existingFileNode = getFileNodeByName(fileNode.getFileName());
        if (existingFileNode.isPresent()) {
            throw new FileAlreadyExistsException(
                    String.format("File %s already exists at this location", fileNode.getFileName()));
        }

        fileNodes.add(fileNode);
    }

    public Optional<FileNode> getFileNodeByName(String name) {
        if (fileNodes == null) {
            return Optional.empty();
        }
        return fileNodes.stream()
                .filter(fn -> fn.getFileName().equals(name))
                .findAny();
    }

    public void removeFileNodeByName(String name) throws NoSuchFileException {
        FileNode fileNode = getFileNodeByName(name)
                .orElseThrow(() -> new NoSuchFileException(name));
        fileNodes.remove(fileNode);
        if (fileNodes.isEmpty()) {
            fileNodes = null;
        }
    }

    public List<DirNode> getDirNodes() {
        return dirNodes;
    }

    public List<FileNode> getFileNodes() {
        return fileNodes;
    }
}
