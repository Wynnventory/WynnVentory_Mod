package com.wynnventory.queue;

import com.wynnventory.core.WynnventoryMod;
import com.wynnventory.model.item.simple.SimpleItem;
import com.wynnventory.model.reward.RewardPool;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class RewardPoolQueue {

    private final Map<RewardPool, Set<SimpleItem>> pools = new ConcurrentHashMap<>();

    public void addItems(RewardPool pool, Collection<SimpleItem> items) {
        Set<SimpleItem> poolItems = pools.computeIfAbsent(pool, k -> ConcurrentHashMap.newKeySet());

        poolItems.addAll(items);
        WynnventoryMod.logInfo("Collected {} items for RewardPool {}", poolItems.size(), pool.getShortName());
    }

    public Map<RewardPool, Set<SimpleItem>> drainAll() {
        if (pools.isEmpty()) return Map.of();

        Map<RewardPool, Set<SimpleItem>> out = new HashMap<>();
        for (Map.Entry<RewardPool, Set<SimpleItem>> e : pools.entrySet()) {
            Set<SimpleItem> set = e.getValue();
            if (set == null || set.isEmpty()) continue;
            out.put(e.getKey(), new HashSet<>(set));
        }

        pools.clear();
        return out;
    }
}
