package com.wynnventory.model.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wynntils.core.components.Models;
import com.wynntils.models.items.items.game.GearItem;
import com.wynntils.models.trademarket.type.TradeMarketPriceInfo;
import com.wynntils.utils.mc.McUtils;
import com.wynnventory.core.ModInfo;
import com.wynnventory.util.TradeMarketPriceParser;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.Optional;

public class TradeMarketGearItem extends TradeMarketItem {
    private final SimplifiedGearItem item;

    public TradeMarketGearItem(GearItem item, int listingPrice, int amount) {
        super(listingPrice, amount, McUtils.playerName(), ModInfo.VERSION);
        this.item = new SimplifiedGearItem(item);
    }

    public static TradeMarketGearItem createTradeMarketItem(ItemStack item) {
        Optional<GearItem> gearItemOptional = Models.Item.asWynnItem(item, GearItem.class);
        if(gearItemOptional.isPresent()) {
            GearItem gearItem = gearItemOptional.get();
            TradeMarketPriceInfo priceInfo = TradeMarketPriceParser.calculateItemPriceInfo(item);

            if (priceInfo != TradeMarketPriceInfo.EMPTY) {
                return new TradeMarketGearItem(gearItem, priceInfo.price(), priceInfo.amount());
            }
        }

        return null;
    }

    public SimplifiedGearItem getItem() {
        return item;
    }

    @JsonProperty("hash_code")
    public int getHashCode() {
        return hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof TradeMarketGearItem other) {
            return listingPrice == other.listingPrice &&
                    amount == other.amount &&
                    Objects.equals(item, other.item);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, listingPrice, amount);
    }

}
