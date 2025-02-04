package com.jetbrains.jacobfs.command;

import com.jetbrains.jacobfs.AbstractJacobFSTest;
import com.jetbrains.jacobfs.tree.FileNode;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoveFileTest extends AbstractJacobFSTest {
    @Test
    void shouldNotAllowMovingRoot() {
        assertThrows(IllegalArgumentException.class,
                () -> jacobFS.executeCommand(new MoveFile("/", "/destination.file")));
        assertThrows(IllegalArgumentException.class,
                () -> jacobFS.executeCommand(new MoveFile("/source.file", "/")));
    }

    @Test
    void shouldNotAllowOverwrite() throws IOException {
        String testFilePath = "/windows/system32/kernel.dll";
        String movedFilePath = "/rootkit.dll";
        jacobFS.executeCommand(new WriteFile(testFilePath, "working"));
        jacobFS.executeCommand(new WriteFile(movedFilePath, "hacking"));
        assertThrows(FileAlreadyExistsException.class,
                () -> jacobFS.executeCommand(new MoveFile(movedFilePath, testFilePath)));
        assertThrows(FileAlreadyExistsException.class,
                () -> jacobFS.executeCommand(new MoveFile(testFilePath, testFilePath)));

        reopenTestContainer();

        assertThrows(FileAlreadyExistsException.class,
                () -> jacobFS.executeCommand(new MoveFile(movedFilePath, testFilePath)));
        assertThrows(FileAlreadyExistsException.class,
                () -> jacobFS.executeCommand(new MoveFile(testFilePath, testFilePath)));
    }

    @Test
    void shouldAllowRename() throws IOException {
        String sourceFilePath = "/windows/system32/rootkit.dll";
        String destinationFilePath = "/windows/system32/kernel.dll";
        jacobFS.executeCommand(new WriteFile(sourceFilePath, "system"));

        jacobFS.executeCommand(new MoveFile(sourceFilePath, destinationFilePath));
        assertEquals("system", jacobFS.executeCommand(new ReadFileString(destinationFilePath)));
        assertThrows(NoSuchFileException.class,
                () -> jacobFS.executeCommand(new ReadFileString(sourceFilePath)));

        reopenTestContainer();

        jacobFS.executeCommand(new MoveFile(destinationFilePath, sourceFilePath));
        assertEquals("system", jacobFS.executeCommand(new ReadFileString(sourceFilePath)));
        assertThrows(NoSuchFileException.class,
                () -> jacobFS.executeCommand(new ReadFileString(destinationFilePath)));
    }

    @Test
    void shouldMoveToDifferentFolder() throws IOException {
        String sourceFilePath = "/rootkit.dll";
        String destinationFilePath = "/windows/system32/kernel.dll";
        jacobFS.executeCommand(new WriteFile(sourceFilePath, "system"));

        jacobFS.executeCommand(new MoveFile(sourceFilePath, destinationFilePath));
        assertEquals("system", jacobFS.executeCommand(new ReadFileString(destinationFilePath)));
        assertThrows(NoSuchFileException.class,
                () -> jacobFS.executeCommand(new ReadFileString(sourceFilePath)));

        reopenTestContainer();

        jacobFS.executeCommand(new MoveFile(destinationFilePath, sourceFilePath));
        assertEquals("system", jacobFS.executeCommand(new ReadFileString(sourceFilePath)));
        assertThrows(NoSuchFileException.class,
                () -> jacobFS.executeCommand(new ReadFileString(destinationFilePath)));
    }

    @Test
    void shouldNotRewriteToNewOffsetWhenMoving() throws IOException {
        String sourceFilePath = "/rootkit.dll";
        String destinationFilePath = "/windows/system32/kernel.dll";
        jacobFS.executeCommand(new WriteFile(sourceFilePath, "system"));
        Long oldPayloadEndOffset = jacobFS.executeCommand(getPayloadSpaceEndOffsetCommand());
        FileNode sourceFileNode = jacobFS.executeCommand(getFileNodeCommand(sourceFilePath));
        assertEquals("rootkit.dll", sourceFileNode.getFileName());
        long sourceOffset = sourceFileNode.getOffset();
        int sourceLength = sourceFileNode.getLength();

        jacobFS.executeCommand(new MoveFile(sourceFilePath, destinationFilePath));
        FileNode destinationFileNode = jacobFS.executeCommand(getFileNodeCommand(destinationFilePath));

        assertEquals("kernel.dll", destinationFileNode.getFileName());
        assertEquals(sourceOffset, destinationFileNode.getOffset());
        assertEquals(sourceLength, destinationFileNode.getLength());
        assertEquals(oldPayloadEndOffset, jacobFS.executeCommand(getPayloadSpaceEndOffsetCommand()));
    }
}