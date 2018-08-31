package com.nprinting.utils;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.joining;

import java.util.List;

public final class StringUtils {

    private static final String EMPTY_STRING = "";
    private static final String SPACE = " ";
    private static final String NEW_LINE = "\n";
    private static final String SEPARATOR = ";";
    private static final String PREFIX = "[";
    private static final String SUFFIX = "]";

    private StringUtils() {
        throw new RuntimeException("This class should not be instantiated");
    }

    public static boolean isEmpty(final String input) {
        return (null == input) || (EMPTY_STRING.equals(input));
    }

    public static <T> String asString(List<T> list) {
        return list.stream().map(String::valueOf).collect(joining(SEPARATOR, PREFIX, SUFFIX));
    }

    public static List<String> splitSafely(final String stringToSplit, final String separator) {
        if (isEmpty(stringToSplit)) {
            return singletonList(stringToSplit);
        }
        return (stringToSplit.contains(separator))
               ? asList(stringToSplit.split(separator))
               : singletonList(stringToSplit);
    }

    public static String trim(String stringToTrim) {
        return stringToTrim.replaceAll(SPACE, EMPTY_STRING).replaceAll(NEW_LINE, EMPTY_STRING);
    }
}
