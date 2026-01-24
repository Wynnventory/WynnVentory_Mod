package com.wynnventory.model.item.simple;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wynntils.core.components.Models;
import com.wynntils.models.items.WynnItem;
import com.wynntils.models.items.WynnItemData;
import com.wynntils.models.items.items.game.*;
import com.wynnventory.model.item.Icon;
import com.wynnventory.model.item.TimestampedObject;
import com.wynnventory.util.IconManager;
import com.wynnventory.util.ItemStackUtils;
import com.wynnventory.util.StringUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SimpleItem extends TimestampedObject {
    protected Component displayName;
    protected String name;
    protected String rarity;
    protected String itemType;
    protected String type;
    protected Icon icon;
    protected int amount;

    public SimpleItem() {}

    public SimpleItem(Component displayName, String name, String rarity, String itemType, String type) {
        this(displayName, name, rarity, itemType, type, null);
    }

    public SimpleItem(Component displayName, String name, String rarity, String itemType, String type, Icon icon) {
        this(displayName, name, rarity, itemType, type, icon, 1);
    }

    public SimpleItem(Component displayName, String name, String rarity, String itemType, String type, Icon icon, int amount) {
        this.displayName = displayName;
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

    public Component getDisplayName() {
        return displayName;
    }

    public void setDisplayName(MutableComponent displayName) {
        this.displayName = displayName;
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

    @Override
    public String toString() {
        return "SimpleItem{" +
                "name='" + name + '\'' +
                ", rarity='" + rarity + '\'' +
                ", itemType='" + itemType + '\'' +
                ", type='" + type + '\'' +
                ", icon=" + icon +
                ", amount=" + amount +
                '}';
    }

    public static SimpleItem from(ItemStack stack) {
        return Models.Item.getWynnItem(stack).map(SimpleItem::from).orElse(null);
    }

    public static SimpleItem from(WynnItem item) {
        return switch (item) {
            case SimulatorItem simItem -> fromSimulatorItem(simItem);
            case InsulatorItem insItem -> fromInsulatorItem(insItem);
            case RuneItem runeItem -> fromRuneItem(runeItem);
            case DungeonKeyItem dungeonKeyItem -> fromDungeonKeyItem(dungeonKeyItem);
            case EmeraldItem emeraldItem -> fromEmeraldItem(emeraldItem);
            case AspectItem aspectItem -> fromAspectItem(aspectItem);
            case TomeItem tomeItem -> fromTomeItem(tomeItem);
            case null, default -> null;
        };
    }

    private static SimpleItem fromSimulatorItem(SimulatorItem item) {
        return createSimpleItem(item, item.getGearTier().getName(), "SimulatorItem", "Simulator");
    }

    private static SimpleItem fromInsulatorItem(InsulatorItem item) {
        return createSimpleItem(item, item.getGearTier().getName(), "InsulatorItem", "Insulator");
    }

    private static SimpleItem fromRuneItem(RuneItem item) {
        return createSimpleItem(item, "RuneItem");
    }

    private static SimpleItem fromDungeonKeyItem(DungeonKeyItem item) {
        return createSimpleItem(item,"DungeonKeyItem");
    }

    private static SimpleItem fromEmeraldItem(EmeraldItem emeraldItem) {
        return createSimpleItem(emeraldItem, "Common", "EmeraldItem", emeraldItem.getUnit().name());
    }

    private static SimpleItem fromAspectItem(AspectItem aspectItem) {
        return createSimpleItem(aspectItem, aspectItem.getGearTier().getName(), "AspectItem", aspectItem.getRequiredClass().getName() + "Aspect");
    }

    private static SimpleItem fromTomeItem(TomeItem tomeItem) {
        Component displayName = ItemStackUtils.getWynntilsOriginalNameAsComponent(tomeItem);
        return new SimpleItem(displayName, tomeItem.getName().replace("Unidentified ", ""), tomeItem.getGearTier().getName(), "TomeItem", tomeItem.getItemInfo().type().name());
    }

    private static SimpleItem createSimpleItem(WynnItem item, String itemType) {
        String name = ItemStackUtils.getWynntilsOriginalNameAsString(item);
        return createSimpleItem(item, "Common", itemType, StringUtils.toCamelCase(name));
    }

    private static SimpleItem createSimpleItem(WynnItem item, String rarity, String itemType, String type) {
        String name = ItemStackUtils.getWynntilsOriginalNameAsString(item);
        int amount = ((ItemStack) item.getData().get(WynnItemData.ITEMSTACK_KEY)).getCount();
        Component displayName = ItemStackUtils.getWynntilsOriginalNameAsComponent(item);

        return new SimpleItem(displayName, name, rarity, itemType, type, IconManager.getIcon(name), amount);
    }
}