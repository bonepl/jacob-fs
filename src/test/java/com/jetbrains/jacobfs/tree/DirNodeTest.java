package com.jetbrains.jacobfs.tree;

import org.junit.jupiter.api.Test;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;

import static org.junit.jupiter.api.Assertions.*;

class DirNodeTest {
    @Test
    void testDefaults() {
        String test = "test";
        DirNode dirNode = new DirNode(test);
        assertEquals(test, dirNode.getName());
        assertNull(dirNode.getDirNodes());
        assertNull(dirNode.getFileNodes());
    }

    @Test
    void addDirNode() {
        DirNode dirNode = new DirNode("test");
        String abc = "abc";
        DirNode abcDirNode = new DirNode(abc);
        assertTrue(dirNode.getDirNodeByName(abc).isEmpty());

        DirNode actual = dirNode.addDirNode(abcDirNode);
        assertEquals(abcDirNode, actual);
        assertTrue(dirNode.getDirNodeByName(abc).isPresent());
        assertEquals(abcDirNode, dirNode.getDirNodeByName(abc).get());

        DirNode secondAbc = new DirNode(abc);
        actual = dirNode.addDirNode(secondAbc);
        assertEquals(abcDirNode, actual);
        assertTrue(dirNode.getDirNodeByName(abc).isPresent());
        assertEquals(abcDirNode, dirNode.getDirNodeByName(abc).get());
    }

    @Test
    void getDirNodeByName() {
        String findMe = "find me";
        String notMe = "notMe";
        DirNode dirNode = new DirNode("test");
        assertTrue(dirNode.getDirNodeByName(notMe).isEmpty());

        DirNode findMeDN = new DirNode(findMe);

        dirNode.addDirNode(findMeDN);
        dirNode.addDirNode(new DirNode("someOtherDir"));

        assertTrue(dirNode.getDirNodeByName(findMe).isPresent());
        assertEquals(findMeDN, dirNode.getDirNodeByName(findMe).get());
        assertTrue(dirNode.getDirNodeByName(notMe).isEmpty());
    }

    @Test
    void removeDirNodeByName() throws NoSuchFileException {
        DirNode dirNode = new DirNode("test");
        dirNode.addDirNode(new DirNode("a"));
        dirNode.addDirNode(new DirNode("b"));
        assertEquals(2, dirNode.getDirNodes().size());
        dirNode.removeDirNodeByName("a");
        assertTrue(dirNode.getDirNodeByName("a").isEmpty());
        assertTrue(dirNode.getDirNodeByName("b").isPresent());
        assertEquals(1, dirNode.getDirNodes().size());

        assertThrows(NoSuchFileException.class, () -> dirNode.removeDirNodeByName("a"));
        dirNode.removeDirNodeByName("b");
        assertTrue(dirNode.getDirNodeByName("b").isEmpty());
        assertNull(dirNode.getDirNodes());
    }

    @Test
    void addFileNode() throws FileAlreadyExistsException {
        DirNode dirNode = new DirNode("test");
        String abc = "abc.file";
        FileNode abcFileNode = new FileNode(abc, 0L, 3);
        assertTrue(dirNode.getFileNodeByName(abc).isEmpty());

        dirNode.addFileNode(abcFileNode);
        assertTrue(dirNode.getFileNodeByName(abc).isPresent());
        assertEquals(abcFileNode, dirNode.getFileNodeByName(abc).get());

        FileNode secondAbc = new FileNode(abc, 4L, 6);
        assertThrows(FileAlreadyExistsException.class, () -> dirNode.addFileNode(secondAbc));
        assertTrue(dirNode.getFileNodeByName(abc).isPresent());
        assertEquals(abcFileNode, dirNode.getFileNodeByName(abc).get());
    }

    @Test
    void getFileNodeByName() throws FileAlreadyExistsException {
        String findMe = "find me";
        String notMe = "notMe";
        DirNode dirNode = new DirNode("test");
        assertTrue(dirNode.getFileNodeByName(notMe).isEmpty());

        FileNode findMeFN = new FileNode(findMe, 0L, 8);

        dirNode.addFileNode(findMeFN);
        dirNode.addFileNode(new FileNode("someOtherFile", 6L, 8));

        assertTrue(dirNode.getFileNodeByName(findMe).isPresent());
        assertEquals(findMeFN, dirNode.getFileNodeByName(findMe).get());
        assertTrue(dirNode.getFileNodeByName(notMe).isEmpty());
    }

    @Test
    void removeFileNodeByName() throws FileAlreadyExistsException, NoSuchFileException {
        DirNode dirNode = new DirNode("test");
        dirNode.addFileNode(new FileNode("a", 0L, 16));
        dirNode.addFileNode(new FileNode("b", 16L, 16));
        assertEquals(2, dirNode.getFileNodes().size());

        dirNode.removeFileNodeByName("a");
        assertTrue(dirNode.getFileNodeByName("a").isEmpty());
        assertTrue(dirNode.getFileNodeByName("b").isPresent());
        assertEquals(1, dirNode.getFileNodes().size());

        assertThrows(NoSuchFileException.class, () -> dirNode.removeFileNodeByName("a"));
        dirNode.removeFileNodeByName("b");
        assertTrue(dirNode.getFileNodeByName("b").isEmpty());
        assertNull(dirNode.getFileNodes());
    }
}