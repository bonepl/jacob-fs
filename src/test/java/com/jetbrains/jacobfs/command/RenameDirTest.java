package com.jetbrains.jacobfs.command;

import com.jetbrains.jacobfs.AbstractJacobFSTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.NoSuchFileException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RenameDirTest extends AbstractJacobFSTest {
    @Test
    void shouldRenameDir() throws IOException {
        jacobFS.executeCommand(new WriteFile("/windows/system32/system.dll", "system"));

        jacobFS.executeCommand(new RenameDir("/windows/system32", "rootkit"));
        assertEquals("system",
                jacobFS.executeCommand(new ReadFileString("/windows/rootkit/system.dll")));
        assertThrows(NoSuchFileException.class,
                () -> jacobFS.executeCommand(new ReadFileString("/windows/system32/system.dll")));

        reopenTestContainer();

        jacobFS.executeCommand(new RenameDir("/windows/rootkit", "system32"));
        assertEquals("system", jacobFS.executeCommand(new ReadFileString("/windows/system32/system.dll")));
        assertThrows(NoSuchFileException.class,
                () -> jacobFS.executeCommand(new ReadFileString("/windows/rootkit/system.dll")));
    }
}