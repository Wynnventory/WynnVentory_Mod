package com.wynnventory.model.item.trademarket;

import com.wynntils.core.components.Models;
import com.wynntils.models.gear.type.GearInfo;
import com.wynntils.models.items.items.game.GearBoxItem;
import com.wynnventory.api.TrademarketPriceDictionary;
import com.wynnventory.model.item.simple.SimpleGearItem;
import com.wynnventory.model.item.simple.SimpleItem;
import com.wynnventory.model.item.simple.SimpleTierItem;
import com.wynnventory.util.ItemStackUtils;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public record TrademarketItemSnapshot(TrademarketItemSummary live, TrademarketItemSummary historic) {

    public boolean hasHistoricData() {
        return historic != null;
    }

    public boolean isExpired() {
        return live != null && live.isExpired();
    }

    public static TrademarketItemSnapshot resolveSnapshot(ItemStack itemStack) {
        SimpleItem simpleItem = ItemStackUtils.toSimpleItem(itemStack);

        return switch (simpleItem) {
            case SimpleGearItem gearItem    -> TrademarketPriceDictionary.INSTANCE.getItem(gearItem.getName(), gearItem.isShiny());
            case SimpleTierItem tierItem    -> TrademarketPriceDictionary.INSTANCE.getItem(tierItem.getName(), tierItem.getTier());
            case SimpleItem item            -> TrademarketPriceDictionary.INSTANCE.getItem(item.getName());
            case null                       -> null;
        };
    }

    // ... existing code ...
    public static Map<GearInfo, TrademarketItemSnapshot> resolveGearBoxItem(GearBoxItem item) {
        Map<GearInfo, TrademarketItemSnapshot> snapshots = new HashMap<>();
        for(GearInfo info : Models.Gear.getPossibleGears(item)) {
            TrademarketItemSnapshot snapshot = TrademarketPriceDictionary.INSTANCE.getItem(info.name(), false);
            snapshots.put(info, snapshot);
        }

        return snapshots.entrySet().stream()
                .sorted(java.util.Comparator
                        .comparing((Map.Entry<GearInfo, TrademarketItemSnapshot> e) -> getAvg80(e.getValue()))
                        .reversed()
                        .thenComparing(e -> getUnidAvg80(e.getValue()), java.util.Comparator.reverseOrder())
                        .thenComparing(e -> e.getKey().name(), java.util.Comparator.nullsLast(String::compareToIgnoreCase))
                )
                .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        java.util.LinkedHashMap::new
                ));
    }

    private static Double getAvg80(TrademarketItemSnapshot snapshot) {
        return getPriceOrNegativeInfinity(snapshot, TrademarketPriceSummary::getAverageMid80PercentPrice);
    }

    private static Double getUnidAvg80(TrademarketItemSnapshot snapshot) {
        return getPriceOrNegativeInfinity(snapshot, TrademarketPriceSummary::getUnidentifiedAverageMid80PercentPrice);
    }

    private static Double getPriceOrNegativeInfinity(TrademarketItemSnapshot snapshot, Function<TrademarketPriceSummary, Double> extractor) {
        if (snapshot == null || snapshot.live() == null || snapshot.live().getPriceInfo() == null) {
            return Double.NEGATIVE_INFINITY;
        }

        Double value = extractor.apply(snapshot.live().getPriceInfo());
        return value != null ? value : Double.NEGATIVE_INFINITY;
    }
}
