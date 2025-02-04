package com.jetbrains.jacobfs.command;

import com.jetbrains.jacobfs.AbstractJacobFSTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoveDirTest extends AbstractJacobFSTest {
    @Test
    void shouldNotAllowMovingRoot() {
        assertThrows(IllegalArgumentException.class,
                () -> jacobFS.executeCommand(new MoveDir("/", "/destination")));
        assertThrows(IllegalArgumentException.class,
                () -> jacobFS.executeCommand(new MoveDir("/source", "/")));
    }

    @Test
    void shouldNotAllowOverwrite() throws IOException {
        String testDirPath = "/windows/system32/kernel";
        String movedDirPath = "/rootkit";
        String fileName = "/system.dll";
        jacobFS.executeCommand(new WriteFile(testDirPath + fileName, "working"));
        jacobFS.executeCommand(new WriteFile(movedDirPath + fileName, "hacking"));
        assertThrows(FileAlreadyExistsException.class,
                () -> jacobFS.executeCommand(new MoveDir(movedDirPath, testDirPath)));
        assertThrows(FileAlreadyExistsException.class,
                () -> jacobFS.executeCommand(new MoveDir(testDirPath, testDirPath)));

        reopenTestContainer();

        assertThrows(FileAlreadyExistsException.class,
                () -> jacobFS.executeCommand(new MoveDir(movedDirPath, testDirPath)));
        assertThrows(FileAlreadyExistsException.class,
                () -> jacobFS.executeCommand(new MoveDir(testDirPath, testDirPath)));
    }

    @Test
    void shouldAllowRename() throws IOException {
        String sourceDirPath = "/windows/system32/rootkit";
        String destinationDirPath = "/windows/system32/kernel";
        String fileName = "/system.dll";

        jacobFS.executeCommand(new WriteFile(sourceDirPath + fileName, "system"));

        jacobFS.executeCommand(new MoveDir(sourceDirPath, destinationDirPath));
        assertEquals("system", jacobFS.executeCommand(new ReadFileString(destinationDirPath + fileName)));
        assertThrows(NoSuchFileException.class,
                () -> jacobFS.executeCommand(new ReadFileString(sourceDirPath)));

        reopenTestContainer();

        jacobFS.executeCommand(new MoveDir(destinationDirPath, sourceDirPath));
        assertEquals("system", jacobFS.executeCommand(new ReadFileString(sourceDirPath + fileName)));
        assertThrows(NoSuchFileException.class,
                () -> jacobFS.executeCommand(new ReadFileString(destinationDirPath)));
    }

    @Test
    void shouldMoveToDifferentFolder() throws IOException {
        String sourceFilePath = "/rootkit";
        String destinationFilePath = "/windows/system32/kernel";
        String fileName = "/system.dll";

        jacobFS.executeCommand(new WriteFile(sourceFilePath + fileName, "system"));

        jacobFS.executeCommand(new MoveDir(sourceFilePath, destinationFilePath));
        assertEquals("system", jacobFS.executeCommand(new ReadFileString(destinationFilePath + fileName)));
        assertThrows(NoSuchFileException.class,
                () -> jacobFS.executeCommand(new ReadFileString(sourceFilePath + fileName)));

        reopenTestContainer();

        jacobFS.executeCommand(new MoveDir(destinationFilePath, sourceFilePath));
        assertEquals("system", jacobFS.executeCommand(new ReadFileString(sourceFilePath + fileName)));
        assertThrows(NoSuchFileException.class,
                () -> jacobFS.executeCommand(new ReadFileString(destinationFilePath + fileName)));
    }

    @Test
    void shouldNotAffectPayloadOffset() throws IOException {
        String sourceFilePath = "/rootkit";
        String destinationFilePath = "/windows/system32/kernel";
        String fileName = "/system.dll";

        jacobFS.executeCommand(new WriteFile(sourceFilePath + fileName, "system"));
        Long oldPayloadEndOffset = jacobFS.executeCommand(getPayloadSpaceEndOffsetCommand());

        jacobFS.executeCommand(new MoveDir(sourceFilePath, destinationFilePath));
        assertEquals(oldPayloadEndOffset, jacobFS.executeCommand(getPayloadSpaceEndOffsetCommand()));
    }
}