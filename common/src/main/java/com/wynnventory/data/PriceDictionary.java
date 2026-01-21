package com.wynnventory.data;

import com.wynnventory.api.WynnventoryApi;
import com.wynnventory.model.item.trademarket.CalculatedPriceItem;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class PriceDictionary {
    private static PriceDictionary instance;

    private WynnventoryApi api = new WynnventoryApi();
    private Set<CalculatedPriceItem> prices = new HashSet<>();

    private PriceDictionary() {}

    public static void get() {
        if (instance != null) return;

        synchronized (PriceDictionary.class) {
            if (instance == null) {
                instance = new PriceDictionary();
            }
        }
    }

    public CalculatedPriceItem getItem(String name) {
        return getItemInternal(name, null);
    }

    public CalculatedPriceItem getItem(String name, int tier) {
        return getItemInternal(name, tier);
    }

    private CalculatedPriceItem getItemInternal(String name, Integer tier) {
        Iterator<CalculatedPriceItem> it = prices.iterator();
        while (it.hasNext()) {
            CalculatedPriceItem data = it.next();

            boolean sameName = data.getItem().getName().equals(name);
            boolean sameTier = (tier == null) ? data.getTier() == null : tier.equals(data.getTier());

            if (sameName && sameTier) {
                if (data.isTimeValid()) {
                    return data;
                } else {
                    CalculatedPriceItem newData = (tier == null) ? api.fetchItemPrice(name) : api.fetchItemPrice(name, tier);

                    if (newData != null) {
                        it.remove();
                        prices.add(newData);
                        return newData;
                    }
                }
            }
        }

        return null;
    }
}
