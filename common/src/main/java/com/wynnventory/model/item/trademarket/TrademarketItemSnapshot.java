package com.wynnventory.model.item.trademarket;

import com.wynntils.core.components.Models;
import com.wynntils.models.gear.type.GearInfo;
import com.wynntils.models.items.items.game.GearBoxItem;
import com.wynnventory.api.service.TrademarketService;
import com.wynnventory.model.item.simple.SimpleGearItem;
import com.wynnventory.model.item.simple.SimpleItem;
import com.wynnventory.model.item.simple.SimpleTierItem;
import com.wynnventory.util.ItemStackUtils;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.item.ItemStack;

public record TrademarketItemSnapshot(TrademarketItemSummary live, TrademarketItemSummary historic) {
    public boolean hasHistoricData() {
        return historic != null;
    }

    public boolean isExpired() {
        return live != null && live.isExpired();
    }

    public static TrademarketItemSnapshot resolveSnapshot(ItemStack stack) {
        SimpleItem simpleItem = ItemStackUtils.toSimpleItem(stack);

        if (simpleItem == null || !simpleItem.getItemTypeEnum().isSellable()) return null;

        return switch (simpleItem) {
            case SimpleGearItem gearItem -> TrademarketService.INSTANCE.getItem(gearItem.getName(), gearItem.isShiny());
            case SimpleTierItem tierItem -> TrademarketService.INSTANCE.getItem(tierItem.getName(), tierItem.getTier());
            case SimpleItem item -> TrademarketService.INSTANCE.getItem(item.getName());
        };
    }

    public static Map<GearInfo, TrademarketItemSnapshot> resolveGearBoxItem(GearBoxItem item) {
        Map<GearInfo, TrademarketItemSnapshot> snapshots = new HashMap<>();
        for (GearInfo info : Models.Gear.getPossibleGears(item)) {
            TrademarketItemSnapshot snapshot = TrademarketService.INSTANCE.getItem(info.name(), false);
            snapshots.put(info, snapshot);
        }

        return snapshots.entrySet().stream()
                .filter(e -> e.getKey() != null && e.getValue() != null)
                .sorted(java.util.Comparator.comparing(
                                (Map.Entry<GearInfo, TrademarketItemSnapshot> e) ->
                                        PriceType.AVG_80.getValue(e.getValue().live()),
                                java.util.Comparator.nullsFirst(Double::compareTo))
                        .reversed()
                        .thenComparing(
                                e -> PriceType.UNID_AVG_80.getValue(e.getValue().live()),
                                java.util.Comparator.nullsFirst(Double::compareTo)
                                        .reversed())
                        .thenComparing(
                                e -> e.getKey().name(), java.util.Comparator.nullsLast(String::compareToIgnoreCase)))
                .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, java.util.LinkedHashMap::new));
    }

    public PriceData getPriceData(PriceType type) {
        return new PriceData(type.getValue(live), type.getValue(historic));
    }

    public record PriceData(Double live, Double historic) {}
}
