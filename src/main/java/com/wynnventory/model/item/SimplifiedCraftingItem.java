package com.wynnventory.model.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wynntils.models.items.items.game.IngredientItem;
import com.wynntils.models.items.items.game.MaterialItem;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class SimplifiedCraftingItem extends SimplifiedItem {
    private final int tier;
    @JsonIgnore
    private String sourceMaterialName;
    @JsonIgnore
    private String resourceTypeName;

    public SimplifiedCraftingItem(IngredientItem ingredientItem) {
        super(ingredientItem.getName(), null, "IngredientItem", ingredientItem.getIngredientInfo().professions().toString());
        this.tier = ingredientItem.getQualityTier();
    }

    public SimplifiedCraftingItem(MaterialItem materialItem) {
        super("", null, "MaterialItem", materialItem.getProfessionTypes().toString());
        this.tier = materialItem.getQualityTier();
        this.sourceMaterialName = materialItem.getMaterialProfile().getSourceMaterial().name();
        this.resourceTypeName = materialItem.getMaterialProfile().getResourceType().name();
    }

    public int getTier() {
        return tier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof SimplifiedCraftingItem other) {
            return tier == other.tier &&
                    Objects.equals(name, other.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, tier);
    }

    @Override
    public String getName() {
        if (resourceTypeName == null && sourceMaterialName == null) return super.getName();
        return Arrays.stream((this.sourceMaterialName + " " + this.resourceTypeName).trim().split("\\s+"))
                .map(SimplifiedCraftingItem::capitalize)
                .collect(Collectors.joining(" "));
    }

    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}