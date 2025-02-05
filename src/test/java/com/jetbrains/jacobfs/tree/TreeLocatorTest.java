package com.jetbrains.jacobfs.tree;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TreeLocatorTest {
    public static final Path TEST_CONTAINER_PATH = Path.of("./tree-locator-test.jfs");
    TreeLocator treeLocator;

    @BeforeEach
    void setUp() throws IOException {
        Files.deleteIfExists(TEST_CONTAINER_PATH);
        treeLocator = new TreeLocator(TEST_CONTAINER_PATH);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(TEST_CONTAINER_PATH);
    }

    void reloadTestTreeLocator() throws IOException {
        treeLocator = new TreeLocator(TEST_CONTAINER_PATH);
    }

    @Test
    void testSimpleSaveAndLoad() throws IOException {
        RootNode rootNode = treeLocator.getRootNode();
        FileNode fileNode = new FileNode("test.sh", 2L, 8);
        rootNode.addFileNode(fileNode);
        treeLocator.saveState();

        reloadTestTreeLocator();

        RootNode reloadedRootNode = treeLocator.getRootNode();
        assertTrue(reloadedRootNode.getFileNodeByName(fileNode.getFileName()).isPresent());
        assertEquals(fileNode, reloadedRootNode.getFileNodeByName(fileNode.getFileName()).get());
    }

    @Test
    void shouldMakeDirNodes() {
        RootNode rootNode = treeLocator.getRootNode();
        String testFilePath = "/home/jacob/downloads/movies/harry potter/prisoner of azkaban/thumbnail.jpg";
        //return new
        DirNode dirNode = treeLocator.makeDirNodesPath(Path.of(testFilePath));

        String[] split = testFilePath.split("/");
        DirNode currentNode = rootNode;
        for (int i = 1; i < split.length-1; i++) {
            Optional<DirNode> dirNodeByName = currentNode.getDirNodeByName(split[i]);
            currentNode = dirNodeByName.orElseThrow();
        }
        assertSame(currentNode, dirNode);

        //return existing
        DirNode existingDirNode = treeLocator.makeDirNodesPath(Path.of(testFilePath));
        assertSame(existingDirNode, dirNode);
    }

    @Test
    void shouldRemoveEmptyDirsOnPath() throws FileAlreadyExistsException, NoSuchFileException {
        RootNode rootNode = treeLocator.getRootNode();
        Path testFilePath = Path.of("/home/jacob/downloads/movies/harry potter/sorcerer's stone/thumbnail.jpg");
        treeLocator.makeDirNodesPath(testFilePath);

        DirNode dirNode = rootNode.getDirNodeByName("home").orElseThrow()
                .getDirNodeByName("jacob").orElseThrow()
                .getDirNodeByName("downloads").orElseThrow();
        dirNode.addFileNode(new FileNode("sample.movie.mpg", 20L, 1000));
        assertEquals(1, dirNode.getDirNodes().size());

        treeLocator.removeEmptyDirNodesFromPath(testFilePath);
        dirNode = rootNode.getDirNodeByName("home").orElseThrow()
                .getDirNodeByName("jacob").orElseThrow()
                .getDirNodeByName("downloads").orElseThrow();
        assertNull(dirNode.getDirNodes());

        assertThrows(NoSuchFileException.class, () -> treeLocator.removeEmptyDirNodesFromPath(testFilePath));
    }

    @Test
    void shouldGetFileNodeFromPath() throws FileAlreadyExistsException {
        RootNode rootNode = treeLocator.getRootNode();
        DirNode testDirNode = rootNode.addDirNode(new DirNode("test"));
        DirNode dirNode = testDirNode.addDirNode(new DirNode("dir"));
        FileNode fileNode = new FileNode("sample.txt", 16L, 32);
        dirNode.addFileNode(fileNode);

        Optional<FileNode> existing = treeLocator.getFileNode(Path.of("/test/dir/sample.txt"));
        assertTrue(existing.isPresent());
        assertSame(fileNode, existing.get());

        assertTrue(treeLocator.getFileNode(Path.of("/test/dir/bad sample.txt")).isEmpty());
    }
}