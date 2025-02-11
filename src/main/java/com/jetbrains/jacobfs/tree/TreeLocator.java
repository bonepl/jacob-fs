package com.jetbrains.jacobfs.tree;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class TreeLocator {

    private RootNode rootNode;
    private final TreeLocatorMetadata treeLocatorMetadata;
    private final TreeLocatorFileBlockEncoder treeLocatorEncoder = new TreeLocatorFileBlockEncoder();
    private final TreeLocatorFileBlockDecoder treeLocatorDecoder = new TreeLocatorFileBlockDecoder();

    public TreeLocator(Path path) throws IOException {
        this.treeLocatorMetadata = new TreeLocatorMetadata(path);
        if (treeLocatorMetadata.getTreeLocatorLength() == 0) {
            initializeContainer();
        } else {
            loadContainer();
        }
    }

    private void initializeContainer() {
        rootNode = new RootNode();
        rootNode.setPayloadSpaceEndOffset(treeLocatorMetadata.getPayloadSpaceOffset());
    }

    private void loadContainer() throws IOException {
        rootNode = treeLocatorDecoder.loadTreeLocatorFromFile(treeLocatorMetadata);
    }

    /**
     * add all dirs on path, return top dir, new or existing
     */
    public DirNode makeDirNodesPath(Path filePath) {
        return rootNode.makeDirNodesPath(filePath);
    }

    /**
     * get top dir node from path
     */
    public Optional<DirNode> getDirNode(Path filePath) {
        return getDirNodesPath(filePath).map(List::getLast);
    }

    /**
     * get path to top dir node as list
     */
    public Optional<List<DirNode>> getDirNodesPath(Path filePath) {
        List<DirNode> dirNodes = new LinkedList<>();
        DirNode currentNode = rootNode;
        dirNodes.add(rootNode);
        for (Path path : filePath.getParent()) {
            Optional<DirNode> dirNode = currentNode.getDirNodeByName(path.toString());
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
    public void removeEmptyDirNodesFromPath(Path path) throws NoSuchFileException {
        List<DirNode> dirNodes = getDirNodesPath(path)
                .orElseThrow(() -> new NoSuchFileException(path.toString()));
        for (int i = dirNodes.size() - 1; i > 0; i--) {
            DirNode dirNode = dirNodes.get(i);
            if (dirNode.getFileNodes() == null && dirNode.getDirNodes() == null) {
                dirNodes.get(i - 1).removeDirNodeByName(dirNode.getName());
            } else {
                return;
            }
        }
    }

    /**
     * returns file node for a path
     */
    public Optional<FileNode> getFileNode(Path filePath) {
        return getDirNode(filePath)
                .flatMap(dn -> dn.getFileNodeByName(filePath.getFileName().toString()));
    }

    public RootNode getRootNode() {
        return rootNode;
    }

    public void saveFileNodeToFile(Path path, FileNode fileNode) throws IOException {
        treeLocatorEncoder.saveFileNodeToFile(path, fileNode, treeLocatorMetadata);
    }

    public void removeFileNodeFromFile(FileNode fileNode) throws IOException {
        treeLocatorEncoder.removeFileNodeFromFile(fileNode, treeLocatorMetadata);
    }

    public File getFile() {
        return treeLocatorMetadata.getFile();
    }
}
