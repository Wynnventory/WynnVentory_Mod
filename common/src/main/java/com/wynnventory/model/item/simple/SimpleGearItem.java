package com.wynnventory.model.item.simple;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wynntils.models.stats.type.ShinyStat;
import com.wynnventory.model.item.Icon;
import com.wynnventory.model.item.ItemStat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SimpleGearItem extends SimpleItem {
    private final boolean unidentified;
    private final int rerollCount;
    private final Optional<ShinyStat> shinyStat;
    private final float overallRollPercentage;
    private final List<ItemStat> actualStatsWithPercentage = new ArrayList<>();
    private final boolean shiny;

    public SimpleGearItem(String name, String rarity, String itemType, String type, Icon icon, boolean unidentified, int rerollCount, Optional<ShinyStat> shinyStat, float overallRollPercentage, List<ItemStat> actualStatsWithPercentage) {
        this(name, rarity, itemType, type, icon, 1, unidentified, rerollCount, shinyStat, overallRollPercentage, actualStatsWithPercentage);
    }

    public SimpleGearItem(String name, String rarity, String itemType, String type, Icon icon, int amount, boolean unidentified, int rerollCount, Optional<ShinyStat> shinyStat, float overallRollPercentage, List<ItemStat> actualStatsWithPercentage) {
        super(name, rarity, itemType, type, icon, amount);
        this.unidentified = unidentified;
        this.rerollCount = rerollCount;
        this.shinyStat = shinyStat;
        this.overallRollPercentage = overallRollPercentage;
        this.actualStatsWithPercentage.addAll(actualStatsWithPercentage);
        this.shiny = shinyStat.isPresent();
    }

    public boolean isUnidentified() {
        return unidentified;
    }

    public Optional<ShinyStat> getShinyStat() {
        return shinyStat;
    }

    public float getOverallRollPercentage() {
        return overallRollPercentage;
    }

    public int getRerollCount() {
        return rerollCount;
    }

    public List<ItemStat> getActualStatsWithPercentage() {
        return actualStatsWithPercentage;
    }

    public boolean isShiny() { return shiny; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o instanceof SimpleGearItem other) {
            return unidentified == other.unidentified &&
                    Objects.equals(name, other.name) &&
                    Objects.equals(rarity, other.rarity) &&
                    Objects.equals(rerollCount, other.rerollCount) &&
                    Objects.equals(actualStatsWithPercentage, other.actualStatsWithPercentage) &&
                    Objects.equals(itemType, other.itemType) &&
                    Objects.equals(type, other.type) &&
                    Objects.equals(
                            shinyStat.map(s -> s.statType().key() + ":" + s.value()),
                            other.shinyStat.map(s -> s.statType().key() + ":" + s.value())
                    );
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                name,
                rarity,
                unidentified,
                rerollCount,
                shinyStat.map(s -> s.statType().key() + ":" + s.value()).orElse(null),
                actualStatsWithPercentage,
                itemType,
                type);
    }

}
