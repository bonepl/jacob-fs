package com.jetbrains.jacobfs.command;

import com.jetbrains.jacobfs.AbstractJacobFSTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WriteFileTest extends AbstractJacobFSTest {
    @Test
    void shouldNotAllowWriteToRoot() {
        assertThrows(IllegalArgumentException.class,
                () -> jacobFS.executeCommand(new WriteFile("/", "xyz")));
    }

    @Test
    void shouldNotAllowSameFile() throws IOException {
        String testFilePath = "/test/file.txt";
        jacobFS.executeCommand(new WriteFile(testFilePath, "abc"));
        assertThrows(FileAlreadyExistsException.class, () -> jacobFS.executeCommand(new WriteFile(testFilePath, "xyz")));
    }

    @Test
    void shouldWriteStrings() throws IOException {
        Map<String, String> testFiles = Map.of("/test/file.txt", "file",
                "/test/file2.txt", "file2",
                "/file3.txt", "file3");

        for (Map.Entry<String, String> entry : testFiles.entrySet()) {
            jacobFS.executeCommand(new WriteFile(entry.getKey(), entry.getValue()));
        }
        for (Map.Entry<String, String> entry : testFiles.entrySet()) {
            assertEquals(entry.getValue(), jacobFS.executeCommand(new ReadFileString(entry.getKey())));
        }

        reopenTestContainer();
        for (Map.Entry<String, String> entry : testFiles.entrySet()) {
            assertEquals(entry.getValue(), jacobFS.executeCommand(new ReadFileString(entry.getKey())));
        }
    }

    @Test
    void shouldWriteBytes() throws IOException {
        Map<String, byte[]> testFiles = Map.of("/test/file.bin", "file".getBytes(),
                "/file2.bin", "file2".getBytes(),
                "/file3", "file3".getBytes());

        for (Map.Entry<String, byte[]> entry : testFiles.entrySet()) {
            jacobFS.executeCommand(new WriteFile(entry.getKey(), entry.getValue()));
        }
        for (Map.Entry<String, byte[]> entry : testFiles.entrySet()) {
            assertArrayEquals(entry.getValue(), jacobFS.executeCommand(new ReadFileBytes(entry.getKey())));
        }

        jacobFS.closeContainer();
        reopenTestContainer();
        for (Map.Entry<String, byte[]> entry : testFiles.entrySet()) {
            assertArrayEquals(entry.getValue(), jacobFS.executeCommand(new ReadFileBytes(entry.getKey())));
        }
    }
}