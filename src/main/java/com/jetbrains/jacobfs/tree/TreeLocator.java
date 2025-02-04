package com.jetbrains.jacobfs.tree;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class TreeLocator {

    private RootNode rootNode;
    private TreeLocatorMetadata treeLocatorMetadata;

    private TreeLocator() {
    }

    public static TreeLocator init(Path path) throws IOException {
        TreeLocator treeLocator = new TreeLocator();
        if (path.toFile().exists()) {
            throw new FileAlreadyExistsException(String.format("Container already exists: " + path.toFile().getAbsolutePath()));
        }
        treeLocator.treeLocatorMetadata = TreeLocatorMetadata.init(path);
        treeLocator.rootNode = new RootNode();
        treeLocator.rootNode.setPayloadSpaceEndOffset(treeLocator.treeLocatorMetadata.getPayloadSpaceOffset());
        treeLocator.saveState();
        return treeLocator;
    }

    public static TreeLocator load(Path path) throws IOException {
        TreeLocator treeLocator = new TreeLocator();
        if (!path.toFile().exists()) {
            throw new NoSuchFileException(String.format("Container file does not exist: " + path.toFile().getAbsolutePath()));
        }
        treeLocator.treeLocatorMetadata = TreeLocatorMetadata.load(path);
        treeLocator.rootNode = TreeLocatorReader.loadTreeLocatorFromFile(treeLocator.treeLocatorMetadata);
        return treeLocator;
    }

    /**
     * add all dirs on path, return top dir, new or existing
     */
    public DirNode addDirNodes(Path filePath) {
        DirNode currentNode = rootNode;
        if (filePath.getParent() != null) {
            for (Path path : filePath.getParent()) {
                currentNode = currentNode.addDirNode(new DirNode(path.toString()));
            }
        }

        return currentNode;
    }

    /**
     * get top dir node from path
     */
    public Optional<DirNode> getDirNode(Path filePath) {
        return getDirNodes(filePath).map(List::getLast);
    }

    /**
     * get path to top dir node as list
     */
    public Optional<List<DirNode>> getDirNodes(Path filePath) {
        List<DirNode> dirNodes = new LinkedList<>();
        DirNode currentNode = rootNode;
        dirNodes.add(rootNode);
        for (Path path : filePath.getParent()) {
            Optional<DirNode> dirNode = currentNode.getDirNode(path.toString());
            if (dirNode.isPresent()) {
                currentNode = dirNode.get();
                dirNodes.add(currentNode);
            } else {
                return Optional.empty();
            }
        }
        return Optional.of(dirNodes);
    }

    /**
     * clean up empty directories on path
     */
    public void removeEmptyDirNodes(Path path) throws NoSuchFileException {
        List<DirNode> dirNodes = getDirNodes(path)
                .orElseThrow(() -> new NoSuchFileException(path.toString()));
        for (int i = dirNodes.size() - 1; i > 0; i--) {
            DirNode dirNode = dirNodes.get(i);
            if (dirNode.getFileNodes() == null && dirNode.getDirNodes() == null) {
                dirNodes.get(i - 1).removeDirNode(dirNode.getName());
            }
        }
    }

    /**
     * returns file node for a path
     */
    public Optional<FileNode> getFileNode(Path filePath) {
        return getDirNode(filePath)
                .flatMap(dn -> dn.getFileNode(filePath.getFileName().toString()));
    }

    public RootNode getRootNode() {
        return rootNode;
    }

    public void saveState() throws IOException {
        TreeLocatorWriter.saveTreeLocatorToFile(rootNode, treeLocatorMetadata);
    }

    public File getFile() {
        return treeLocatorMetadata.getFile();
    }
}
