package com.wynnventory.model.item;

import com.wynntils.models.stats.StatCalculator;
import com.wynntils.models.stats.type.StatActualValue;
import com.wynntils.models.stats.type.StatPossibleValues;
import com.wynntils.utils.type.RangedValue;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public record ItemStat(StatActualValue statActualValue, StatPossibleValues possibleValues) {
    public String getDisplayName() {
        return statActualValue.statType().getDisplayName();
    }

    public String getApiName() {
        return statActualValue.statType().getApiName();
    }

    public int getStatRoll() {
        return statActualValue.value();
    }

    public RangedValue getStatRange() {
        if (possibleValues == null) return null;

        return possibleValues.range();
    }

    public String getUnit() {
        return statActualValue.statType().getUnit().name();
    }

    public int getStars() {
        return statActualValue.stars();
    }

    public RangedValue getInternalRoll() {
        return statActualValue.internalRoll();
    }

    public float getRollPercentage() {
        return StatCalculator.getPercentage(statActualValue, possibleValues);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof ItemStat other) {
            return Objects.equals(
                            statActualValue.statType().getKey(),
                            other.statActualValue.statType().getKey())
                    && Objects.equals(getRollPercentage(), other.getRollPercentage());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(statActualValue.statType().getKey(), getRollPercentage());
    }

    @Override
    public @NonNull String toString() {
        return "statName=" + statActualValue.statType().getKey() + ", actualValue=" + statActualValue.value()
                + ", rollPercent=" + getRollPercentage() + ", minRange="
                + possibleValues.range().low() + ", maxRange="
                + possibleValues.range().high();
    }
}
