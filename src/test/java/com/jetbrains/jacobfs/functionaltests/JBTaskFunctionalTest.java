package com.jetbrains.jacobfs.functionaltests;

import com.jetbrains.jacobfs.command.DeleteFile;
import com.jetbrains.jacobfs.command.FetchAllFiles;
import com.jetbrains.jacobfs.command.WriteFile;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JBTaskFunctionalTest extends AbstractFunctionalTest {

    @Test
    void shouldPerformJBTestFromTask() throws IOException {
        //add all project files
        Map<String, byte[]> projectFiles = getProjectFiles();
        for (Map.Entry<String, byte[]> entry : projectFiles.entrySet()) {
            jacobFS.executeCommand(new WriteFile(entry.getKey(), entry.getValue()));
        }

        //remove everything but com.jetbrains.jacobfs.command sources
        String filter70percent = "/src/main/java/com/jetbrains/jacobfs/command";
        for (String toDelete : projectFiles.keySet()) {
            if (!toDelete.startsWith(filter70percent)) {
                jacobFS.executeCommand(new DeleteFile(toDelete));
            }
        }

        //add all project files again under clone/ dir
        for (Map.Entry<String, byte[]> entry : projectFiles.entrySet()) {
            jacobFS.executeCommand(new WriteFile("/clone" + entry.getKey(), entry.getValue()));
        }

        //reopen container
        reopenTestContainer();

        //verify
        List<String> commandFiles = projectFiles.keySet().stream()
                .filter(key -> key.startsWith(filter70percent)).toList();
        Map<String, byte[]> containerFiles = jacobFS.executeCommand(new FetchAllFiles());
        assertEquals(projectFiles.size() + commandFiles.size(), containerFiles.size());

        for (String commandFileKey : commandFiles) {
            assertArrayEquals(projectFiles.get(commandFileKey), containerFiles.get(commandFileKey));
        }

        for (Map.Entry<String, byte[]> entry : projectFiles.entrySet()) {
            assertArrayEquals(entry.getValue(), containerFiles.get("/clone" + entry.getKey()));
        }
    }
}