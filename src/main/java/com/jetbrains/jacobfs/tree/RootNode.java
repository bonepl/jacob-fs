package com.jetbrains.jacobfs.tree;

public class RootNode extends DirNode {
    public static final String ROOT = "/";
    private long payloadSpaceEndOffset;

    public RootNode() {
        super(ROOT);
    }

    public long getPayloadSpaceEndOffset() {
        return payloadSpaceEndOffset;
    }

    public void setPayloadSpaceEndOffset(long payloadSpaceEndOffset) {
        this.payloadSpaceEndOffset = payloadSpaceEndOffset;
    }
}
