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
    private TreeLocatorMetadata treeLocatorMetadata;
    private final TreeLocatorDecoder treeLocatorDecoder;
    private final TreeLocatorEncoder treeLocatorEncoder;

    public TreeLocator(Path path) throws IOException {
        this(path, new TreeLocatorEncoder(), new TreeLocatorDecoder());
    }

    public TreeLocator(Path path, TreeLocatorEncoder treeLocatorEncoder, TreeLocatorDecoder treeLocatorDecoder) throws IOException {
        this.treeLocatorEncoder = treeLocatorEncoder;
        this.treeLocatorDecoder = treeLocatorDecoder;
        if (path.toFile().exists()) {
            loadContainer(path);
        } else {
            createNewContainer(path);
        }
    }

    private void createNewContainer(Path path) throws IOException {
        treeLocatorMetadata = TreeLocatorMetadata.init(path);
        rootNode = new RootNode();
        rootNode.setPayloadSpaceEndOffset(treeLocatorMetadata.getPayloadSpaceOffset());
        saveState();
    }

    public void loadContainer(Path path) throws IOException {
        treeLocatorMetadata = TreeLocatorMetadata.load(path);
        rootNode = treeLocatorDecoder.loadTreeLocatorFromFile(treeLocatorMetadata);
    }

    /**
     * add all dirs on path, return top dir, new or existing
     */
    public DirNode makeDirNodesPath(Path filePath) {
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

    public void saveState() throws IOException {
        treeLocatorEncoder.saveTreeLocatorToFile(rootNode, treeLocatorMetadata);
    }

    public File getFile() {
        return treeLocatorMetadata.getFile();
    }
}
