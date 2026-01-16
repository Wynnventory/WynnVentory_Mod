package com.wynnventory.model.item.simple;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wynnventory.data.TimestampedObject;
import com.wynnventory.model.item.Icon;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SimpleItem extends TimestampedObject {
    protected String name;
    protected String rarity;
    protected String itemType;
    protected String type;
    protected Icon icon;
    protected int amount;

    public SimpleItem() {}

    public SimpleItem(String name, String rarity, String itemType, String type) {
        this(name, rarity, itemType, type, null);
    }

    public SimpleItem(String name, String rarity, String itemType, String type, Icon icon) {
        this(name, rarity, itemType, type, icon, 1);
    }

    public SimpleItem(String name, String rarity, String itemType, String type, Icon icon, int amount) {
        this.name = name;

        if(rarity == null || rarity.isBlank()) {
            this.rarity = "Common";
        } else {
            this.rarity = rarity;
        }

        this.itemType = itemType;
        this.type = type;
        this.icon = icon;
        this.amount = amount;
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof SimpleItem other) {
            return Objects.equals(name, other.name) &&
                    Objects.equals(rarity, other.rarity) &&
                    Objects.equals(itemType, other.itemType) &&
                    Objects.equals(type, other.type) &&
                    amount == other.amount;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, rarity, itemType, type, amount);
    }
}