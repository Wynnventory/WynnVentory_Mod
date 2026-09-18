package com.wynnventory.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wynnventory.api.response.ApiEnvelope;
import com.wynnventory.api.response.ApiError;
import com.wynnventory.api.response.PoolGroup;
import com.wynnventory.api.response.PoolItem;
import com.wynnventory.api.response.PoolResponse;
import com.wynnventory.core.WynnventoryMod;
import com.wynnventory.model.item.simple.SimpleGambitItem;
import com.wynnventory.model.item.simple.SimpleItem;
import com.wynnventory.model.item.trademarket.TrademarketItemSummary;
import com.wynnventory.model.item.trademarket.TrademarketListing;
import com.wynnventory.model.item.trademarket.prediction.PricePredictionRequest;
import com.wynnventory.model.item.trademarket.prediction.PricePredictionResponse;
import com.wynnventory.model.reward.RewardPool;
import com.wynnventory.model.reward.RewardPoolDocument;
import com.wynnventory.model.reward.RewardType;
import com.wynnventory.util.HttpUtils;
import java.net.URI;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class WynnventoryApi {
    // /api/v2 responses only change additively, so an unknown field must never break a read.
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public void sendGambitData(Set<SimpleGambitItem> gambits) {
        if (gambits.isEmpty()) return;

        WynnventoryMod.logDebug("Sending gambit data to {} endpoint.", WynnventoryMod.isBeta() ? "DEV" : "PROD");
        URI uri = Endpoint.RAIDPOOL_GAMBITS.uri();
        HttpUtils.sendPostRequest(uri, gambits);
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

            HttpUtils.sendPostRequest(uri, doc);
        }
    }

    public void sendTradeMarketData(Set<TrademarketListing> trademarketItems) {
        URI uri = Endpoint.TRADE_MARKET_ITEMS.uri();
        HttpUtils.sendPostRequest(uri, trademarketItems);
        WynnventoryMod.logDebug("Trying to send {} trademarket items", trademarketItems.size());
    }

    public CompletableFuture<TrademarketItemSummary> fetchItemPrice(String name, Integer tier, Boolean shiny) {
        if (name == null || name.isBlank()) {
            return CompletableFuture.completedFuture(null);
        }

        return fetchPriceStats(Endpoint.TRADE_MARKET_PRICE.uri(HttpUtils.encode(name)), tier, shiny);
    }

    public CompletableFuture<TrademarketItemSummary> fetchHistoricItemPrice(String name, Integer tier, Boolean shiny) {
        if (name == null || name.isBlank()) {
            return CompletableFuture.completedFuture(null);
        }

        return fetchPriceStats(Endpoint.TRADE_MARKET_HISTORIC_PRICE.uri(HttpUtils.encode(name)), tier, shiny);
    }

    public CompletableFuture<PricePredictionResponse> fetchPricePrediction(PricePredictionRequest request) {
        if (request == null || request.getName() == null || request.getName().isBlank()) {
            return CompletableFuture.completedFuture(null);
        }

        URI uri = Endpoint.PRICE_PREDICTION.uri();

        return HttpUtils.sendPostRequestWithApiKey(uri, request, ApiConfig.getWynnmarketApiKey())
                .thenApply(this::handlePricePredictionResponse)
                .exceptionally(ex -> {
                    WynnventoryMod.logError("Failed to fetch price prediction", ex);
                    return null;
                });
    }

    private CompletableFuture<TrademarketItemSummary> fetchPriceStats(URI baseUri, Integer tier, Boolean shiny) {
        Map<String, Object> params = new LinkedHashMap<>();
        // v2 validates tier >= 1; untiered items (tier 0) are queried without the parameter.
        params.put("tier", tier != null && tier > 0 ? tier : null);
        params.put("shiny", shiny);

        URI uri = HttpUtils.withQueryParams(baseUri, params);

        return HttpUtils.sendGetRequest(uri)
                .thenApply(resp -> handleEnvelopedResponse(uri, resp, TrademarketItemSummary.class, null))
                .exceptionally(ex -> {
                    WynnventoryMod.logError("Failed to fetch item price", ex);
                    return null;
                });
    }

    public CompletableFuture<List<RewardPoolDocument>> fetchRewardPools(RewardType type) {
        URI uri = type == RewardType.LOOTRUN ? Endpoint.LOOTPOOL_CURRENT.uri() : Endpoint.RAIDPOOL_CURRENT.uri();

        return HttpUtils.sendGetRequest(uri)
                // A 404 means no pool is stored for this week yet: an empty result, not a failed one.
                .thenApply(resp -> handleEnvelopedResponse(uri, resp, PoolResponse.class, PoolResponse.EMPTY))
                .thenApply(pool -> pool == null ? null : toRewardPoolDocuments(pool))
                .exceptionally(ex -> {
                    WynnventoryMod.logError("Failed to fetch reward pools for type " + type, ex);
                    return null;
                });
    }

    private List<RewardPoolDocument> toRewardPoolDocuments(PoolResponse pool) {
        List<RewardPoolDocument> documents = new ArrayList<>();

        for (PoolGroup group : pool.groups()) {
            RewardPool rewardPool = RewardPool.fromFullName(group.name());
            if (rewardPool == null) {
                WynnventoryMod.logDebug("Ignoring unknown reward pool '{}'", group.name());
                continue;
            }

            List<SimpleItem> items = new ArrayList<>();
            for (PoolItem item : group.items()) {
                SimpleItem mapped = item.toSimpleItem();
                if (mapped == null) {
                    WynnventoryMod.logDebug(
                            "Ignoring pool item '{}' with unknown item type '{}'", item.name(), item.itemType());
                    continue;
                }
                items.add(mapped);
            }

            documents.add(new RewardPoolDocument(items, rewardPool));
        }

        return documents;
    }

    /**
     * Unwraps a /api/v2 response: the {@code data} payload on 200, {@code notFound} on 404 (the normal
     * "no data" answer), null for anything else after logging the v2 error.
     */
    private <T> T handleEnvelopedResponse(URI uri, HttpResponse<String> resp, Class<T> type, T notFound) {
        if (resp == null) return null;

        int status = resp.statusCode();
        if (status == 200) {
            WynnventoryMod.logDebug("API response: {}", resp.body());
            try {
                JsonNode data = ApiEnvelope.data(MAPPER, resp.body());
                if (data == null) {
                    WynnventoryMod.logError("API response from {} has no data envelope: {}", uri, resp.body());
                    return null;
                }
                return MAPPER.treeToValue(data, type);
            } catch (JsonProcessingException e) {
                WynnventoryMod.logError("Failed to parse API response from {}: {}", uri, resp.body(), e);
                return null;
            }
        }

        if (status == 404) {
            WynnventoryMod.logDebug("No data at {}", uri);
            return notFound;
        }

        ApiError error = ApiEnvelope.error(MAPPER, resp.body());
        if (error != null) {
            WynnventoryMod.logError("API error ({}) at {}: {} - {}", status, uri, error.code(), error.message());
        } else {
            WynnventoryMod.logError("API error ({}) at {}: {}", status, uri, resp.body());
        }
        return null;
    }

    private PricePredictionResponse handlePricePredictionResponse(HttpResponse<String> resp) {
        if (resp == null) return null;

        if (resp.statusCode() != 200) {
            WynnventoryMod.logError("Price prediction error ({}): {}", resp.statusCode(), resp.body());
            return null;
        }

        try {
            return MAPPER.readValue(resp.body(), PricePredictionResponse.class);
        } catch (JsonProcessingException e) {
            WynnventoryMod.logError("Failed to parse price prediction response {}", resp.body(), e);
            return null;
        }
    }
}
