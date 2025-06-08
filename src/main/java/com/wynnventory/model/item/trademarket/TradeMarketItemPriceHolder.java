package com.wynnventory.model.item.trademarket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TradeMarketItemPriceHolder {
    private TradeMarketItemPriceInfo priceInfo;
    private final String itemName;
    private final Instant timestamp;

    public TradeMarketItemPriceHolder(TradeMarketItemPriceInfo priceInfo, String itemName) {
        this.priceInfo = priceInfo;
        this.itemName = itemName;
        this.timestamp = Instant.now();
    }

    public void setPriceInfo(TradeMarketItemPriceInfo priceInfo) {
        this.priceInfo = priceInfo;
    }

    public String getItemName() {
        return itemName;
    }

    public TradeMarketItemPriceInfo getPriceInfo() {
        return priceInfo;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public boolean isPriceExpired(long minutes) {
        Instant now = Instant.now();
        return now.isAfter(timestamp.plusSeconds(minutes * 60));
    }
}
