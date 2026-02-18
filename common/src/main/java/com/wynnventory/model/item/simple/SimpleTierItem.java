package com.wynnventory.model.item.simple;

import com.wynntils.models.gear.type.GearTier;
import com.wynntils.models.items.WynnItem;
import com.wynntils.models.items.WynnItemData;
import com.wynntils.models.items.items.game.AmplifierItem;
import com.wynntils.models.items.items.game.EmeraldPouchItem;
import com.wynntils.models.items.items.game.HorseItem;
import com.wynntils.models.items.items.game.IngredientItem;
import com.wynntils.models.items.items.game.MaterialItem;
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
        String type = powderItem.getPowderProfile().element().getName() + "Powder";
        return createTierItem(
                powderItem,
                ItemStackUtils.getPowderName(powderItem),
                GearTier.NORMAL,
                SimpleItemType.POWDER,
                type,
                powderItem.getTier());
    }

    private static SimpleTierItem fromAmplifierItem(AmplifierItem amplifierItem) {
        return createTierItem(
                amplifierItem,
                ItemStackUtils.getAmplifierName(amplifierItem),
                amplifierItem.getGearTier(),
                SimpleItemType.AMPLIFIER,
                amplifierItem.getTier());
    }

    private static SimpleTierItem fromHorseItem(HorseItem horseItem) {
        return createTierItem(
                horseItem,
                ItemStackUtils.getHorseName(horseItem),
                GearTier.NORMAL,
                SimpleItemType.HORSE,
                horseItem.getTier().getNumeral());
    }

    private static SimpleTierItem fromEmeraldPouchItem(EmeraldPouchItem emeraldPouchItem) {
        return createTierItem(
                emeraldPouchItem,
                "Emerald Pouch",
                GearTier.NORMAL,
                SimpleItemType.EMERALD_POUCH,
                emeraldPouchItem.getTier());
    }

    private static SimpleTierItem createTierItem(
            WynnItem item, String name, GearTier rarity, SimpleItemType itemType, int tier) {
        return createTierItem(item, name, rarity, itemType, StringUtils.toCamelCase(name), tier);
    }

    private static SimpleTierItem createTierItem(
            WynnItem item, String name, GearTier rarity, SimpleItemType itemType, String type, int tier) {
        int amount = ((ItemStack) item.getData().get(WynnItemData.ITEMSTACK_KEY)).getCount();
        Icon icon = IconService.INSTANCE.getIcon(name);

        if (icon == null) {
            icon = IconService.INSTANCE.getIcon(name, tier);
        }

        return new SimpleTierItem(name, rarity, itemType, type, icon, amount, tier);
    }
}
