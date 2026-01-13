package com.wynnventory.model.item.simple;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wynntils.models.gear.GearModel;
import com.wynntils.models.gear.type.GearInstance;
import com.wynntils.models.items.WynnItemData;
import com.wynntils.models.items.items.game.GearItem;
import com.wynntils.models.stats.type.ShinyStat;
import com.wynntils.models.stats.type.StatActualValue;
import com.wynntils.models.stats.type.StatPossibleValues;
import com.wynnventory.model.item.ItemStat;
import com.wynnventory.util.IconManager;
import net.minecraft.world.item.ItemStack;

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

    public SimpleGearItem(GearItem item) {
        super();

        this.name = item.getName();
        this.rarity = item.getGearTier().getName();
        this.itemType = "GearItem";
        this.type = item.getGearType().name();
        this.icon = IconManager.getIcon(item.getName());
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
                actualStatsWithPercentage.add(new ItemStat(actual, possibleValue));
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

    public List<ItemStat> getActualStatsWithPercentage() {
        return actualStatsWithPercentage;
    }

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
