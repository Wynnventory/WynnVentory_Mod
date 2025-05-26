package com.wynnventory.model.item;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class SimplifiedItem {
    protected String name;
    protected String rarity;
    protected String itemType;
    protected String type;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }
}