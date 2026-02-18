package com.wynnventory.core.queue;

import com.wynnventory.core.WynnventoryMod;
import com.wynnventory.model.item.simple.SimpleItem;
import com.wynnventory.model.reward.RewardPool;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class RewardPoolQueue {
    private final Map<RewardPool, Set<SimpleItem>> pools = new EnumMap<>(RewardPool.class);

    public RewardPoolQueue() {
        for (RewardPool pool : RewardPool.values()) {
            pools.put(pool, ConcurrentHashMap.newKeySet());
        }
    }

    public void addItems(RewardPool pool, Collection<SimpleItem> items) {
        Set<SimpleItem> poolItems = pools.get(pool);
        if (poolItems == null) return;

        poolItems.addAll(items);
        WynnventoryMod.logInfo("Collected {} items for RewardPool {}", poolItems.size(), pool.getShortName());
    }

    public Map<RewardPool, Set<SimpleItem>> drainAll() {
        Map<RewardPool, Set<SimpleItem>> out = new EnumMap<>(RewardPool.class);
        for (Map.Entry<RewardPool, Set<SimpleItem>> e : pools.entrySet()) {
            Set<SimpleItem> set = e.getValue();
            if (set.isEmpty()) continue;
            out.put(e.getKey(), new HashSet<>(set));
            set.clear();
        }

        return out;
    }
}
