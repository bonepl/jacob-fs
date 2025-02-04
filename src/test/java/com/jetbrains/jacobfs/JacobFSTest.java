package com.jetbrains.jacobfs;

import com.jetbrains.jacobfs.command.FetchAllFiles;
import com.jetbrains.jacobfs.command.WriteFile;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JacobFSTest extends AbstractJacobFSTest {

    @Test
    void shouldCreateACopy() throws IOException {
        Map<String, byte[]> projectFiles = getProjectFiles();
        for (Map.Entry<String, byte[]> entry : projectFiles.entrySet()) {
            jacobFS.executeCommand(new WriteFile(entry.getKey(), entry.getValue()));
        }
        Map<String, byte[]> containerFiles = jacobFS.executeCommand(new FetchAllFiles());
        assertEquals(projectFiles.size(), containerFiles.size());
        assertTrue(projectFiles.entrySet().stream()
                .allMatch(entry -> Arrays.equals(entry.getValue(), containerFiles.get(entry.getKey()))));
    }

    private Map<String, byte[]> getProjectFiles() throws IOException {
        try (Stream<Path> walk = Files.walk(Path.of("."))) {
            return walk.map(Path::normalize)
                    .filter(path -> !path.startsWith("target"))
                    .filter(path -> path.toFile().isFile())
                    .collect(Collectors.toMap(JacobFSTest::convertWindowsPath, JacobFSTest::readAllBytesSilently));
        }
    }

    private static byte[] readAllBytesSilently(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String convertWindowsPath(Path path) {
        return "/" + path.toString().replaceAll("\\\\", "/");
    }

}