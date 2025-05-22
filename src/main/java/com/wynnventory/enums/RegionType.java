package com.wynnventory.enums;

public enum RegionType {
    LOOTRUN("lootpool"),
    RAID("raidpool");

    private final String name;

    RegionType(String id) {
        this.name = id;
    }

    public String getName() {
        return name;
    }
}
