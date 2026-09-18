package com.wynnventory.api.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wynntils.models.gear.type.GearTier;
import com.wynntils.models.stats.type.ShinyStat;
import com.wynnventory.model.item.Icon;
import com.wynnventory.model.item.simple.SimpleGearItem;
import com.wynnventory.model.item.simple.SimpleItem;
import com.wynnventory.model.item.simple.SimpleItemType;
import com.wynnventory.model.item.simple.SimpleTierItem;
import java.util.List;
import java.util.Optional;

/**
 * One item of a v2 pool group. Vocabulary is the v2 one: lowercase {@code rarity} and {@code subtype},
 * snake_case {@code item_type} labels.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PoolItem(
        String name,
        int amount,
        String rarity,
        @JsonProperty("item_type") String itemType,
        String subtype,
        Integer tier,
        boolean shiny,
        @JsonProperty("shiny_stat") Optional<ShinyStat> shinyStat,
        Icon icon) {
    /**
     * Maps this item onto the domain model the reward screen works with, choosing the subclass by
     * {@code item_type}. Returns null when the label is not one the mod knows.
     */
    public SimpleItem toSimpleItem() {
        SimpleItemType type = SimpleItemType.fromApiLabel(itemType);
        if (type == null) return null;

        // GearTier.fromString is case-insensitive but not null-safe; the constructors default null to NORMAL.
        GearTier gearTier = rarity == null ? null : GearTier.fromString(rarity);

        return switch (type) {
            case GEAR, CHARM ->
                new SimpleGearItem(name, gearTier, type, subtype, icon, amount, false, 0, shinyStat, 0f, List.of());
            case INGREDIENT, MATERIAL, POWDER, AMPLIFIER, MOUNT, EMERALD_POUCH ->
                new SimpleTierItem(name, gearTier, type, subtype, icon, amount, tier == null ? 0 : tier);
            default -> new SimpleItem(name, gearTier, type, subtype, icon, amount);
        };
    }
}
