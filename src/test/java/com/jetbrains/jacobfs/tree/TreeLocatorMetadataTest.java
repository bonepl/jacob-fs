package com.jetbrains.jacobfs.tree;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TreeLocatorMetadataTest {

    @Test
    void testDefaultsAndSizeOfInitialFile() throws IOException {
        Path testFile = Path.of("./test-container.jfs");
        try {
            TreeLocatorMetadata tlm = new TreeLocatorMetadata(testFile);
            assertEquals(TreeLocatorMetadata.DEFAULT_OFFSET, tlm.getTreeLocatorOffset());
            assertEquals(TreeLocatorMetadata.DEFAULT_OFFSET + TreeLocatorMetadata.DEFAULT_RESERVED, tlm.getPayloadSpaceOffset());
            assertEquals(TreeLocatorMetadata.DEFAULT_RESERVED, tlm.getTreeLocatorReservedSpace());
            assertEquals(tlm.getTreeLocatorOffset() + tlm.getTreeLocatorReservedSpace(), Files.readAllBytes(testFile).length);
        } finally {
            Files.deleteIfExists(testFile);
        }
    }

    @Test
    void testUpdateOfFile() throws IOException {
        Path testFile = Path.of("./test-container.jfs");
        int newTreeLocatorLength = 32;
        try {
            TreeLocatorMetadata tlm = new TreeLocatorMetadata(testFile);
            tlm.setTreeLocatorLength(newTreeLocatorLength);
            tlm.saveState();

            tlm = new TreeLocatorMetadata(testFile);
            assertEquals(newTreeLocatorLength, tlm.getTreeLocatorLength());
            assertEquals(tlm.getTreeLocatorOffset() + tlm.getTreeLocatorReservedSpace(), Files.readAllBytes(testFile).length);
        } finally {
            Files.deleteIfExists(testFile);
        }
    }
}