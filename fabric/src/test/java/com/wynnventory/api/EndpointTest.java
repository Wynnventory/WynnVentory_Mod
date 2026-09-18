package com.wynnventory.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class EndpointTest {
    @Test
    void readEndpointsResolveUnderTheV2Surface() {
        assertEquals(
                "https://www.wynnventory.com/api/v2/market/items/Divzer/price",
                Endpoint.TRADE_MARKET_PRICE.uri("Divzer").toString());
        assertEquals(
                "https://www.wynnventory.com/api/v2/market/items/Divzer/history/latest",
                Endpoint.TRADE_MARKET_HISTORIC_PRICE.uri("Divzer").toString());
        assertEquals(
                "https://www.wynnventory.com/api/v2/lootpools/current",
                Endpoint.LOOTPOOL_CURRENT.uri().toString());
        assertEquals(
                "https://www.wynnventory.com/api/v2/raidpools/current",
                Endpoint.RAIDPOOL_CURRENT.uri().toString());
    }

    @Test
    void submissionEndpointsStayOnTheLegacySurface() {
        assertEquals(
                "https://www.wynnventory.com/api/trademarket/items",
                Endpoint.TRADE_MARKET_ITEMS.uri().toString());
        assertEquals(
                "https://www.wynnventory.com/api/lootpool/items",
                Endpoint.LOOTPOOL_ITEMS.uri().toString());
        assertEquals(
                "https://www.wynnventory.com/api/raidpool/items",
                Endpoint.RAIDPOOL_ITEMS.uri().toString());
        assertEquals(
                "https://www.wynnventory.com/api/raidpool/gambits",
                Endpoint.RAIDPOOL_GAMBITS.uri().toString());
    }

    @Test
    void externalEndpointsAreAbsolute() {
        assertEquals(
                "https://wynnmarket.com/api/estimate",
                Endpoint.PRICE_PREDICTION.uri().toString());
    }
}
