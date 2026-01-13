package com.wynnventory.model.item.simple;

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

    public SimpleTierItem(String name, String rarity, String itemType, String type, Icon icon, int tier) {
        super(name, rarity, itemType, type, icon);
        this.tier = tier;
    }

    public SimpleTierItem(String name, String rarity, String itemType, String type, Icon icon, int amount, int tier) {
        super(name, rarity, itemType, type, icon, amount);
        this.tier = tier;
    }

    public static SimpleTierItem fromIngredientItem(IngredientItem item) {
        String name = item.getName();

        return new SimpleTierItem(name,
                null,
                "IngredientItem",
                item.getIngredientInfo().professions().toString(),
                IconManager.getIcon(name),
                ((ItemStack) item.getData().get(WynnItemData.ITEMSTACK_KEY)).getCount(),
                item.getQualityTier()
        );
    }

     public static SimpleTierItem fromMaterialItem(MaterialItem materialItem) {
        String name = ItemStackUtils.getMaterialName(materialItem);
        int tier = materialItem.getQualityTier();

        return new SimpleTierItem(name,
                null,
                "MaterialItem",
                materialItem.getProfessionTypes().toString(),
                IconManager.getIcon(name, tier),
                ((ItemStack) materialItem.getData().get(WynnItemData.ITEMSTACK_KEY)).getCount(),
                tier
        );
     }

     public static SimpleTierItem fromPowderItem(PowderItem powderItem) {
        String name = ItemStackUtils.getPowderName(powderItem);

        return new SimpleTierItem(name,
                null,
                "PowderItem",
                powderItem.getPowderProfile().element().getName() + "Powder",
                IconManager.getIcon(name),
                ((ItemStack) powderItem.getData().get(WynnItemData.ITEMSTACK_KEY)).getCount(),
                powderItem.getTier()
        );
     }

     public static SimpleTierItem fromAmplifierItem(AmplifierItem amplifierItem) {
        String name = ItemStackUtils.getAmplifierName(amplifierItem);
        int tier = amplifierItem.getTier();

        return new SimpleTierItem(name,
                amplifierItem.getGearTier().getName(),
                "AmplifierItem",
                StringUtils.toCamelCase(name),
                IconManager.getIcon(name, tier),
                ((ItemStack) amplifierItem.getData().get(WynnItemData.ITEMSTACK_KEY)).getCount(),
                tier
        );
     }

     public static SimpleTierItem fromHorseItem(HorseItem horseItem) {
        String name = ItemStackUtils.getHorseName(horseItem);
        int tier = horseItem.getTier().getNumeral();

        return new SimpleTierItem(name,
                "Normal",
                "HorseItem",
                StringUtils.toCamelCase(name),
                IconManager.getIcon(name, tier),
                ((ItemStack) horseItem.getData().get(WynnItemData.ITEMSTACK_KEY)).getCount(),
                tier
        );
     }

     public static SimpleTierItem fromEmeraldPouchItem(EmeraldPouchItem emeraldPouchItem) {
        String name = "Emerald Pouch";
        int tier = emeraldPouchItem.getTier();

        return new SimpleTierItem(name,
                "Normal",
                "EmeraldPouchItem",
                StringUtils.toCamelCase(name),
                IconManager.getIcon(name, tier),
                ((ItemStack) emeraldPouchItem.getData().get(WynnItemData.ITEMSTACK_KEY)).getCount(),
                tier
        );
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