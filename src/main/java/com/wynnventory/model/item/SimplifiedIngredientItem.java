package com.wynnventory.model.item;

import com.wynntils.models.items.items.game.IngredientItem;
import com.wynntils.models.profession.type.ProfessionType;

import java.util.List;
import java.util.Objects;

public class SimplifiedIngredientItem {
    private final String name;
    private final int tier;
    private final int level;
    private final List<ProfessionType> professions;

    public SimplifiedIngredientItem(IngredientItem item) {
        this.name = item.getName();
        this.tier = item.getQualityTier();
        this.level = item.getLevel();
        this.professions = item.getProfessionTypes();
    }

    public String getName() {
        return name;
    }

    public int getTier() {
        return tier;
    }

    public int getLevel() {
        return level;
    }

    public List<ProfessionType> getProfessions() {
        return professions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof SimplifiedIngredientItem other) {
            return tier == other.tier &&
                    level == other.level &&
                    Objects.equals(name, other.name) &&
                    Objects.equals(professions, other.professions);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, tier, level, professions);
    }
}