package com.wynnventory.model.item.simplified;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wynntils.models.gear.GearModel;
import com.wynntils.models.gear.type.GearInstance;
import com.wynntils.models.items.WynnItemData;
import com.wynntils.models.items.items.game.GearItem;
import com.wynntils.models.stats.type.ShinyStat;
import com.wynntils.models.stats.type.StatActualValue;
import com.wynntils.models.stats.type.StatPossibleValues;
import com.wynnventory.model.stat.ActualStatWithPercentage;
import com.wynnventory.util.IconManager;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SimplifiedGearItem extends SimplifiedItem {
    private final boolean unidentified;
    private final int rerollCount;
    private final Optional<ShinyStat> shinyStat;
    private final float overallRollPercentage;
    private final List<ActualStatWithPercentage> actualStatsWithPercentage = new ArrayList<>();

    public SimplifiedGearItem(GearItem item) {
        super(item.getName(),
                item.getGearTier().getName(),
                "GearItem",
                item.getGearType().name(),
                IconManager.getIcon(item.getName()));

        this.unidentified = item.isUnidentified();
        this.rerollCount = item.getRerollCount();
        this.overallRollPercentage = item.getOverallPercentage();

        GearInstance gearInstance = new GearModel().parseInstance(item.getItemInfo(), (ItemStack) item.getData().get(WynnItemData.ITEMSTACK_KEY));
        this.shinyStat = gearInstance.shinyStat();

        final List<StatActualValue> actualValues = item.getIdentifications();
        final List<StatPossibleValues> possibleValues = item.getPossibleValues();

        for (StatActualValue actual : actualValues) {
            StatPossibleValues possibleValue = possibleValues.stream().filter(p -> p.statType().getKey().equals(actual.statType().getKey())).findFirst().orElse(null);
            if(possibleValue != null) {
                actualStatsWithPercentage.add(new ActualStatWithPercentage(actual, possibleValue));
            }
        }
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

