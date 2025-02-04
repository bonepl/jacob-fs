package com.jetbrains.jacobfs.command;

import com.jetbrains.jacobfs.AbstractJacobFSTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.NoSuchFileException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeleteFileTest extends AbstractJacobFSTest {
    @Test
    void shouldNotDeleteNonexistentFile() {
        assertThrows(NoSuchFileException.class,
                () -> jacobFS.executeCommand(new DeleteFile("/im/not/here.dot")));
    }

    @Test
    void shouldDeleteFileWithoutReloads() throws IOException {
        shouldDeleteFiles(false);
    }

    @Test
    void shouldDeleteFileWithReloads() throws IOException {
        shouldDeleteFiles(true);
    }

    private void shouldDeleteFiles(boolean reloadContainer) throws IOException {
        String testFile1 = "/test/file.txt";
        String testFile1Contents = "test";
        String testFile2 = "/file3.txt";
        jacobFS.executeCommand(new TouchFile(testFile2));
        jacobFS.executeCommand(new WriteFile(testFile1, testFile1Contents));
        assertEquals(2, jacobFS.executeCommand(new ListAllFiles()).size());
        assertEquals(testFile1Contents, jacobFS.executeCommand(new ReadFileString(testFile1)));
        assertEquals(0, jacobFS.executeCommand(new ReadFileBytes(testFile2)).length);

        jacobFS.executeCommand(new DeleteFile(testFile1));
        if (reloadContainer) {
            reopenTestContainer();
        }

        assertEquals(1, jacobFS.executeCommand(new ListAllFiles()).size());
        assertThrows(NoSuchFileException.class, () -> jacobFS.executeCommand(new ReadFileString(testFile1)));
        assertEquals(0, jacobFS.executeCommand(new ReadFileBytes(testFile2)).length);

        jacobFS.executeCommand(new DeleteFile(testFile2));
        if (reloadContainer) {
            reopenTestContainer();
        }

        assertEquals(0, jacobFS.executeCommand(new ListAllFiles()).size());
        assertThrows(NoSuchFileException.class, () -> jacobFS.executeCommand(new ReadFileString(testFile1)));
        assertThrows(NoSuchFileException.class, () -> jacobFS.executeCommand(new ReadFileString(testFile2)));
    }
}