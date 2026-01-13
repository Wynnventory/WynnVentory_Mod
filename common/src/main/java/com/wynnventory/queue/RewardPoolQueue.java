package com.wynnventory.queue;

import com.wynnventory.model.item.simple.SimpleItem;
import com.wynnventory.model.reward.RewardPool;
import com.wynnventory.util.ItemStackUtils;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class RewardPoolQueue {

    private final Map<RewardPool, Set<SimpleItem>> pools = new ConcurrentHashMap<>();

    public void addItems(RewardPool pool, Collection<ItemStack> items) {
        Set<SimpleItem> poolItems = pools.computeIfAbsent(pool, k -> ConcurrentHashMap.newKeySet());

        for (ItemStack stack : items) {
            if (stack == null || stack.isEmpty()) continue;
            poolItems.add(ItemStackUtils.toSimpleItem(stack));
        }

        for (SimpleItem simpleItem : poolItems) {
            System.out.println("Item collected: " + simpleItem.getName() + " | " + simpleItem.getAmount());
        }
    }
}
