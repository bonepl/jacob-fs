package com.jetbrains.jacobfs.tree;

import org.junit.jupiter.api.Test;

import java.nio.file.FileAlreadyExistsException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TreeLocatorEncoderTest {

    @Test
    void shouldThrowIfTreeLocatorTooBig() {
        TreeLocatorMetadata mockedTLM = mock(TreeLocatorMetadata.class);
        int reservedSpace = 8;
        when(mockedTLM.getTreeLocatorReservedSpace()).thenReturn(reservedSpace);

        TreeLocatorEncoder treeLocatorEncoder = spy(new TreeLocatorEncoder());
        doReturn(new String(new byte[reservedSpace + 1])).when(treeLocatorEncoder).encode(any());
        assertThrows(RuntimeException.class,
                () -> treeLocatorEncoder.saveTreeLocatorToFile(new RootNode(), mockedTLM));
    }

    @Test
    void shouldEncodeDecodeEmpty() throws FileAlreadyExistsException {
        TreeLocatorEncoder treeLocatorEncoder = new TreeLocatorEncoder();
        TreeLocatorDecoder treeLocatorDecoder = new TreeLocatorDecoder();

        String encoded1 = treeLocatorEncoder.encode(new RootNode());
        RootNode decoded = treeLocatorDecoder.decode(encoded1);
        String encoded2 = treeLocatorEncoder.encode(decoded);
        assertEquals(encoded1, encoded2);
        assertTrue(encoded1.isEmpty());
    }

    @Test
    void shouldEncodeDecode() throws FileAlreadyExistsException {
        DirNode lvl31 = new DirNode("lvl3");
        lvl31.addFileNode(new FileNode("31.txt", 0L, 8));
        lvl31.addFileNode(new FileNode("32.txt", 8L, 16));

        DirNode lvl32 = new DirNode("lvl3");
        lvl32.addFileNode(new FileNode("33.txt", 16L, 24));

        DirNode lvl2 = new DirNode("lvl2");
        lvl2.addDirNode(lvl31);
        lvl2.addDirNode(lvl32);
        lvl2.addFileNode(new FileNode("34.dll", 24L, 32));

        DirNode lvl1 = new DirNode("lvl1");
        lvl1.addDirNode(lvl2);

        RootNode rootNode = new RootNode();
        rootNode.addDirNode(lvl1);
        rootNode.addFileNode(new FileNode("35.lock", 32L, 40));

        TreeLocatorEncoder treeLocatorEncoder = new TreeLocatorEncoder();
        TreeLocatorDecoder treeLocatorDecoder = new TreeLocatorDecoder();
        String encoded1 = treeLocatorEncoder.encode(rootNode);
        RootNode decoded1 = treeLocatorDecoder.decode(encoded1);
        String encoded2 = treeLocatorEncoder.encode(decoded1);
        assertEquals(encoded1, encoded2);
    }
}