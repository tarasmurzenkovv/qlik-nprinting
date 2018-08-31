package com.nprinting.utils;

import static java.time.format.DateTimeFormatter.ofPattern;

import java.time.LocalDateTime;


public final class DateUtils {
    private static final String DATE_TIME_PATTERN = "yyyyMMddHHmmss";

    private DateUtils() {
        throw new RuntimeException("This class should not be instantiated");
    }

    public static String getCurrentDateAsString() {
        return LocalDateTime.now().format(ofPattern(DATE_TIME_PATTERN));
    }
}
