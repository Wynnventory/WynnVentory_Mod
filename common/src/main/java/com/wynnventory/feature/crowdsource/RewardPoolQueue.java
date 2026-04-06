package com.wynnventory.feature.crowdsource;

import com.wynnventory.core.WynnventoryMod;
import com.wynnventory.model.item.simple.SimpleItem;
import com.wynnventory.model.reward.RewardPool;
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

    public void addItem(RewardPool pool, SimpleItem item) {
        if (item == null) return;
        pools.get(pool).add(item);

        WynnventoryMod.logInfo(
                "Collected {} items for RewardPool {}", pools.get(pool).size(), pool.getShortName());
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
