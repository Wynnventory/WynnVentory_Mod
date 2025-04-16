package com.wynnventory.model.item;

public class SimplifiedItem {
    protected final String name;
    protected final String rarity;


    protected final String itemType;
    protected final String type;

    SimplifiedItem(String name, String rarity, String itemType, String type) {
        this.name = name;
        this.rarity = rarity;
        this.itemType = itemType;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getRarity() {
        return rarity;
    }

    public String getItemType() {
        return itemType;
    }

    public String getType() {
        return type;
    }
}
