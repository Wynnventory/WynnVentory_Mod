package com.wynnventory.api;

import java.net.URI;

public enum Endpoint {
    // Data submission stays on the legacy surface: /api/v2 is read-only.
    TRADE_MARKET_ITEMS("trademarket/items"),
    LOOTPOOL_ITEMS("lootpool/items"),
    RAIDPOOL_ITEMS("raidpool/items"),
    RAIDPOOL_GAMBITS("raidpool/gambits"),
    // Reads use the standardized /api/v2 surface (WynnVentory_Web/docs/API_V2.md).
    LOOTPOOL_CURRENT("v2/lootpools/current"),
    RAIDPOOL_CURRENT("v2/raidpools/current"),
    TRADE_MARKET_PRICE("v2/market/items/%s/price"),
    TRADE_MARKET_HISTORIC_PRICE("v2/market/items/%s/history/latest"),
    PRICE_PREDICTION("https://wynnmarket.com/api/estimate");

    private final String template;

    Endpoint(String template) {
        this.template = template;
    }

    public URI uri(Object... args) {
        String path = String.format(template, args);
        return ApiConfig.baseUri().resolve(path);
    }
}
