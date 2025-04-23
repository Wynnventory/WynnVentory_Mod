package com.wynnventory.model.item;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SimplifiedItem {
    protected final String name;
    protected final String rarity;
    protected final String itemType;
    protected final String type;

    public SimplifiedItem(String name, String rarity, String itemType, String type) {
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

    @JsonProperty("item_type")
    public String getItemType() {
        return itemType;
    }

    public String getType() {
        return type;
    }
}