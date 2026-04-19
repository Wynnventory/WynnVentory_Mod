package com.wynnventory.model.item.simple;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wynntils.models.gear.GearModel;
import com.wynntils.models.gear.type.GearTier;
import com.wynntils.models.items.WynnItemData;
import com.wynntils.models.items.items.game.GearItem;
import com.wynntils.models.stats.type.ShinyStat;
import com.wynntils.models.stats.type.StatActualValue;
import com.wynntils.models.stats.type.StatPossibleValues;
import com.wynnventory.api.service.IconService;
import com.wynnventory.model.item.Icon;
import com.wynnventory.model.item.ItemStat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.world.item.ItemStack;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SimpleGearItem extends SimpleItem {
    private boolean unidentified;
    private int rerollCount;
    private Optional<ShinyStat> shinyStat = Optional.empty();
    private float overallRollPercentage;
    private final List<ItemStat> actualStatsWithPercentage = new ArrayList<>();

    public SimpleGearItem() {
        super();
    }

    public SimpleGearItem(
            String name,
            GearTier rarity,
            String type,
            Icon icon,
            boolean unidentified,
            int rerollCount,
            Optional<ShinyStat> shinyStat,
            float overallRollPercentage,
            List<ItemStat> actualStatsWithPercentage) {
        this(
                name,
                rarity,
                type,
                icon,
                1,
                unidentified,
                rerollCount,
                shinyStat,
                overallRollPercentage,
                actualStatsWithPercentage);
    }

    public SimpleGearItem(
            String name,
            GearTier rarity,
            String type,
            Icon icon,
            int amount,
            boolean unidentified,
            int rerollCount,
            Optional<ShinyStat> shinyStat,
            float overallRollPercentage,
            List<ItemStat> actualStatsWithPercentage) {
        super(name, rarity, SimpleItemType.GEAR, type, icon, amount);
        this.unidentified = unidentified;
        this.rerollCount = rerollCount;
        this.shinyStat = shinyStat;
        this.overallRollPercentage = overallRollPercentage;
        this.actualStatsWithPercentage.addAll(actualStatsWithPercentage);
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

    public void setUnidentified(boolean unidentified) {
        this.unidentified = unidentified;
    }

    public void setRerollCount(int rerollCount) {
        this.rerollCount = rerollCount;
    }

    public void setShinyStat(Optional<ShinyStat> shinyStat) {
        this.shinyStat = shinyStat;
    }

    public void setOverallRollPercentage(float overallRollPercentage) {
        this.overallRollPercentage = overallRollPercentage;
    }

    public void setActualStatsWithPercentage(List<ItemStat> actualStatsWithPercentage) {
        this.actualStatsWithPercentage.clear();
        if (actualStatsWithPercentage != null) {
            this.actualStatsWithPercentage.addAll(actualStatsWithPercentage);
        }
    }

    public boolean isShiny() {
        return shinyStat.isPresent();
    }

    private List<ItemStat> getSortedStats() {
        return actualStatsWithPercentage.stream()
                .sorted(Comparator.comparing(s -> s.statActualValue().statType().getKey()))
                .toList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!super.equals(o)) return false;

        if (o instanceof SimpleGearItem other) {
            return unidentified == other.unidentified
                    && rerollCount == other.rerollCount
                    //                    && overallRollPercentage == other.overallRollPercentage
                    //                    && Objects.equals(actualStatsWithPercentage, other.actualStatsWithPercentage)
                    && Objects.equals(getSortedStats(), other.getSortedStats())
                    && Objects.equals(
                            shinyStat.map(s -> s.statType().key() + ":" + s.value()),
                            other.shinyStat.map(s -> s.statType().key() + ":" + s.value()));
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                super.hashCode(),
                unidentified,
                rerollCount,
                shinyStat.map(s -> s.statType().key() + ":" + s.value()).orElse(null),
                getSortedStats());
    }

    public static SimpleGearItem from(GearItem item) {
        String name = item.getName();
        ItemStack stack = item.getData().get(WynnItemData.ITEMSTACK_KEY);

        return new SimpleGearItem(
                name,
                item.getGearTier(),
                item.getGearType().name(),
                IconService.INSTANCE.resolveIcon(name, SimpleItemType.GEAR),
                stack.getCount(),
                item.isUnidentified(),
                item.getRerollCount(),
                new GearModel()
                        .parseInstance(item.getItemInfo(), stack, item.isUnidentified())
                        .getShinyStat(),
                item.getOverallPercentage(),
                getActualStats(item));
    }

    private static List<ItemStat> getActualStats(GearItem item) {
        final List<StatActualValue> actualValues = item.getIdentifications();
        final List<StatPossibleValues> possibleValues = item.getPossibleValues();

        return actualValues.stream()
                .map(actual -> possibleValues.stream()
                        .filter(p ->
                                p.statType().getKey().equals(actual.statType().getKey()))
                        .findFirst()
                        .map(possible -> new ItemStat(actual, possible))
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }
}
