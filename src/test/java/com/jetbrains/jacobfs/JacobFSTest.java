package com.jetbrains.jacobfs;

import com.jetbrains.jacobfs.command.VoidCommand;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class JacobFSTest extends AbstractJacobFSTest {

    @Test
    void shouldRequiredOpenContainerBeforeExecution() throws IOException {
        jacobFS.executeCommand(sysOutCommand());
        jacobFS.closeContainer();
        assertThrows(IllegalStateException.class, () -> jacobFS.executeCommand(sysOutCommand()));
    }

    private static VoidCommand sysOutCommand() {
        return System.out::println;
    }
}