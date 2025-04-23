package com.wynnventory.util;

import java.util.Arrays;
import java.util.stream.Collectors;

public class StringUtils {

    private StringUtils() {}

    public static String toCamelCase(String input) {
        if (input == null || input.isBlank()) return input;
        return Arrays.stream(input.trim().split("\\s+"))
                .map(StringUtils::capitalize)
                .collect(Collectors.joining(" "));
    }

    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase()
                + str.substring(1).toLowerCase();
    }
}
