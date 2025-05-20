package com.wynnventory.model.item;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class SimplifiedItem {
    protected final String name;
    protected final String rarity;
    protected final String itemType;
    protected final String type;
    protected Icon icon;

    protected SimplifiedItem(String name, String rarity, String itemType, String type) {
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

    public Icon getIcon() { return icon; }
}