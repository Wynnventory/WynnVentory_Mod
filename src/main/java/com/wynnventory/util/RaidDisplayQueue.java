package com.wynnventory.util;

public class RaidDisplayQueue {
    private static boolean showRaidAspects = false;

    private RaidDisplayQueue() {}

    public static void setShowRaidAspects(boolean value) {
        showRaidAspects = value;
    }

    public static boolean shouldShowRaidAspects() {
        return showRaidAspects;
    }
}