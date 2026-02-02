package com.wynnventory.model.item.trademarket;

import com.wynnventory.api.TrademarketPriceDictionary;
import com.wynnventory.model.item.simple.SimpleGearItem;
import com.wynnventory.model.item.simple.SimpleItem;
import com.wynnventory.model.item.simple.SimpleTierItem;
import com.wynnventory.util.ItemStackUtils;
import net.minecraft.world.item.ItemStack;

public record TrademarketItemSnapshot(TrademarketItemSummary live, TrademarketItemSummary historic) {

    public boolean hasHistoricData() {
        return historic != null;
    }

    public boolean isExpired() {
        return live != null && live.isExpired();
    }

    public static TrademarketItemSnapshot resolveSnapshot(ItemStack itemStack) {
        SimpleItem simpleItem = ItemStackUtils.toSimpleItem(itemStack);

        if (simpleItem == null) {
            return null;
        }

        return switch (simpleItem) {
            case SimpleGearItem gearItem ->
                    TrademarketPriceDictionary.INSTANCE.getItem(gearItem.getName(), gearItem.isShiny());
            case SimpleTierItem tierItem ->
                    TrademarketPriceDictionary.INSTANCE.getItem(tierItem.getName(), tierItem.getTier());
            default ->
                    TrademarketPriceDictionary.INSTANCE.getItem(simpleItem.getName());
        };
    }
}
