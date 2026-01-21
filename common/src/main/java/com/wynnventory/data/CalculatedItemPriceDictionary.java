package com.wynnventory.data;

import com.wynnventory.api.WynnventoryApi;
import com.wynnventory.model.item.trademarket.CalculatedPriceItem;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public final class CalculatedItemPriceDictionary {
    public static final CalculatedItemPriceDictionary INSTANCE =  new CalculatedItemPriceDictionary();

    private static final WynnventoryApi API = new WynnventoryApi();
    private final ConcurrentHashMap<Integer, CalculatedPriceItem> prices = new ConcurrentHashMap<>();
    private final ConcurrentLinkedDeque<Integer> fetching = new ConcurrentLinkedDeque<>();

    private CalculatedItemPriceDictionary() {}

    public CalculatedPriceItem getItem(String name) {
        return getOrFetch(name, null, false);
    }

    public CalculatedPriceItem getItem(String name, int tier) {
        return getOrFetch(name, tier, false);
    }

    public CalculatedPriceItem getItem(String name, boolean shiny) {
        return getOrFetch(name, null, shiny);
    }

    private CalculatedPriceItem getOrFetch(String name, Integer tier, boolean shiny) {
        if (name == null || name.isBlank()) return null;

        int key = generateHash(name, tier, shiny);
        if (fetching.contains(key)) return null;

        CalculatedPriceItem cached = prices.get(key);
        if (cached != null && cached.isTimeValid()) {
            return cached;
        }

        fetching.push(key);
        API.fetchItemPrice(name, tier).thenAccept(fetched -> {
            if (fetched == null) return;
            prices.put(key, fetched);
            fetching.remove(key);
        }).exceptionally( ex -> {
                fetching.remove(key);
                return null;
        });

        return cached;
    }

    public int generateHash(String name, Integer tier,  boolean shiny) {
        return Objects.hash(name, tier, shiny);
    }
}
