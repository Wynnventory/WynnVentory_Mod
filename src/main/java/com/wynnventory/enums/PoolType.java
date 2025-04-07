package com.wynnventory.enums;

public enum PoolType {
    LOOTRUN("lootrun"),
    RAID("raidpool");

    private final String name;

    PoolType(String id) {
        this.name = id;
    }

    public String getName() {
        return name;
    }
}