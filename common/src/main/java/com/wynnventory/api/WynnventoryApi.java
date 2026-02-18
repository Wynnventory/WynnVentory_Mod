package com.wynnventory.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
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
import com.wynnventory.model.reward.RewardType;
import com.wynnventory.util.HttpUtils;
import java.net.URI;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class WynnventoryApi {
    private static final ObjectMapper MAPPER =
            new ObjectMapper().registerModule(new Jdk8Module()).registerModule(new JavaTimeModule());

    public void sendGambitData(Set<SimpleGambitItem> gambits) {
        if (gambits.isEmpty()) return;

        WynnventoryMod.logDebug("Sending gambit data to {} endpoint.", WynnventoryMod.isBeta() ? "DEV" : "PROD");
        URI uri = Endpoint.RAIDPOOL_GAMBITS.uri();
        HttpUtils.sendPostRequest(uri, serialize(gambits));
        WynnventoryMod.logDebug("Submitted {} gambit items to API: {}", gambits.size(), uri);
    }

    public void sendRewardPoolData(Map<RewardPool, Set<SimpleItem>> drainedPools, Endpoint endpoint) {
        URI uri = endpoint.uri();

        for (Map.Entry<RewardPool, Set<SimpleItem>> entry : drainedPools.entrySet()) {
            RewardPool pool = entry.getKey();
            Set<SimpleItem> itemsSet = entry.getValue();
            if (pool == null || itemsSet == null || itemsSet.isEmpty()) continue;

            RewardPoolDocument doc = new RewardPoolDocument(new ArrayList<>(itemsSet), pool);
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

        return getTrademarketItemSummaryCompletableFuture(tier, shiny, baseUri);
    }

    public CompletableFuture<TrademarketItemSummary> fetchHistoricItemPrice(String name, Integer tier, Boolean shiny) {
        if (name == null || name.isBlank()) {
            return CompletableFuture.completedFuture(null);
        }

        URI baseUri = Endpoint.TRADE_MARKET_HISTORIC_PRICE.uri(HttpUtils.encode(name));

        return getTrademarketItemSummaryCompletableFuture(tier, shiny, baseUri);
    }

    private CompletableFuture<TrademarketItemSummary> getTrademarketItemSummaryCompletableFuture(
            Integer tier, Boolean shiny, URI baseUri) {
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

    public CompletableFuture<List<RewardPoolDocument>> fetchRewardPools(RewardType type) {
        URI uri = type == RewardType.LOOTRUN ? Endpoint.LOOTPOOL_CURRENT.uri() : Endpoint.RAIDPOOL_CURRENT.uri();

        return HttpUtils.sendGetRequest(uri)
                .thenApply(resp -> handleResponse(resp, this::parseRewardPoolResponse))
                .exceptionally(ex -> {
                    WynnventoryMod.logError("Failed to fetch reward pools for type " + type, ex);
                    return null;
                });
    }

    private List<RewardPoolDocument> parseRewardPoolResponse(String responseBody) {
        if (responseBody == null || responseBody.isBlank()) return Collections.emptyList();
        try {
            JsonNode node = MAPPER.readTree(responseBody);
            if (node != null && node.isObject() && node.has("regions")) {
                node = node.get("regions");
            }

            if (node == null || node.isNull()) return Collections.emptyList();

            return MAPPER.readValue(
                    node.traverse(),
                    MAPPER.getTypeFactory().constructCollectionType(List.class, RewardPoolDocument.class));
        } catch (Exception e) {
            WynnventoryMod.logError("Failed to parse reward pool response {}", responseBody, e);
        }

        return Collections.emptyList();
    }

    private <T> T handleResponse(HttpResponse<String> resp, Function<String, T> on200) {
        if (resp != null && resp.statusCode() == 200) {
            WynnventoryMod.logDebug("API response: {}", resp.body());
            return on200.apply(resp.body());
        } else if (resp != null) {
            WynnventoryMod.logError("API error ({}): {}", resp.statusCode(), resp.body());
        }

        return null;
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
