package com.jetbrains.jacobfs.functionaltests;

import com.jetbrains.jacobfs.command.FetchAllFiles;
import com.jetbrains.jacobfs.command.WriteFile;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImportExportProjectFunctionalTest extends AbstractFunctionalTest {

    @Test
    void shouldImportExportProjectFiles() throws IOException {
        Map<String, byte[]> projectFiles = getProjectFiles();
        for (Map.Entry<String, byte[]> entry : projectFiles.entrySet()) {
            jacobFS.executeCommand(new WriteFile(entry.getKey(), entry.getValue()));
        }
        Map<String, byte[]> containerFiles = jacobFS.executeCommand(new FetchAllFiles());
        assertEquals(projectFiles.size(), containerFiles.size());
        assertTrue(projectFiles.entrySet().stream()
                .allMatch(entry -> Arrays.equals(entry.getValue(), containerFiles.get(entry.getKey()))));
    }
}