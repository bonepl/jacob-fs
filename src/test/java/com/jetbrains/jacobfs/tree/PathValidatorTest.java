package com.jetbrains.jacobfs.tree;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class PathValidatorTest {
    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "/",
            "//",
            "d",
            "f",
            "\\\\",
            "/./no",
            "/some/../file.txt"
    })
    void shouldThrowExceptionIfInputInvalid(String wrongInput) {
        assertThrows(IllegalArgumentException.class, () -> PathValidator.validatePath(wrongInput));
    }
}