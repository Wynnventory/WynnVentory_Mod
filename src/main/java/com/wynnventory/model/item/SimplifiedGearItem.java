package com.wynnventory.model.item;

import com.wynntils.models.items.items.game.GearItem;
import com.wynntils.models.stats.type.ShinyStat;
import com.wynntils.models.stats.type.StatActualValue;
import com.wynntils.models.stats.type.StatPossibleValues;
import com.wynnventory.model.stat.ActualStatWithPercentage;
import net.minidev.json.annotate.JsonIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SimplifiedGearItem extends SimplifiedItem {
    private final boolean unidentified;
    private final int rerollCount;
    private final Optional<ShinyStat> shinyStat;
    private final List<ActualStatWithPercentage> actualStatsWithPercentage = new ArrayList<>();

    public SimplifiedGearItem(GearItem item) {
        super(item.getName(), item.getGearTier().getName(), "GearItem", item.getGearType().name());
        this.unidentified = item.isUnidentified();
        this.rerollCount = item.getRerollCount();
        this.shinyStat = item.getShinyStat();

        final List<StatActualValue> actualValues = item.getIdentifications();
        final List<StatPossibleValues> possibleValues = item.getPossibleValues();

        for(StatActualValue actual : actualValues) {
            StatPossibleValues possibleValue = possibleValues.stream().filter(p -> p.statType().getKey().equals(actual.statType().getKey())).findFirst().orElse(null);
            actualStatsWithPercentage.add(new ActualStatWithPercentage(actual, possibleValue));
        }
    }

    public boolean isUnidentified() {
        return unidentified;
    }

    public Optional<ShinyStat> getShinyStat() {
        return shinyStat;
    }

    @JsonIgnore
    public int getRerollCount() {
        return rerollCount;
    }

    @JsonIgnore
    public List<ActualStatWithPercentage> getActualStatsWithPercentage() {
        return actualStatsWithPercentage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o instanceof SimplifiedGearItem other) {
            return unidentified == other.unidentified &&
                    Objects.equals(name, other.name) &&
                    Objects.equals(rarity, other.rarity) &&
                    Objects.equals(shinyStat, other.shinyStat) &&
                    Objects.equals(rerollCount, other.rerollCount) &&
                    Objects.equals(actualStatsWithPercentage, other.actualStatsWithPercentage);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, rarity, unidentified, rerollCount, shinyStat, actualStatsWithPercentage);
    }

}

