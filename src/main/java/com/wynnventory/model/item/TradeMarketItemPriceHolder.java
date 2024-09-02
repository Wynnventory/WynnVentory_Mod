package com.wynnventory.model.item;

import com.wynntils.models.gear.type.GearInfo;
import com.wynntils.models.items.items.game.GearItem;

import java.time.Instant;

public class TradeMarketItemPriceHolder {
    private TradeMarketItemPriceInfo priceInfo;
    private final GearInfo info;
    private final Instant timestamp;

    public TradeMarketItemPriceHolder(TradeMarketItemPriceInfo priceInfo, GearInfo info) {
        this.priceInfo = priceInfo;
        this.info = info;
        this.timestamp = Instant.now();
    }

    public void setPriceInfo(TradeMarketItemPriceInfo priceInfo) {
        this.priceInfo = priceInfo;
    }

    public GearInfo getInfo() {
        return info;
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
