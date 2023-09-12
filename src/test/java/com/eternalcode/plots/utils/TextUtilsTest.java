package com.eternalcode.plots.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextUtilsTest {

    @Test
    void testLetterOrDigit() {
        assertTrue(TextUtils.isLetterOrDigit("0123456789"));
        assertTrue(TextUtils.isLetterOrDigit("Siema123"));
        assertTrue(TextUtils.isLetterOrDigit("123Siema"));
        assertTrue(TextUtils.isLetterOrDigit("Siema"));
    }

    @Test
    void testNoLetterOrDigit() {
        assertFalse(TextUtils.isLetterOrDigit(""));
        assertFalse(TextUtils.isLetterOrDigit("   "));
        assertFalse(TextUtils.isLetterOrDigit("1-"));
        assertFalse(TextUtils.isLetterOrDigit("-1"));
        assertFalse(TextUtils.isLetterOrDigit("()@#$"));
        assertFalse(TextUtils.isLetterOrDigit("!SIEMA!"));
    }

}
