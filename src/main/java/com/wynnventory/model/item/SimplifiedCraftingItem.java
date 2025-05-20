package com.wynnventory.model.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wynntils.models.items.items.game.IngredientItem;
import com.wynntils.models.items.items.game.MaterialItem;
import com.wynnventory.util.IconManager;
import com.wynnventory.util.StringUtils;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SimplifiedCraftingItem extends SimplifiedItem {
    private final int tier;
    @JsonIgnore
    private String sourceMaterialName;
    @JsonIgnore
    private String resourceTypeName;

    public SimplifiedCraftingItem(IngredientItem ingredientItem) {
        super(ingredientItem.getName(), null, "IngredientItem", ingredientItem.getIngredientInfo().professions().toString());
        this.tier = ingredientItem.getQualityTier();
        this.icon = IconManager.getIcon(ingredientItem.getName());
    }

    public SimplifiedCraftingItem(MaterialItem materialItem) {
        super("", null, "MaterialItem", materialItem.getProfessionTypes().toString());
        this.tier = materialItem.getQualityTier();
        this.sourceMaterialName = materialItem.getMaterialProfile().getSourceMaterial().name();
        this.resourceTypeName = materialItem.getMaterialProfile().getResourceType().name();
        this.icon = IconManager.getIcon(getName() + " " + tier);
    }

    public int getTier() {
        return tier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof SimplifiedCraftingItem other) {
            return tier == other.tier &&
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

    @Override
    public String getName() {
        if (resourceTypeName == null && sourceMaterialName == null) {
            return super.getName();
        }

        String combined = (sourceMaterialName + " " + resourceTypeName).trim();
        return StringUtils.toCamelCase(combined);
    }
}