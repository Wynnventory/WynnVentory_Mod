package com.wynnventory.util;

import com.wynntils.models.emeralds.EmeraldModel;
import com.wynntils.models.emeralds.type.EmeraldUnits;

import java.util.Arrays;
import java.util.stream.Collectors;

public class StringUtils {

    private StringUtils() {}

    private static final EmeraldModel EMERALD_MODEL = new EmeraldModel();

    public static String getFormattedString(int emeralds, boolean appendZeros) {
        StringBuilder builder = new StringBuilder();
        int[] emeraldAmounts = EMERALD_MODEL.emeraldsPerUnit(emeralds);

        if (emeraldAmounts[3] > 0) {
            // Handling stx and fractional le
            builder.append(emeraldAmounts[3]).append(EmeraldUnits.LIQUID_EMERALD_STX.getSymbol()).append(" ");

            double fractionalLe = emeraldAmounts[2] + emeraldAmounts[1] / 64.0 + emeraldAmounts[0] / 4096.0;
            if (fractionalLe > 0 || appendZeros) {
                builder.append(String.format("%.1f", fractionalLe)).append(EmeraldUnits.LIQUID_EMERALD.getSymbol()).append(" ");
            }
        } else {
            // Handling le, eb, and e normally
            for (int i = emeraldAmounts.length - 2; i >= 0; --i) {
                if (emeraldAmounts[i] != 0 || appendZeros) {
                    builder.append(emeraldAmounts[i]).append(EmeraldUnits.values()[i].getSymbol()).append(" ");
                }
            }
        }

        return builder.toString().trim();
    }

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