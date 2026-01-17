package com.wynnventory.queue;

import com.wynnventory.core.WynnventoryMod;
import com.wynnventory.model.item.simple.SimpleItem;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class TrademarketQueue {

    private final Set<SimpleItem> items = ConcurrentHashMap.newKeySet();

    public void addItem(SimpleItem item) {
        boolean added = items.add(item);

        if (added) WynnventoryMod.logInfo("Collected trademarket item '{}'. New queued size: {}", item.getName(), items.size());
    }

    public Set<SimpleItem> drainAll() {
        if (items.isEmpty()) return Set.of();

        Set<SimpleItem> out = new HashSet<>(items);
        items.clear();
        return out;
    }
}
