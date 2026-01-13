package com.wynnventory.model.item;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wynntils.models.items.items.game.DungeonKeyItem;
import com.wynntils.models.items.items.game.InsulatorItem;
import com.wynntils.models.items.items.game.RuneItem;
import com.wynntils.models.items.items.game.SimulatorItem;
import com.wynnventory.util.IconManager;
import com.wynnventory.util.ItemStackUtils;
import com.wynnventory.util.StringUtils;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SimplifiedItem {
    protected String name;
    protected String rarity;
    protected String itemType;
    protected String type;
    protected Icon icon;

    public SimplifiedItem() {}

    public SimplifiedItem(String name, String rarity, String itemType, String type) {
        this(name, rarity, itemType, type, null);
    }

    public SimplifiedItem(String name, String rarity, String itemType, String type, Icon icon) {
        this.name = name;
        this.rarity = rarity;
        this.itemType = itemType;
        this.type = type;
        this.icon = icon;
    }

    public SimplifiedItem(SimulatorItem simulatorItem) {
        this.name = ItemStackUtils.getWynntilsOriginalNameAsString(simulatorItem);
        this.rarity = simulatorItem.getGearTier().getName();
        this.itemType = "SimulatorItem";
        this.type = StringUtils.toCamelCase(this.name);
        this.icon = IconManager.getIcon(this.name);
    }

    public SimplifiedItem(InsulatorItem insulatorItem) {
        this.name = ItemStackUtils.getWynntilsOriginalNameAsString(insulatorItem);
        this.rarity = insulatorItem.getGearTier().getName();
        this.itemType = "InsulatorItem";
        this.type = StringUtils.toCamelCase(this.name);
        this.icon = IconManager.getIcon(this.name);
    }

    public SimplifiedItem(RuneItem runeItem) {
        this.name = ItemStackUtils.getWynntilsOriginalNameAsString(runeItem);
        this.rarity = "Normal";
        this.itemType = "RuneItem";
        this.type = StringUtils.toCamelCase(this.name);
        this.icon = IconManager.getIcon(this.name);
    }

    public SimplifiedItem(DungeonKeyItem dungeonKeyItem) {
        this.name = ItemStackUtils.getWynntilsOriginalNameAsString(dungeonKeyItem);
        this.rarity = "Normal";
        this.itemType = "DungeonKeyItem";
        this.type = StringUtils.toCamelCase(this.name);
        this.icon = IconManager.getIcon(this.name);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof SimplifiedItem other) {
            return Objects.equals(name, other.name) &&
                    Objects.equals(rarity, other.rarity) &&
                    Objects.equals(itemType, other.itemType) &&
                    Objects.equals(type, other.type);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, rarity, itemType, type);
    }
}