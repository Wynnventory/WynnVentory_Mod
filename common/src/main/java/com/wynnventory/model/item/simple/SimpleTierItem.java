package com.wynnventory.model.item.simple;

import com.wynnventory.model.item.Icon;

import java.util.Objects;

public class SimpleTierItem extends SimpleItem {
    protected final int tier;

    public SimpleTierItem(String name, String rarity, String itemType, String type, Icon icon, int tier) {
        super(name, rarity, itemType, type, icon);
        this.tier = tier;
    }

    public SimpleTierItem(String name, String rarity, String itemType, String type, Icon icon, int amount, int tier) {
        super(name, rarity, itemType, type, icon, amount);
        this.tier = tier;
    }

    public int getTier() {
        return tier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof SimpleTierItem other) {
            return tier == other.getTier() &&
                    Objects.equals(name, other.name) &&
                    Objects.equals(itemType, other.itemType) &&
                    Objects.equals(type, other.type);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tier, name, itemType, type);
    }
}