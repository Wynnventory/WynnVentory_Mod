package com.wynnventory.enums;

public enum PoolType {
    LOOTRUN("lootpool"),
    RAID("raidpool");

    private final String name;

    PoolType(String id) {
        this.name = id;
    }

    public String getName() {
        return name;
    }
}