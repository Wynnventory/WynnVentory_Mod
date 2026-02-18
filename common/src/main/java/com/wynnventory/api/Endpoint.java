package com.wynnventory.api;

import java.net.URI;

public enum Endpoint {
    TRADE_MARKET_ITEMS("trademarket/items"),
    LOOTPOOL_ITEMS("lootpool/items"),
    LOOTPOOL_CURRENT("lootpool/current"),
    RAIDPOOL_ITEMS("raidpool/items"),
    RAIDPOOL_CURRENT("raidpool/current"),
    RAIDPOOL_GAMBITS("raidpool/gambits"),
    TRADE_MARKET_PRICE("trademarket/item/%s/price"),
    TRADE_MARKET_HISTORIC_PRICE("trademarket/history/%s/price");

    private final String template;

    Endpoint(String template) {
        this.template = template;
    }

    public URI uri(Object... args) {
        String path = String.format(template, args);
        return ApiConfig.baseUri().resolve(path);
    }
}
