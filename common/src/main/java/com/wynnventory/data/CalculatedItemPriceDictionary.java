package com.wynnventory.data;

import com.wynnventory.api.WynnventoryApi;
import com.wynnventory.model.item.trademarket.CalculatedPriceItem;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class CalculatedItemPriceDictionary {
    private static CalculatedItemPriceDictionary instance;

    private final WynnventoryApi api = new WynnventoryApi();
    private final Set<CalculatedPriceItem> prices = new HashSet<>();

    private CalculatedItemPriceDictionary() {}

    public static CalculatedItemPriceDictionary get() {
        synchronized (CalculatedItemPriceDictionary.class) {
            if (instance == null) {
                instance = new CalculatedItemPriceDictionary();
            }
        }

        return instance;
    }

    public CalculatedPriceItem getItem(String name) {
        return getItemInternal(name, null);
    }

    public CalculatedPriceItem getItem(String name, int tier) {
        return getItemInternal(name, tier);
    }

    private CalculatedPriceItem getItemInternal(String name, Integer tier) {
        if (prices.isEmpty()) {
            CalculatedPriceItem fetched = (tier == null)
                    ? api.fetchItemPrice(name)
                    : api.fetchItemPrice(name, tier);

            if (fetched != null) {
                prices.add(fetched);
            }

            return fetched;
        }

        Iterator<CalculatedPriceItem> it = prices.iterator();
        while (it.hasNext()) {
            CalculatedPriceItem data = it.next();

            if (!matchesNameAndTier(data, name, tier)) {
                continue;
            }

            if (data.isTimeValid()) {
                return data;
            }

            CalculatedPriceItem newData = (tier == null)
                    ? api.fetchItemPrice(name)
                    : api.fetchItemPrice(name, tier);

            if (newData == null) {
                return null;
            }

            it.remove();
            prices.add(newData);
            return newData;
        }

        return null;
    }

    private boolean matchesNameAndTier(CalculatedPriceItem data, String name, Integer tier) {
        if (!data.getItem().getName().equals(name)) {
            return false;
        }
        return (tier == null) ? data.getTier() == null : tier.equals(data.getTier());
    }
}
