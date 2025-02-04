package com.jetbrains.jacobfs.command;

import com.jetbrains.jacobfs.tree.TreeLocator;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FetchAllFiles implements Command<Map<String, byte[]>> {
    private static Function<String, byte[]> readFileSilently(TreeLocator treeLocator) {
        return file -> {
            try {
                return new ReadFileBytes(file).execute(treeLocator);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Override
    public Map<String, byte[]> execute(TreeLocator treeLocator) {
        return new ListAllFiles().execute(treeLocator).stream()
                .collect(Collectors.toMap(Function.identity(), readFileSilently(treeLocator)));
    }


}
