package com.jetbrains.jacobfs.tree;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class TreeLocatorMetadataTest {
    @Test
    void shouldThrowExceptionWhenFileExistsBeforeCreation() throws IOException {
        Path testFile = Path.of("./test-container.jfs");
        try {
            File file = testFile.toFile();
            if (!file.createNewFile()) {
                fail("Couldn't create test file");
            }
            assertThrows(FileAlreadyExistsException.class, () -> TreeLocatorMetadata.init(testFile));
        } finally {
            Files.deleteIfExists(testFile);
        }
    }

    @Test
    void shouldThrowExceptionWhenTryingToOpenNonexistentFile() {
        assertThrows(NoSuchFileException.class, () -> TreeLocatorMetadata.load(Path.of("./idontexist.jfs")));
    }

    @Test
    void testDefaultsAndSizeOfInitialFile() throws IOException {
        Path testFile = Path.of("./test-container.jfs");
        try {
            TreeLocatorMetadata tlm = TreeLocatorMetadata.init(testFile);
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
            TreeLocatorMetadata tlm = TreeLocatorMetadata.init(testFile);
            tlm.setTreeLocatorLength(newTreeLocatorLength);
            tlm.saveState();

            tlm = TreeLocatorMetadata.load(testFile);
            assertEquals(newTreeLocatorLength, tlm.getTreeLocatorLength());
            assertEquals(tlm.getTreeLocatorOffset() + tlm.getTreeLocatorReservedSpace(), Files.readAllBytes(testFile).length);
        } finally {
            Files.deleteIfExists(testFile);
        }
    }
}