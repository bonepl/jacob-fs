package com.jetbrains.jacobfs.tree;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DirNode {
    public static final String TAG = "d";
    public static final Pattern DIR_NODE_PATTERN
            = Pattern.compile(TAG + "([^\\n]+)\\n?");
    private String name;
    private List<DirNode> dirNodes;
    private List<FileNode> fileNodes;

    public DirNode(String name) {
        this.name = name;
    }

    public static DirNode fromString(String encoded) {
        Matcher matcher = DIR_NODE_PATTERN.matcher(encoded);
        if (matcher.find()) {
            return new DirNode(matcher.group(1));
        }
        throw new RuntimeException(String.format("Improper DirNode string format: %s", encoded));
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

        Optional<DirNode> existingDirNode = getDirNode(dirNode.getName());
        if (existingDirNode.isPresent()) {
            return existingDirNode.get();
        }

        dirNodes.add(dirNode);
        return dirNode;
    }

    public Optional<DirNode> getDirNode(String dirName) {
        if (dirNodes == null) {
            return Optional.empty();
        }
        return dirNodes.stream()
                .filter(pn -> pn.getName().equals(dirName))
                .findAny();
    }

    public void removeDirNode(String dirName) throws NoSuchFileException {
        DirNode dirNode = getDirNode(dirName)
                .orElseThrow(() -> new NoSuchFileException(dirName));
        dirNodes.remove(dirNode);
        if (dirNodes.isEmpty()) {
            dirNodes = null;
        }
    }

    public void addFileNode(FileNode fileNode) throws FileAlreadyExistsException {
        if (fileNodes == null) {
            fileNodes = new LinkedList<>();
        }

        Optional<FileNode> existingFileNode = getFileNode(fileNode.getFileName());
        if (existingFileNode.isPresent()) {
            throw new FileAlreadyExistsException(
                    String.format("File %s already exists at this location", fileNode.getFileName()));
        }

        fileNodes.add(fileNode);
    }

    public Optional<FileNode> getFileNode(String fileName) {
        if (fileNodes == null) {
            return Optional.empty();
        }
        return fileNodes.stream()
                .filter(fn -> fn.getFileName().equals(fileName))
                .findAny();
    }

    public void removeFileNode(String fileName) throws NoSuchFileException {
        FileNode fileNode = getFileNode(fileName)
                .orElseThrow(() -> new NoSuchFileException(fileName));
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

    @Override
    public String toString() {
        return TAG + name;
    }
}
