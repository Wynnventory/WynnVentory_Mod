package com.wynnventory.model.stat;

import com.wynntils.models.stats.StatCalculator;
import com.wynntils.models.stats.type.StatActualValue;
import com.wynntils.models.stats.type.StatPossibleValues;
import com.wynntils.utils.type.RangedValue;

import java.util.Objects;

public class ActualStatWithPercentage {
    private final StatActualValue statActualValue;
    private final StatPossibleValues possibleValues;

    public ActualStatWithPercentage(StatActualValue statActualValue, StatPossibleValues possibleValues) {
        this.statActualValue = statActualValue;
        this.possibleValues = possibleValues;
    }

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
        if(possibleValues == null) return null;

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

    public String getActualRollPercentage() {
        if (possibleValues == null) return "NaN";
        float percent = StatCalculator.getPercentage(statActualValue, possibleValues);
        if (Float.isInfinite(percent) || Float.isNaN(percent)) {
            return "NaN";
        }
        return StatCalculator.getPercentage(statActualValue, possibleValues)+"%";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof ActualStatWithPercentage other) {
            return Objects.equals(statActualValue, other.statActualValue);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getApiName(), getActualRollPercentage());
    }


    @Override
    public String toString() {
        return "statName=" + statActualValue.statType().getKey() + ", actualValue=" + statActualValue.value() + ", actualValuePercent=" + getActualRollPercentage() + ", minRange=" + possibleValues.range().low() + ", maxRange=" + possibleValues.range().high();
    }
}