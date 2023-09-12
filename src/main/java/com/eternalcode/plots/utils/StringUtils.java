package com.eternalcode.plots.utils;

import java.util.regex.Pattern;

public final class StringUtils {

    public static final Pattern LETTER_OR_DIGIT = Pattern.compile("^[a-zA-Z\\d]+$");

    private StringUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static boolean isLetterOrDigit(String text) {
        return LETTER_OR_DIGIT.matcher(text).matches();
    }

}
