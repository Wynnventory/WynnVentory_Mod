package com.wynnventory.api;

import com.wynnventory.model.item.trademarket.TrademarketItemSnapshot;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public final class TrademarketPriceDictionary {
    public static final TrademarketPriceDictionary INSTANCE =  new TrademarketPriceDictionary();

    private static final WynnventoryApi API = new WynnventoryApi();
    private final ConcurrentHashMap<Integer, TrademarketItemSnapshot> prices = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, CompletableFuture<TrademarketItemSnapshot>> inFlight = new ConcurrentHashMap<>();

    private TrademarketPriceDictionary() {}

    public TrademarketItemSnapshot getItem(String name) {
        return getOrFetch(name, null, false);
    }

    public TrademarketItemSnapshot getItem(String name, int tier) {
        return getOrFetch(name, tier, false);
    }

    public TrademarketItemSnapshot getItem(String name, boolean shiny) {
        return getOrFetch(name, null, shiny);
    }

    private TrademarketItemSnapshot getOrFetch(String name, Integer tier, boolean shiny) {
        if (name == null || name.isBlank()) return null;

        int key = generateHash(name, tier, shiny);

        TrademarketItemSnapshot cached = prices.get(key);
        if (cached != null && !cached.isExpired()) {
            return cached;
        }

        inFlight.computeIfAbsent(key, k ->
                fetchSnapshot(name, tier, shiny)
                        .whenComplete((snapshot, ex) -> cacheAndCleanup(k, snapshot))
        );

        return cached;
    }

    private void cacheAndCleanup(int key, TrademarketItemSnapshot snapshot) {
        if (snapshot != null) {
            prices.put(key, snapshot);
        }
        inFlight.remove(key);
    }


    private CompletableFuture<TrademarketItemSnapshot> fetchSnapshot(String name, Integer tier, boolean shiny) {
        var liveF = API.fetchItemPrice(name, tier, shiny);
        var histF = API.fetchHistoricItemPrice(name, tier, shiny);

        return liveF.thenCombine(histF, TrademarketItemSnapshot::new)
                .thenApply(snapshot -> {
                    if (snapshot != null) {
                        int key = generateHash(name, tier, shiny);
                        prices.put(key, snapshot);
                    }
                    return snapshot;
                })
                .exceptionally(ex -> null);
    }

    public int generateHash(String name, Integer tier,  boolean shiny) {
        return Objects.hash(name, tier, shiny);
    }
}
