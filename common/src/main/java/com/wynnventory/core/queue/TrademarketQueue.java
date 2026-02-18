package com.wynnventory.core.queue;

import com.wynnventory.core.WynnventoryMod;
import com.wynnventory.model.item.trademarket.TrademarketListing;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class TrademarketQueue {
    private final Set<TrademarketListing> items = ConcurrentHashMap.newKeySet();

    public void addItem(TrademarketListing listing) {
        boolean added = items.add(listing);

        if (added)
            WynnventoryMod.logDebug(
                    "Collected TM Listing for item: {} | price: {} | quantity: {}. New queued size: {}",
                    listing.getItem().getName(),
                    listing.getListingPrice(),
                    listing.getQuantity(),
                    items.size());
    }

    public Set<TrademarketListing> drainAll() {
        if (items.isEmpty()) return Set.of();

        Set<TrademarketListing> out = new HashSet<>(items);
        items.clear();
        return out;
    }
}
