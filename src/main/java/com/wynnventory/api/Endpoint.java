package com.wynnventory.api;

import java.net.URI;

public enum Endpoint {
    TRADE_MARKET_ITEMS         ("trademarket/items"),
    LOOTPOOL_ITEMS             ("lootpool/items"),
    LOOTPOOL_CURRENT           ("lootpool/current"),
    RAIDPOOL_ITEMS             ("raidpool/items"),
    RAIDPOOL_CURRENT           ("raidpool/current"),
    TRADE_MARKET_PRICE         ("trademarket/item/%s/price?tier=%d"),
    TRADE_MARKET_HISTORY_LATEST("trademarket/history/%s/price?tier=%d");

    private final String template;
    Endpoint(String template) { this.template = template; }

    public URI uri(Object... args) {
        String path = String.format(template, args);
        return ApiConfig.baseUri().resolve(path);
    }
}