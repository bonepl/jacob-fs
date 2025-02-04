package com.jetbrains.jacobfs.command;

import com.jetbrains.jacobfs.AbstractJacobFSTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.NoSuchFileException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RenameFileTest extends AbstractJacobFSTest {
    @Test
    void shouldRenameFile() throws IOException {
        jacobFS.executeCommand(new WriteFile("/windows/system32/system.dll", "system"));

        jacobFS.executeCommand(new RenameFile("/windows/system32/system.dll", "rootkit.dll"));
        assertEquals("system",
                jacobFS.executeCommand(new ReadFileString("/windows/system32/rootkit.dll")));
        assertThrows(NoSuchFileException.class,
                () -> jacobFS.executeCommand(new ReadFileString("/windows/system32/system.dll")));

        reopenTestContainer();

        jacobFS.executeCommand(new RenameFile("/windows/system32/rootkit.dll", "system.dll"));
        assertEquals("system", jacobFS.executeCommand(new ReadFileString("/windows/system32/system.dll")));
        assertThrows(NoSuchFileException.class,
                () -> jacobFS.executeCommand(new ReadFileString("/windows/system32/rootkit.dll")));
    }
}