package com.wynnventory.model.item.simplified;

import com.wynntils.models.items.items.game.*;
import com.wynnventory.util.IconManager;
import com.wynnventory.util.ItemStackUtils;
import com.wynnventory.util.StringUtils;

import java.util.Objects;

public class SimpleTierItem extends SimpleItem {
    protected final int tier;

    public SimpleTierItem(IngredientItem ingredientItem) {
        super();

        this.name = ingredientItem.getName();
        this.rarity = null;
        this.itemType = "IngredientItem";
        this.type = ingredientItem.getIngredientInfo().professions().toString();
        this.icon = IconManager.getIcon(ingredientItem.getName());
        this.tier = ingredientItem.getQualityTier();
    }

    public SimpleTierItem(MaterialItem materialItem) {
        super();

        this.name = ItemStackUtils.getMaterialName(materialItem);
        this.rarity = null;
        this.itemType = "MaterialItem";
        this.type = materialItem.getProfessionTypes().toString();
        this.tier = materialItem.getQualityTier();
        this.icon = IconManager.getIcon(this.name, this.tier);
    }

    public SimpleTierItem(PowderItem powderItem) {
        super();

        this.name = ItemStackUtils.getPowderName(powderItem);
        this.rarity = null;
        this.itemType = "PowderItem";
        this.type = powderItem.getPowderProfile().element().getName() + "Powder";
        this.tier = powderItem.getTier();
        this.icon = IconManager.getIcon(this.name, this.tier);
    }

    public SimpleTierItem(AmplifierItem amplifierItem) {
        super();

        this.name = ItemStackUtils.getAmplifierName(amplifierItem);
        this.rarity = amplifierItem.getGearTier().getName();
        this.itemType = "AmplifierItem";
        this.type = StringUtils.toCamelCase(this.name);
        this.tier = amplifierItem.getTier();
        this.icon = IconManager.getIcon(this.name, this.tier);
    }

    public SimpleTierItem(HorseItem horseItem) {
        super();

        this.name = ItemStackUtils.getHorseName(horseItem);
        this.rarity = "Normal";
        this.itemType = "HorseItem";
        this.type = StringUtils.toCamelCase(this.name);
        this.tier = horseItem.getTier().getNumeral();
        this.icon = IconManager.getIcon(this.name, this.tier);
    }

    public SimpleTierItem(EmeraldPouchItem emeraldPouchItem) {
        super();

        this.name = "Emerald Pouch";
        this.rarity = "Normal";
        this.itemType = "EmeraldPouchItem";
        this.type = StringUtils.toCamelCase(this.name);
        this.tier = emeraldPouchItem.getTier();
        this.icon = IconManager.getIcon(this.name, this.tier);
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