package com.wynnventory.model.reward;

import java.util.regex.Pattern;

public enum RewardScreen {

    // --- Lootruns ---
    CANYON_OF_THE_LOST(RewardType.LOOTRUN, "COTL", "Canyon of the Lost", Pattern.compile("\uDAFF\uDFF2\uE00A\uDAFF\uDF6F\uF006")),
    CORKUS(RewardType.LOOTRUN, "Corkus", "Corkus", Pattern.compile("\uDAFF\uDFF2\uE00A\uDAFF\uDF6F\uF007")),
    MOLTEN_HEIGHTS(RewardType.LOOTRUN, "Molten Heights", "Molten Heights", Pattern.compile("\uDAFF\uDFF2\uE00A\uDAFF\uDF6F\uF008")),
    SKY_ISLANDS(RewardType.LOOTRUN, "Sky Islands", "Sky Islands", Pattern.compile("\uDAFF\uDFF2\uE00A\uDAFF\uDF6F\uF009")),
    SILENT_EXPANSE(RewardType.LOOTRUN, "Silent Expanse", "Silent Expanse", Pattern.compile("\uDAFF\uDFF2\uE00A\uDAFF\uDF6F\uF00A"));

    // --- Raids ---
    // TODO

    private final RewardType type;
    private final String shortName;
    private final String fullName;
    private final Pattern screenTitle;

    RewardScreen(RewardType type, String shortName, String fullName, Pattern screenTitle) {
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

    public static RewardScreen fromTitle(String title) {
        if (title == null) return null;

        for (RewardScreen screen : values()) {
            if (screen.screenTitle.matcher(title).find()) {
                return screen;
            }
        }
        return null;
    }

    public static boolean isLootrunTitle(String title) {
        RewardScreen screen = fromTitle(title);
        return screen != null && screen.type == RewardType.LOOTRUN;
    }
}