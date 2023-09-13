package com.eternalcode.plots.util.old;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateUtils {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    private DateUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String format(Date date) {
        return DATE_FORMAT.format(date);
    }
}
