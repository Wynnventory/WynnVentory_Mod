package com.wynnventory.core.queue;

import com.wynnventory.core.WynnventoryMod;
import com.wynnventory.model.item.simple.SimpleGambitItem;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class GambitQueue {
    private final Set<SimpleGambitItem> gambits = ConcurrentHashMap.newKeySet();

    public void addItem(SimpleGambitItem gambit) {
        boolean added = gambits.add(gambit);

        if (added)
            WynnventoryMod.logInfo("Adding {} to gambit queue. New queue size: {}", gambit.getName(), gambits.size());
    }

    public Set<SimpleGambitItem> drainAll() {
        if (gambits.isEmpty()) return Set.of();

        Set<SimpleGambitItem> out = new HashSet<>(gambits);
        gambits.clear();
        return out;
    }
}
