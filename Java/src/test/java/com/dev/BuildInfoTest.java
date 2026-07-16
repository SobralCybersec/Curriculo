package com.dev;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class BuildInfoTest {

    @Test
    void providesANonBlankVersion() {
        assertFalse(BuildInfo.version().isBlank());
    }
}
