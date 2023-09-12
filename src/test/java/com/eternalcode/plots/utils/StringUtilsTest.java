package com.eternalcode.plots.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringUtilsTest {

    @Test
    void testLetterOrDigit() {
        assertTrue(StringUtils.isLetterOrDigit("0123456789"));
        assertTrue(StringUtils.isLetterOrDigit("Siema123"));
        assertTrue(StringUtils.isLetterOrDigit("123Siema"));
        assertTrue(StringUtils.isLetterOrDigit("Siema"));
    }

    @Test
    void testNoLetterOrDigit() {
        assertFalse(StringUtils.isLetterOrDigit(""));
        assertFalse(StringUtils.isLetterOrDigit("   "));
        assertFalse(StringUtils.isLetterOrDigit("1-"));
        assertFalse(StringUtils.isLetterOrDigit("-1"));
        assertFalse(StringUtils.isLetterOrDigit("()@#$"));
        assertFalse(StringUtils.isLetterOrDigit("!SIEMA!"));
    }

}
