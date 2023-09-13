package com.eternalcode.plots.util.recoded;

import java.util.regex.Pattern;

public final class TextUtil {

    public static final Pattern LETTER_OR_DIGIT = Pattern.compile("^[a-zA-Z\\d]+$");

    private TextUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static boolean isLetterOrDigit(String text) {
        return LETTER_OR_DIGIT.matcher(text).matches();
    }

}
