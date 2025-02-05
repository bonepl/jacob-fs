package com.jetbrains.jacobfs.functionaltests;

import com.jetbrains.jacobfs.AbstractJacobFSTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AbstractFunctionalTest extends AbstractJacobFSTest {
    public Map<String, byte[]> getProjectFiles() throws IOException {
        try (Stream<Path> walk = Files.walk(Path.of("."))) {
            return walk.map(Path::normalize)
                    .filter(path -> !path.startsWith("target"))
                    .filter(path -> !path.startsWith(".idea"))
                    .filter(path -> !path.startsWith(".git"))
                    .filter(path -> !path.startsWith(".github"))
                    .filter(path -> !path.endsWith(TEST_CNT_PATH.replaceAll("\\./", "")))
                    .filter(path -> path.toFile().isFile())
                    .collect(Collectors.toMap(AbstractFunctionalTest::convertWindowsPath, AbstractFunctionalTest::readAllBytesSilently));
        }
    }

    public static byte[] readAllBytesSilently(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String convertWindowsPath(Path path) {
        return "/" + path.toString().replaceAll("\\\\", "/");
    }
}
