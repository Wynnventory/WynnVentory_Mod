package com.wynnventory.model.item;

import com.wynntils.models.items.items.game.IngredientItem;
import com.wynntils.models.profession.type.ProfessionType;

import java.util.List;
import java.util.Objects;

public class SimplifiedIngredientItem extends SimplifiedItem {
    private final int tier;

    public SimplifiedIngredientItem(IngredientItem item) {
        super(item.getName(), null, "IngredientItem", item.getIngredientInfo().professions().toString());
        this.tier = item.getQualityTier();
    }

    public int getTier() {
        return tier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof SimplifiedIngredientItem other) {
            return tier == other.tier &&
                    Objects.equals(name, other.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, tier);
    }
}