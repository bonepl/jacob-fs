package com.jetbrains.jacobfs.tree;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertThrows;

class TreeLocatorDecoderTest {
    TreeLocatorDecoder treeLocatorDecoder = new TreeLocatorDecoder();

    @ParameterizedTest
    @ValueSource(strings = {
            "wunsupportedTag",
            "/dwrongDir",
            "d",
            "fwrongFile",
            "f0:wrong",
            "f0:1:"
    })
    void shouldThrowExceptionIfInputInvalid(String wrongInput) {
        assertThrows(RuntimeException.class, () -> treeLocatorDecoder.decode(wrongInput));
    }
}