package com.wynnventory.model.item;

import com.wynntils.models.items.items.game.IngredientItem;
import com.wynntils.models.profession.type.ProfessionType;

import java.util.List;
import java.util.Objects;

public class SimplifiedIngredientItem extends SimplifiedItem {
    private final int tier;
    private final List<ProfessionType> professions;

    public SimplifiedIngredientItem(IngredientItem item) {
        super(item.getName(), "Common");
        this.tier = item.getQualityTier();
        this.professions = item.getProfessionTypes();
    }

    public int getTier() {
        return tier;
    }

    public List<ProfessionType> getProfessions() {
        return professions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof SimplifiedIngredientItem other) {
            return tier == other.tier &&
                    Objects.equals(name, other.name) &&
                    Objects.equals(professions, other.professions);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, tier, professions);
    }
}