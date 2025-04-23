package com.wynnventory.enums;

public enum ClassIcon {
    WARRIOR(""),
    ARCHER(""),
    MAGE(""),
    ASSASSIN(""),
    SHAMAN("");

    private final String icon;

    ClassIcon(String icon) {
        this.icon = icon;
    }

    public String get() {
        return icon;
    }

    public static ClassIcon fromAspectType(String type) {
        return switch (type.toLowerCase()) {
            case "warrioraspect" -> WARRIOR;
            case "archeraspect" -> ARCHER;
            case "mageaspect" -> MAGE;
            case "assassinaspect" -> ASSASSIN;
            case "shamanaspect" -> SHAMAN;
            default -> null;
        };
    }
}