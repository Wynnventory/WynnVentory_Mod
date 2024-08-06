package com.wynnventory.model.Item;

import com.wynntils.models.items.items.game.GearItem;

public class TradeMarketItem {
    private GearItem item;
    private int listingPrice;
    private int amount;

    public TradeMarketItem(GearItem item, int listingPrice, int amount) {
        this.item = item;
        this.listingPrice = listingPrice;
        this.amount = amount;
    }

    public GearItem getItem() {
        return item;
    }

    public int getListingPrice() {
        return listingPrice;
    }

    public int getAmount() {
        return amount;
    }
}
