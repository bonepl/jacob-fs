package com.jetbrains.jacobfs.tree;

import java.io.Serializable;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DirNode implements Serializable {
    private String name;
    private Map<String, DirNode> dirNodes;
    private Map<String, FileNode> fileNodes;

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
            dirNodes = new HashMap<>(4);
        }

        Optional<DirNode> existingDirNode = getDirNodeByName(dirNode.getName());
        if (existingDirNode.isPresent()) {
            return existingDirNode.get();
        }

        dirNodes.put(dirNode.getName(), dirNode);
        return dirNode;
    }

    public Optional<DirNode> getDirNodeByName(String name) {
        if (dirNodes == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(dirNodes.get(name));
    }

    public void removeDirNodeByName(String name) throws NoSuchFileException {
        if (dirNodes.remove(name) == null) {
            throw new NoSuchFileException(name);
        }
        if (dirNodes.isEmpty()) {
            dirNodes = null;
        }
    }

    public void addFileNode(FileNode fileNode) throws FileAlreadyExistsException {
        if (fileNodes == null) {
            fileNodes = new HashMap<>(4);
        }

        Optional<FileNode> existingFileNode = getFileNodeByName(fileNode.getFileName());
        if (existingFileNode.isPresent()) {
            throw new FileAlreadyExistsException(
                    String.format("File %s already exists at this location", fileNode.getFileName()));
        }

        fileNodes.put(fileNode.getFileName(), fileNode);
    }

    public Optional<FileNode> getFileNodeByName(String name) {
        if (fileNodes == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(fileNodes.get(name));
    }

    public void removeFileNodeByName(String name) throws NoSuchFileException {
        if (fileNodes.remove(name) == null) {
            throw new NoSuchFileException(name);
        }
        if (fileNodes.isEmpty()) {
            fileNodes = null;
        }
    }

    public Map<String, DirNode> getDirNodes() {
        return dirNodes;
    }

    public Map<String, FileNode> getFileNodes() {
        return fileNodes;
    }
}
