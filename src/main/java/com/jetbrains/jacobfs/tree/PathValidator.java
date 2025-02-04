package com.jetbrains.jacobfs.tree;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

public class PathValidator {
    public static void validatePath(Path path) {
        validateStartsWithRoot(path);
        validateIsNotRoot(path);
        validatePathNormalized(path);
    }

    public static void validatePath(String path) {
        validatePath(Path.of(path));
    }

    private static void validateStartsWithRoot(Path path) {
        if (!Objects.equals(File.separator, path.getRoot().toString())) {
            throw new IllegalArgumentException(
                    String.format("%s - path argument needs to start with %s", path, RootNode.ROOT));
        }
    }

    private static void validateIsNotRoot(Path path) {
        if (Objects.equals(path, path.getRoot())) {
            throw new IllegalArgumentException(
                    String.format("%s - path argument cannot be root", path));
        }
    }

    private static void validatePathNormalized(Path path) {
        if (!Objects.equals(path, path.normalize())) {
            throw new IllegalArgumentException(
                    String.format("%s - path argument needs to be normalized", path));
        }
    }
}
