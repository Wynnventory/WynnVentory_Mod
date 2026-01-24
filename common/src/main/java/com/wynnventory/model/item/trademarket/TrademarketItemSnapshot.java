package com.wynnventory.model.item.trademarket;

public record TrademarketItemSnapshot(TrademarketItemSummary live, TrademarketItemSummary historic) {

    public boolean hasHistoricData() {
        return historic != null;
    }

    public boolean isExpired() {
        return live != null && live.isExpired();
    }
}
