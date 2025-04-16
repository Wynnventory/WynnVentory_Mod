package com.wynnventory.model.item;

public class SimplifiedItem {
    protected final String name;
    protected final String rarity;

    SimplifiedItem(String name, String rarity) {
        this.name = name;
        this.rarity = rarity;
    }

    public String getName() {
        return name;
    }

    public String getRarity() {
        return rarity;
    }
}
