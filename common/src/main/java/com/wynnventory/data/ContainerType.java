package com.wynnventory.data;

import java.util.regex.Pattern;

public enum ContainerType {
    TRADEMARKET(Pattern.compile("\uDAFF\uDFE8\uE011")),
    PARTY_FINDER(Pattern.compile("Party Finder")),

    CANYON_OF_THE_LOST(Pattern.compile("\uDAFF\uDFF2\uE00A\uDAFF\uDF6F\uF006")),
    CORKUS(Pattern.compile("\uDAFF\uDFF2\uE00A\uDAFF\uDF6F\uF007")),
    MOLTEN_HEIGHTS(Pattern.compile("\uDAFF\uDFF2\uE00A\uDAFF\uDF6F\uF008")),
    SKY_ISLANDS(Pattern.compile("\uDAFF\uDFF2\uE00A\uDAFF\uDF6F\uF009")),
    SILENT_EXPANSE(Pattern.compile("\uDAFF\uDFF2\uE00A\uDAFF\uDF6F\uF00A")),

    // --- Raids ---
    NEST_OF_GROOTSLANGS(Pattern.compile("\uDAFF\uDFEA\uE00D\uDAFF\uDF6F\uF00B")),
    NEXUS_OF_LIGHT(Pattern.compile("\uDAFF\uDFEA\uE00D\uDAFF\uDF6F\uF00C")),
    CANYON_COLOSSUS(Pattern.compile("\uDAFF\uDFEA\uE00D\uDAFF\uDF6F\uF00D")),
    NAMELESS_ANOMALY(Pattern.compile("\uDAFF\uDFEA\uE00D\uDAFF\uDF6F\uF00E"));

    private final Pattern pattern;

    ContainerType(Pattern pattern) {
        this.pattern = pattern;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public static ContainerType find(String title) {
        for(ContainerType type : ContainerType.values()) {
            if(type.getPattern().matcher(title).matches()) {
                return type;
            }
        }

        return null;
    }

    public static boolean isTrademarket(String title) {
        ContainerType result = find(title);
        return result != null && result.equals(TRADEMARKET);
    }
}
