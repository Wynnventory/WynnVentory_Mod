package com.wynnventory.handler;

import com.wynntils.utils.wynn.ItemUtils;
import com.wynnventory.event.LootrunPreviewOpenedEvent;
import com.wynnventory.model.container.LootrunRewardPreviewLayout;
import com.wynnventory.model.reward.RewardPool;
import com.wynnventory.queue.QueueManager;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public final class LootRewardHandler {
    private int lastHandledContentId = -2;
    private List<ItemStack> lastHandledItems = List.of();

    @SubscribeEvent
    public void onHandleContainerContent(LootrunPreviewOpenedEvent event) {
        if (isDuplicate(event)) return;

        RewardPool pool = RewardPool.fromTitle(event.getScreenTitle());
        List<ItemStack> rewardStacks = getStacksInBounds(event.getItems());

        QueueManager.lootrun().addItems(pool, rewardStacks);
    }

    private static List<ItemStack> getStacksInBounds(List<ItemStack> items) {
        List<ItemStack> containerItems = new ArrayList<>();
        for (int slot : LootrunRewardPreviewLayout.BOUNDS.getSlots()) {
            if (slot < 0 || slot >= items.size()) continue;
            ItemStack s = items.get(slot);
            if (s == null || s.isEmpty()) continue;
            containerItems.add(s);
        }
        return containerItems;
    }

    private boolean isDuplicate(LootrunPreviewOpenedEvent event) {
        int containerId = event.getContainerId();
        var items = event.getItems();

        if (containerId == lastHandledContentId && ItemUtils.isItemListsEqual(items, lastHandledItems)) {
            return true;
        }

        lastHandledContentId = containerId;
        lastHandledItems = items;
        return false;
    }

}