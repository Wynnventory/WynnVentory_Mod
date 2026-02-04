package com.wynnventory.util;

import com.wynntils.models.emeralds.EmeraldModel;
import com.wynntils.models.emeralds.type.EmeraldUnits;

import java.util.Arrays;
import java.util.stream.Collectors;

public class StringUtils {

    private StringUtils() {}

    private static final EmeraldModel EMERALD_MODEL = new EmeraldModel();

    public static String toCamelCase(String input) {
        return toCamelCase(input, "");
    }

    public static String toCamelCase(String input, String delimiter) {
        if (input == null || input.isBlank()) return input;
        return Arrays.stream(input.trim().split("\\s+"))
                .map(StringUtils::capitalize)
                .collect(Collectors.joining(delimiter));
    }

    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase()
                + str.substring(1).toLowerCase();
    }
}