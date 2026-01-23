package com.wynnventory.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wynnventory.core.WynnventoryMod;
import com.wynnventory.model.item.simple.SimpleGambitItem;
import com.wynnventory.model.item.simple.SimpleItem;
import com.wynnventory.model.item.trademarket.TrademarketItemSummary;
import com.wynnventory.model.item.trademarket.TrademarketListing;
import com.wynnventory.model.reward.RewardPool;
import com.wynnventory.model.reward.RewardPoolDocument;
import com.wynnventory.util.HttpUtils;

import java.net.URI;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class WynnventoryApi  {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule());


    public void sendGambitData(Set<SimpleGambitItem> gambits) {
        if (gambits.isEmpty()) return;

        WynnventoryMod.logDebug("Sending gambit data to {} endpoint.", WynnventoryMod.isDev() ? "DEV" : "PROD");
        URI uri = Endpoint.RAIDPOOL_GAMBITS.uri();
        post(uri, gambits);
        WynnventoryMod.logDebug("Submitted {} gambit items to API: {}", gambits.size(), uri);
    }

    public void sendRewardPoolData(Map<RewardPool, Set<SimpleItem>> drainedPools, Endpoint endpoint) {
        URI uri = endpoint.uri();

        for (Map.Entry<RewardPool, Set<SimpleItem>> entry : drainedPools.entrySet()) {
            RewardPool pool = entry.getKey();
            Set<SimpleItem> itemsSet = entry.getValue();
            if (pool == null || itemsSet == null || itemsSet.isEmpty()) continue;

            RewardPoolDocument doc = new RewardPoolDocument(new ArrayList<>(itemsSet), pool.getFullName(), pool.getType().name());
            WynnventoryMod.logDebug("Trying to send {} items for RewardPool {}", itemsSet.size(), pool.getShortName());

            HttpUtils.sendPostRequest(uri, serialize(doc));
        }
    }

    public void sendTradeMarketData(Set<TrademarketListing> trademarketItems) {
        URI uri = Endpoint.TRADE_MARKET_ITEMS.uri();
        HttpUtils.sendPostRequest(uri, serialize(trademarketItems));
        WynnventoryMod.logDebug("Trying to send {} trademarket items", trademarketItems.size());
    }

    public CompletableFuture<TrademarketItemSummary> fetchItemPrice(String name, Integer tier, Boolean shiny) {
        if (name == null || name.isBlank()) {
            return CompletableFuture.completedFuture(null);
        }

        URI baseUri = Endpoint.TRADE_MARKET_PRICE.uri(HttpUtils.encode(name));

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("tier", tier);
        params.put("shiny", shiny);

        URI uri = HttpUtils.withQueryParams(baseUri, params);

        return HttpUtils.sendGetRequest(uri)
                .thenApply(resp -> handleResponse(resp, this::parsePriceInfoResponse))
                .exceptionally(ex -> {
                    WynnventoryMod.logError("Failed to fetch item price", ex);
                    return null;
                });
    }

    // TODO: fetchLootpools (RAID or LOOTPOOL)

    // TODO: fetchLatestHistoricItemPrice (by name)

    // TODO: fetchLatestHistoricItemPrice (by name and tier)

    // TODO: parsePriceInfoResponse

    // TODO: parseLootpoolResponse

    private <T> T handleResponse(HttpResponse<String> resp, Function<String, T> on200) {
        if (resp.statusCode() == 200) {
            WynnventoryMod.logDebug("API response: {}", resp.body());
            return on200.apply(resp.body());
        } else {
            WynnventoryMod.logError("API error ({}): {}", resp.statusCode(), resp.body());
        }

        return null;
    }

    private void post(URI uri, Object payload) {
        HttpUtils.sendPostRequest(uri, serialize(payload));
    }

    private String serialize(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            WynnventoryMod.logError("Serialization failed", e);
            return "[]";
        }
    }

    private TrademarketItemSummary parsePriceInfoResponse(String responseBody) {
        try {
            return MAPPER.readValue(responseBody, TrademarketItemSummary.class);
        } catch (JsonProcessingException e) {
            WynnventoryMod.logError("Failed to parse item price response {}", responseBody, e);
        }

        return null;
    }
}