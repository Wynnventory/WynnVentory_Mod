package com.wynnventory.model.reward;

import com.wynnventory.data.ContainerType;

import java.util.regex.Pattern;

public enum RewardPool {

    // --- Lootruns ---
    CANYON_OF_THE_LOST(RewardType.LOOTRUN, "COTL", "Canyon of the Lost", ContainerType.CANYON_OF_THE_LOST.getPattern()),
    CORKUS(RewardType.LOOTRUN, "Corkus", "Corkus", ContainerType.CORKUS.getPattern()),
    MOLTEN_HEIGHTS(RewardType.LOOTRUN, "Molten Heights", "Molten Heights", ContainerType.MOLTEN_HEIGHTS.getPattern()),
    SKY_ISLANDS(RewardType.LOOTRUN, "Sky Islands", "Sky Islands", ContainerType.SKY_ISLANDS.getPattern()),
    SILENT_EXPANSE(RewardType.LOOTRUN, "Silent Expanse", "Silent Expanse", ContainerType.SILENT_EXPANSE.getPattern()),

    // --- Raids ---
    NEST_OF_GROOTSLANGS(RewardType.RAID, "NOTG", "Nest of the Grootslangs", ContainerType.NEST_OF_GROOTSLANGS.getPattern()),
    NEXUS_OF_LIGHT(RewardType.RAID, "NOL", "Orphion's Nexus of Light", ContainerType.NEXUS_OF_LIGHT.getPattern()),
    CANYON_COLOSSUS(RewardType.RAID, "TCC", "The Canyon Colossus", ContainerType.CANYON_COLOSSUS.getPattern()),
    NAMELESS_ANOMALY(RewardType.RAID, "TNA", "The Nameless Anomaly", ContainerType.NAMELESS_ANOMALY.getPattern());

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

        for (RewardPool screen : values()) {
            if (screen.screenTitle.matcher(title).find()) {
                return screen;
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