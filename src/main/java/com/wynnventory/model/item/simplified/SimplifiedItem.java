package com.wynnventory.model.item.simplified;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wynnventory.model.item.Icon;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class SimplifiedItem {
    protected String name;
    protected String rarity;
    protected String itemType;
    protected String type;
    protected Icon icon;

    public SimplifiedItem() {}

    protected SimplifiedItem(String name, String rarity, String itemType, String type) {
        this(name, rarity, itemType, type, null);
    }

    protected SimplifiedItem(String name, String rarity, String itemType, String type, Icon icon) {
        this.name = name;
        this.rarity = rarity;
        this.itemType = itemType;
        this.type = type;
        this.icon = icon;
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