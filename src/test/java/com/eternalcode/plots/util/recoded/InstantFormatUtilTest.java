package com.eternalcode.plots.util.recoded;

import com.eternalcode.plots.good.util.InstantFormatUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
 class InstantFormatUtilTest {

    @Test
    void testFormat() {
        // Given
        ZonedDateTime zdt = ZonedDateTime.of(2023, 2, 1, 10, 15, 0, 0, ZoneId.systemDefault());
        Instant instant = zdt.toInstant();

        // When
        String result = InstantFormatUtil.format(instant);

        // Then
        Assertions.assertEquals("01.02.2023 10:15", result);
    }
}