package com.wynnventory.model.reward;

import java.util.regex.Pattern;

public enum RewardPool {
    // --- Lootruns ---
    CANYON_OF_THE_LOST(
            RewardType.LOOTRUN, "COTL", "Canyon of the Lost", Pattern.compile("\uDAFF\uDFF2\uE00A\uDAFF\uDF6F\uF006")),
    CORKUS(RewardType.LOOTRUN, "Corkus", "Corkus", Pattern.compile("\uDAFF\uDFF2\uE00A\uDAFF\uDF6F\uF007")),
    MOLTEN_HEIGHTS(
            RewardType.LOOTRUN,
            "Molten Heights",
            "Molten Heights",
            Pattern.compile("\uDAFF\uDFF2\uE00A\uDAFF\uDF6F\uF008")),
    SKY_ISLANDS(
            RewardType.LOOTRUN, "Sky Islands", "Sky Islands", Pattern.compile("\uDAFF\uDFF2\uE00A\uDAFF\uDF6F\uF009")),
    SILENT_EXPANSE(
            RewardType.LOOTRUN,
            "Silent Expanse",
            "Silent Expanse",
            Pattern.compile("\uDAFF\uDFF2\uE00A\uDAFF\uDF6F\uF00A")),

    // --- Raids ---
    NEST_OF_GROOTSLANGS(
            RewardType.RAID,
            "NOTG",
            "Nest of the Grootslangs",
            Pattern.compile("\uDAFF\uDFEA\uE00D\uDAFF\uDF6F\uF00B")),
    NEXUS_OF_LIGHT(
            RewardType.RAID,
            "NOL",
            "Orphion's Nexus of Light",
            Pattern.compile("\uDAFF\uDFEA\uE00D\uDAFF\uDF6F\uF00C")),
    CANYON_COLOSSUS(
            RewardType.RAID, "TCC", "The Canyon Colossus", Pattern.compile("\uDAFF\uDFEA\uE00D\uDAFF\uDF6F\uF00D")),
    NAMELESS_ANOMALY(
            RewardType.RAID, "TNA", "The Nameless Anomaly", Pattern.compile("\uDAFF\uDFEA\uE00D\uDAFF\uDF6F\uF00E"));

    private final RewardType type;
    private final String shortName;
    private final String fullName;
    private final Pattern screenTitle;

    RewardPool(RewardType type, String shortName, String fullName, Pattern screenTitle) {
        this.type = type;
        this.shortName = shortName;
        this.fullName = fullName;
        this.screenTitle = screenTitle;
    }

    public RewardType getType() {
        return type;
    }

    public String getShortName() {
        return shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public static RewardPool fromTitle(String title) {
        if (title == null) return null;

        for (RewardPool pool : values()) {
            if (pool.screenTitle.matcher(title).find()) {
                return pool;
            }
        }
        return null;
    }

    public static RewardPool fromFullName(String name) {
        if (name == null) return null;

        for (RewardPool pool : values()) {
            if (pool.fullName.equals(name)) {
                return pool;
            }
        }

        return null;
    }

    public static boolean isLootrunTitle(String title) {
        RewardPool screen = fromTitle(title);
        return screen != null && screen.type == RewardType.LOOTRUN;
    }

    public static boolean isRaidTitle(String title) {
        RewardPool screen = fromTitle(title);
        return screen != null && screen.type == RewardType.RAID;
    }
}
