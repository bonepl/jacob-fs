package com.jetbrains.jacobfs.command;

import com.jetbrains.jacobfs.AbstractJacobFSTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TouchFileTest extends AbstractJacobFSTest {
    @Test
    void shouldNotTouchTheSameFile() throws IOException {
        String testFilePath = "/file.txt";
        jacobFS.executeCommand(new TouchFile(testFilePath));
        assertThrows(FileAlreadyExistsException.class, () -> jacobFS.executeCommand(new TouchFile(testFilePath)));
    }

    @Test
    void shouldTouchFiles() throws IOException {
        List<String> testFiles =
                List.of("/test/file.txt", "/test/file2.txt", "/file3.txt");

        for (String testFile : testFiles) {
            jacobFS.executeCommand(new TouchFile(testFile));
            assertTrue(jacobFS.executeCommand(new ReadFileString(testFile)).isEmpty());
            assertArrayEquals(new byte[0], jacobFS.executeCommand(new ReadFileBytes(testFile)));
        }

        reopenTestContainer();
        for (String testFile : testFiles) {
            assertTrue(jacobFS.executeCommand(new ReadFileString(testFile)).isEmpty());
            assertArrayEquals(new byte[0], jacobFS.executeCommand(new ReadFileBytes(testFile)));
        }
    }
}