package com.jetbrains.jacobfs;

import com.jetbrains.jacobfs.command.Command;
import com.jetbrains.jacobfs.tree.FileNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.nio.file.Path;

public abstract class AbstractJacobFSTest {
    public final static String TEST_CNT_PATH = "./test-container.jfs";
    public final JacobFS jacobFS = new JacobFS();

    public static Command<FileNode> getFileNodeCommand(String sourceFilePath) {
        return treeLocator -> treeLocator.getFileNode(Path.of(sourceFilePath))
                .orElseThrow(() -> new RuntimeException(String.format("Couldn't get file %s for test", sourceFilePath)));
    }

    public static Command<Long> getPayloadSpaceEndOffsetCommand() {
        return treeLocator -> treeLocator.getRootNode().getPayloadSpaceEndOffset();
    }

    @BeforeEach
    protected void setUp() throws IOException {
        jacobFS.deleteContainer(TEST_CNT_PATH);
        jacobFS.openContainer(TEST_CNT_PATH);
    }

    protected void reopenTestContainer() throws IOException {
        jacobFS.openContainer(TEST_CNT_PATH);
    }

    @AfterEach
    protected void tearDown() throws IOException {
        jacobFS.closeContainer();
        jacobFS.deleteContainer(TEST_CNT_PATH);
    }
}
