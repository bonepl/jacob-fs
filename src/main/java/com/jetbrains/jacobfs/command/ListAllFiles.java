package com.jetbrains.jacobfs.command;

import com.jetbrains.jacobfs.tree.DirNode;
import com.jetbrains.jacobfs.tree.TreeLocator;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class ListAllFiles implements Command<List<String>> {
    @Override
    public List<String> execute(TreeLocator treeLocator) {
        List<String> files = new LinkedList<>();
        Path path = Path.of("/");
        collectFilesRecursively(treeLocator.getRootNode(), path, files);
        return files;
    }

    private void collectFilesRecursively(DirNode dirNode, Path path, List<String> fileList) {
        Path newPath = path.resolve(dirNode.getName());
        if (dirNode.getFileNodes() != null) {
            dirNode.getFileNodes().values().stream()
                    .map(fn -> newPath.resolve(fn.getFileName()))
                    .map(fnPath -> fnPath.toString().replaceAll("\\\\", "/"))
                    .forEach(fileList::add);
        }

        if (dirNode.getDirNodes() != null) {
            dirNode.getDirNodes().values()
                    .forEach(dn -> collectFilesRecursively(dn, newPath, fileList));
        }
    }
}
