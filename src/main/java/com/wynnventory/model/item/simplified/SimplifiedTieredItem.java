package com.wynnventory.model.item.simplified;

import com.wynntils.models.items.items.game.IngredientItem;
import com.wynntils.models.items.items.game.MaterialItem;
import com.wynntils.models.items.items.game.PowderItem;
import com.wynnventory.util.IconManager;
import com.wynnventory.util.ItemStackUtils;

import java.util.Objects;

public class SimplifiedTieredItem extends SimplifiedItem {
    protected final int tier;

    public SimplifiedTieredItem(IngredientItem ingredientItem) {
        super(ingredientItem.getName(),
                null,
                "IngredientItem",
                ingredientItem.getIngredientInfo().professions().toString(),
                IconManager.getIcon(ingredientItem.getName()));

        this.tier = ingredientItem.getQualityTier();
    }

    public SimplifiedTieredItem(MaterialItem materialItem) {
        super();

        this.name = ItemStackUtils.getMaterialName(materialItem);
        this.rarity = null;
        this.itemType = "MaterialItem";
        this.type = materialItem.getProfessionTypes().toString();
        this.tier = materialItem.getQualityTier();
        this.icon = IconManager.getIcon(this.name, this.tier);
    }

    public SimplifiedTieredItem(PowderItem powderItem) {
        super();

        this.name = ItemStackUtils.getPowderName(powderItem);
        this.rarity = null;
        this.itemType = "PowderItem";
        this.type = powderItem.getPowderProfile().element().getName() + "Powder";
        this.tier = powderItem.getTier();
        this.icon = IconManager.getIcon(this.name, this.tier);
    }

    public int getTier() {
        return tier;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof SimplifiedTieredItem other) {
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
