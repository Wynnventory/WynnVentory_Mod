package com.wynnventory.model.item;

import com.wynntils.core.components.Models;
import com.wynntils.models.items.items.game.GearItem;
import com.wynntils.models.trademarket.type.TradeMarketPriceInfo;
import com.wynntils.utils.mc.McUtils;
import com.wynnventory.WynnventoryMod;
import com.wynnventory.util.TradeMarketPriceParser;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TradeMarketItem {
    private SimplifiedGearItem item;
    private int listingPrice;
    private int amount;
    private String playerName;
    private String modVersion;

    public TradeMarketItem(GearItem item, int listingPrice, int amount) {
        this.item = new SimplifiedGearItem(item);
        this.listingPrice = listingPrice;
        this.amount = amount;
        this.playerName = McUtils.playerName();
        this.modVersion = WynnventoryMod.WYNNVENTORY_VERSION;
    }

    public static List<TradeMarketItem> createTradeMarketItems(List<ItemStack> items) {
        List<TradeMarketItem> marketItems = new ArrayList<>();

        for (ItemStack item : items) {
            Optional<GearItem> gearItemOptional = Models.Item.asWynnItem(item, GearItem.class);
            gearItemOptional.ifPresent(gearItem -> {
                TradeMarketPriceInfo priceInfo = TradeMarketPriceParser.calculateItemPriceInfo(item);
                if (priceInfo != TradeMarketPriceInfo.EMPTY) {
                    marketItems.add(new TradeMarketItem(gearItem, priceInfo.price(), priceInfo.amount()));
                }
            });
        }

        return marketItems;
    }

    public static TradeMarketItem createTradeMarketItem(ItemStack item) {
        return createTradeMarketItems(List.of(item)).getFirst();
    }

    public SimplifiedGearItem getItem() {
        return item;
    }

    public int getListingPrice() {
        return listingPrice;
    }

    public int getAmount() {
        return amount;
    }

    public String getPlayerName() { return playerName; }

    public String getModVersion() { return modVersion; }
}
