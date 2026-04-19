package com.wynnventory.model.item.simple;

import com.wynntils.models.gear.type.GearTier;
import com.wynntils.models.items.WynnItem;
import com.wynntils.models.items.WynnItemData;
import com.wynntils.models.items.items.game.AmplifierItem;
import com.wynntils.models.items.items.game.EmeraldPouchItem;
import com.wynntils.models.items.items.game.IngredientItem;
import com.wynntils.models.items.items.game.MaterialItem;
import com.wynntils.models.items.items.game.MountItem;
import com.wynntils.models.items.items.game.PowderItem;
import com.wynnventory.api.service.IconService;
import com.wynnventory.model.item.Icon;
import com.wynnventory.util.ItemStackUtils;
import com.wynnventory.util.StringUtils;
import java.util.Objects;
import net.minecraft.world.item.ItemStack;

public class SimpleTierItem extends SimpleItem {
    protected int tier;

    public SimpleTierItem() {
        super();
    }

    public SimpleTierItem(
            String name, GearTier rarity, SimpleItemType itemType, String type, Icon icon, int amount, int tier) {
        super(name, rarity, itemType, type, icon, amount);
        this.tier = tier;
    }

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        if (this == o) return true;

        if (o instanceof SimpleTierItem other) {
            return tier == other.tier;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), tier);
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
            case MountItem mountItem -> fromMountItem(mountItem);
            case EmeraldPouchItem emeraldPouchItem -> fromEmeraldPouchItem(emeraldPouchItem);
            case null, default -> null;
        };
    }

    private static SimpleTierItem fromIngredientItem(IngredientItem item) {
        return createTierItem(
                item,
                item.getName(),
                GearTier.NORMAL,
                SimpleItemType.INGREDIENT,
                item.getIngredientInfo().professions().toString(),
                item.getQualityTier());
    }

    private static SimpleTierItem fromMaterialItem(MaterialItem materialItem) {
        return createTierItem(
                materialItem,
                ItemStackUtils.getMaterialName(materialItem),
                GearTier.NORMAL,
                SimpleItemType.MATERIAL,
                materialItem.getProfessionTypes().toString(),
                materialItem.getQualityTier());
    }

    private static SimpleTierItem fromPowderItem(PowderItem powderItem) {
        String element = powderItem.getPowderProfile().element().getName();
        int tier = powderItem.getTier();
        String iconKey = element + (tier <= 3 ? "Small" : "Large");
        return createTierItem(
                powderItem,
                ItemStackUtils.getPowderName(powderItem),
                SimpleItemType.POWDER,
                element + "Powder",
                tier,
                iconKey);
    }

    private static SimpleTierItem fromAmplifierItem(AmplifierItem amplifierItem) {
        return createTierItem(
                amplifierItem,
                ItemStackUtils.getAmplifierName(amplifierItem),
                amplifierItem.getGearTier(),
                SimpleItemType.AMPLIFIER,
                amplifierItem.getTier());
    }

    private static SimpleTierItem fromMountItem(MountItem mountItem) {
        return createTierItem(
                mountItem, ItemStackUtils.getHorseName(mountItem), GearTier.NORMAL, SimpleItemType.MOUNT, 1);
    }

    private static SimpleTierItem fromEmeraldPouchItem(EmeraldPouchItem emeraldPouchItem) {
        return createTierItem(
                emeraldPouchItem,
                "Emerald Pouch",
                SimpleItemType.EMERALD_POUCH,
                SimpleItemType.EMERALD_POUCH.getType(),
                emeraldPouchItem.getTier(),
                "emeraldEmpty");
    }

    private static SimpleTierItem createTierItem(
            WynnItem item, String name, GearTier rarity, SimpleItemType itemType, int tier) {
        return createTierItem(item, name, rarity, itemType, StringUtils.toCamelCase(name), tier);
    }

    private static SimpleTierItem createTierItem(
            WynnItem item, String name, GearTier rarity, SimpleItemType itemType, String type, int tier) {
        int amount = ((ItemStack) item.getData().get(WynnItemData.ITEMSTACK_KEY)).getCount();
        Icon icon = IconService.INSTANCE.resolveIcon(name, itemType);
        return new SimpleTierItem(name, rarity, itemType, type, icon, amount, tier);
    }

    private static SimpleTierItem createTierItem(
            WynnItem item, String name, SimpleItemType itemType, String type, int tier, String iconKey) {
        int amount = ((ItemStack) item.getData().get(WynnItemData.ITEMSTACK_KEY)).getCount();
        Icon icon = IconService.INSTANCE.resolveIcon(name, itemType, iconKey);
        return new SimpleTierItem(name, GearTier.NORMAL, itemType, type, icon, amount, tier);
    }
}
