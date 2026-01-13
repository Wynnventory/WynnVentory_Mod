package com.wynnventory.model.item.simple;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wynntils.models.items.WynnItem;
import com.wynntils.models.items.WynnItemData;
import com.wynntils.models.items.items.game.*;
import com.wynnventory.model.item.Icon;
import com.wynnventory.util.IconManager;
import com.wynnventory.util.ItemStackUtils;
import com.wynnventory.util.StringUtils;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SimpleItem {
    protected String name;
    protected String rarity;
    protected String itemType;
    protected String type;
    protected Icon icon;
    protected int amount = 1;

    public SimpleItem() {}

    public SimpleItem(String name, String rarity, String itemType, String type) {
        this(name, rarity, itemType, type, null, 1);
    }

    public SimpleItem(String name, String rarity, String itemType, String type, Icon icon, int amount) {
        this.name = name;
        this.rarity = rarity;
        this.itemType = itemType;
        this.type = type;
        this.icon = icon;
        this.amount = amount;
    }

    public static SimpleItem fromWynnItem(WynnItem item) {

        switch (item) {
            case SimulatorItem simulatorItem -> {
                return fromSimulatorItem(simulatorItem);
            } case InsulatorItem insulatorItem -> {
                return fromInsulatorItem(insulatorItem);
            } case RuneItem runeItem -> {
                return fromRuneItem(runeItem);
            } case DungeonKeyItem dungeonKeyItem -> {
                return fromDungeonKeyItem(dungeonKeyItem);
            } case GearItem gearItem -> {
                return fromGearItem(gearItem);
            }

            default -> throw new IllegalStateException("Unexpected value: " + item);
        }
    }

    public static SimpleItem fromSimulatorItem(SimulatorItem item) {
        String name = ItemStackUtils.getWynntilsOriginalNameAsString(item);

        return new SimpleItem(name,
                item.getGearTier().getName(),
                "SimulatorItem",
                StringUtils.toCamelCase(name),
                IconManager.getIcon(name),
                ((ItemStack) item.getData().get(WynnItemData.ITEMSTACK_KEY)).getCount()
        );
    }

    public static SimpleItem fromInsulatorItem(InsulatorItem item) {
        String name = ItemStackUtils.getWynntilsOriginalNameAsString(item);

        return new SimpleItem(name,
                item.getGearTier().getName(),
                "InsulatorItem",
                StringUtils.toCamelCase(name),
                IconManager.getIcon(name),
                ((ItemStack) item.getData().get(WynnItemData.ITEMSTACK_KEY)).getCount()
        );
    }

    public static SimpleItem fromRuneItem(RuneItem item) {
        String name = ItemStackUtils.getWynntilsOriginalNameAsString(item);

        return new SimpleItem(name,
                "Normal",
                "RuneItem",
                StringUtils.toCamelCase(name),
                IconManager.getIcon(name),
                ((ItemStack) item.getData().get(WynnItemData.ITEMSTACK_KEY)).getCount());
    }

    public static SimpleItem fromDungeonKeyItem(DungeonKeyItem item) {
        String name = ItemStackUtils.getWynntilsOriginalNameAsString(item);

        return new SimpleItem(name,
                "Normal",
                "DungeonKeyItem",
                StringUtils.toCamelCase(name),
                IconManager.getIcon(name),
                ((ItemStack) item.getData().get(WynnItemData.ITEMSTACK_KEY)).getCount());
    }

    public static SimpleItem fromGearItem(GearItem item) {
        return new SimpleGearItem(item);
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
        if (o instanceof SimpleItem other) {
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