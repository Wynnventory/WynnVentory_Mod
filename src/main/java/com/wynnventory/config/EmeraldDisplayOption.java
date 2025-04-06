package com.wynnventory.config;

public enum EmeraldDisplayOption {
    BOTH("Both"),
    EMERALDS("Emeralds"),
    FORMATTED("Formatted");

    private final String name;
    EmeraldDisplayOption(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}