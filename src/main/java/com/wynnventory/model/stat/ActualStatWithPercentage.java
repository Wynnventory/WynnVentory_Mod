package com.wynnventory.model.stat;

import com.wynntils.features.tooltips.ItemStatInfoFeature;
import com.wynntils.handlers.tooltip.type.TooltipStyle;
import com.wynntils.models.stats.StatCalculator;
import com.wynntils.models.stats.StatListOrderer;
import com.wynntils.models.stats.type.StatActualValue;
import com.wynntils.models.stats.type.StatPossibleValues;
import com.wynntils.utils.colors.CustomColor;
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

    public String getRollPercentageColor() {
        try {
            int colorValue = new ItemStatInfoFeature().getDecorator().getSuffix(statActualValue, possibleValues, new TooltipStyle(null, false, false, false, false)).getStyle().getColor().getValue();
            return String.format("#%06X", 0xFFFFFF & colorValue);
        } catch (Exception e) {
            return null;
        }
    }

    public int getStars() {
        return statActualValue.stars();
    }

    public RangedValue getInternalRoll() {
        return statActualValue.internalRoll();
    }

    public String getRollPercentage() {
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
        return Objects.hash(statActualValue, getRollPercentage());
    }


    @Override
    public String toString() {
        return "statName=" + statActualValue.statType().getKey() + ", actualValue=" + statActualValue.value() + ", rollPercent=" + getRollPercentage() + ", minRange=" + possibleValues.range().low() + ", maxRange=" + possibleValues.range().high();
    }
}