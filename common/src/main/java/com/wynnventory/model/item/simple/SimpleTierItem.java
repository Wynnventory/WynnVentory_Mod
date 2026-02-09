package com.wynnventory.model.item.simple;

import com.wynntils.models.items.WynnItem;
import com.wynntils.models.items.WynnItemData;
import com.wynntils.models.items.items.game.*;
import com.wynnventory.model.item.Icon;
import com.wynnventory.util.IconManager;
import com.wynnventory.util.ItemStackUtils;
import com.wynnventory.util.StringUtils;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class SimpleTierItem extends SimpleItem {
    protected final int tier;

    public SimpleTierItem(String name, String rarity, String itemType, String type, Icon icon, int amount, int tier) {
        super(name, rarity, itemType, type, icon, amount);
        this.tier = tier;
    }

    public int getTier() {
        return tier;
    }

    @Override
    public boolean equals(Object o) {
        if(!super.equals(o)) return false;
        if (this == o) return true;

        if (o instanceof SimpleTierItem other) {
            return tier == other.getTier();
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tier, name, itemType, type);
    }

    public static SimpleTierItem from(ItemStack stack) {
        return from(ItemStackUtils.getWynnItem(stack));
    }

    public static SimpleTierItem from(WynnItem item) {
        return switch (item) {
            case IngredientItem ingredientItem -> fromIngredientItem(ingredientItem);
            case MaterialItem materialItem -> fromMaterialItem(materialItem);
            case PowderItem powderItem -> fromPowderItem(powderItem);
            case AmplifierItem amplifierItem -> fromAmplifierItem(amplifierItem);
            case HorseItem horseItem -> fromHorseItem(horseItem);
            case EmeraldPouchItem emeraldPouchItem -> fromEmeraldPouchItem(emeraldPouchItem);
            case null, default -> null;
        };
    }

    private static SimpleTierItem fromIngredientItem(IngredientItem item) {
        return createTierItem(item, item.getName(), "Common", "IngredientItem", item.getIngredientInfo().professions().toString(), item.getQualityTier());
    }

    private static SimpleTierItem fromMaterialItem(MaterialItem materialItem) {
        return createTierItem(materialItem, ItemStackUtils.getMaterialName(materialItem), "Common", "MaterialItem", materialItem.getProfessionTypes().toString(), materialItem.getQualityTier());
    }

    private static SimpleTierItem fromPowderItem(PowderItem powderItem) {
        String type = powderItem.getPowderProfile().element().getName() + "Powder";
        return createTierItem(powderItem, ItemStackUtils.getPowderName(powderItem), "Common", "PowderItem", type, powderItem.getTier());
    }

    private static SimpleTierItem fromAmplifierItem(AmplifierItem amplifierItem) {
        return createTierItem(amplifierItem, ItemStackUtils.getAmplifierName(amplifierItem), amplifierItem.getGearTier().getName(), "AmplifierItem", amplifierItem.getTier());
    }

    private static SimpleTierItem fromHorseItem(HorseItem horseItem) {
        return createTierItem(horseItem, ItemStackUtils.getHorseName(horseItem), "Common", "HorseItem", horseItem.getTier().getNumeral());
    }

    private static SimpleTierItem fromEmeraldPouchItem(EmeraldPouchItem emeraldPouchItem) {
        return createTierItem(emeraldPouchItem, "Emerald Pouch", "Common", "EmeraldPouchItem", emeraldPouchItem.getTier());
    }

    private static SimpleTierItem createTierItem(WynnItem item, String name, String rarity, String itemType, int tier) {
        return createTierItem(item, name, rarity, itemType, StringUtils.toCamelCase(name), tier);
    }

    private static SimpleTierItem createTierItem(WynnItem item, String name, String rarity, String itemType, String type, int tier) {
        int amount = ((ItemStack) item.getData().get(WynnItemData.ITEMSTACK_KEY)).getCount();
        Icon icon = IconManager.getIcon(name);

        if(icon == null) {
            icon = IconManager.getIcon(name, tier);
        }

        return new SimpleTierItem(name, rarity, itemType, type, icon, amount, tier);
    }
}