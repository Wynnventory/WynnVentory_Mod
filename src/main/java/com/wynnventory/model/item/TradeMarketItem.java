package com.wynnventory.model.item;

public class TradeMarketItem {
    protected final int listingPrice;
    protected final int amount;
    protected final String playerName;
    protected final String modVersion;

    protected TradeMarketItem(int listingPrice, int amount, String playerName, String modVersion) {
        this.listingPrice = listingPrice;
        this.amount = amount;
        this.playerName = playerName;
        this.modVersion = modVersion;
    }

    public int getListingPrice() {
        return listingPrice;
    }

    public int getAmount() {
        return amount;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getModVersion() {
        return modVersion;
    }
}
