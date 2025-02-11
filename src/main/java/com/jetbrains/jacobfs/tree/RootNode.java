package com.jetbrains.jacobfs.tree;

import java.io.Serializable;
import java.nio.file.Path;

public class RootNode extends DirNode implements Serializable {
    public static final String ROOT = "/";
    private long payloadSpaceEndOffset;

    public RootNode() {
        super(ROOT);
    }

    /**
     * add all dirs on path, return top dir, new or existing
     */
    public DirNode makeDirNodesPath(Path filePath) {
        DirNode currentNode = this;
        if (filePath.getParent() != null) {
            for (Path path : filePath.getParent()) {
                currentNode = currentNode.addDirNode(new DirNode(path.toString()));
            }
        }

        return currentNode;
    }

    public long getPayloadSpaceEndOffset() {
        return payloadSpaceEndOffset;
    }

    public void setPayloadSpaceEndOffset(long payloadSpaceEndOffset) {
        this.payloadSpaceEndOffset = payloadSpaceEndOffset;
    }
}
