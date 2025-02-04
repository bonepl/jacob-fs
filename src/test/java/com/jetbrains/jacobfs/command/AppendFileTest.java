package com.jetbrains.jacobfs.command;

import com.jetbrains.jacobfs.AbstractJacobFSTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.NoSuchFileException;

import static org.junit.jupiter.api.Assertions.*;

class AppendFileTest extends AbstractJacobFSTest {
    @Test
    void shouldNotAppendToNonexistentFile() {
        assertThrows(NoSuchFileException.class,
                () -> jacobFS.executeCommand(new AppendFile("/im/clearly/not/here.sh", "test")));
    }

    @Test
    void shouldNotAppendZeroContents() {
        assertThrows(IllegalArgumentException.class,
                () -> jacobFS.executeCommand(new AppendFile("/im/clearly/not/here.sh", "")));
        assertThrows(IllegalArgumentException.class,
                () -> jacobFS.executeCommand(new AppendFile("/im/clearly/not/here.sh", new byte[0])));
    }

    @Test
    void shouldAppendToEmptyFile() throws IOException {
        String testFile = "/touch.me";
        String toAppend = "you have been touched";
        jacobFS.executeCommand(new TouchFile(testFile));
        assertTrue(jacobFS.executeCommand(new ReadFileString(testFile)).isEmpty());
        jacobFS.executeCommand(new AppendFile(testFile, toAppend));
        assertEquals(toAppend, jacobFS.executeCommand(new ReadFileString(testFile)));

        reopenTestContainer();
        assertEquals(toAppend, jacobFS.executeCommand(new ReadFileString(testFile)));
    }

    @Test
    void shouldAppendToFirst() throws IOException {
        String testFile1 = "/hello/heaven.txt";
        String testFile2 = "/hello/hell.txt";
        String testString = "foo";
        String appendString = "bar";
        jacobFS.executeCommand(new WriteFile(testFile1, testString));
        jacobFS.executeCommand(new WriteFile(testFile2, testString));
        jacobFS.executeCommand(new AppendFile(testFile1, appendString));
        assertEquals(testString + appendString, jacobFS.executeCommand(new ReadFileString(testFile1)));
        assertEquals(testString, jacobFS.executeCommand(new ReadFileString(testFile2)));

        reopenTestContainer();
        assertEquals(testString + appendString, jacobFS.executeCommand(new ReadFileString(testFile1)));
        assertEquals(testString, jacobFS.executeCommand(new ReadFileString(testFile2)));
    }

    @Test
    void shouldAppendToSecond() throws IOException {
        String testFile1 = "/hello/heaven.txt";
        String testFile2 = "/hello/hell.txt";
        String testString = "foo";
        String appendString = "bar";
        jacobFS.executeCommand(new WriteFile(testFile1, testString));
        jacobFS.executeCommand(new WriteFile(testFile2, testString));
        jacobFS.executeCommand(new AppendFile(testFile2, appendString));
        assertEquals(testString, jacobFS.executeCommand(new ReadFileString(testFile1)));
        assertEquals(testString + appendString, jacobFS.executeCommand(new ReadFileString(testFile2)));

        reopenTestContainer();
        assertEquals(testString, jacobFS.executeCommand(new ReadFileString(testFile1)));
        assertEquals(testString + appendString, jacobFS.executeCommand(new ReadFileString(testFile2)));
    }
}