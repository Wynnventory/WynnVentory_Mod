package com.wynnventory.handler;

import com.wynntils.models.containers.type.ContainerBounds;
import com.wynntils.utils.wynn.ItemUtils;
import com.wynnventory.core.queue.QueueScheduler;
import com.wynnventory.events.RewardPreviewOpenedEvent;
import com.wynnventory.model.container.LootrunRewardPreviewLayout;
import com.wynnventory.model.container.RaidRewardPreviewLayout;
import com.wynnventory.model.item.simple.SimpleItem;
import com.wynnventory.model.reward.RewardPool;
import com.wynnventory.util.ItemStackUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;

public final class LootRewardHandler {
    private int lastHandledContentId = -2;
    private List<ItemStack> lastHandledItems = List.of();

    @SubscribeEvent
    public void onLootrunPreviewOpened(RewardPreviewOpenedEvent.Lootrun event) {
        if (isDuplicate(event)) return;

        QueueScheduler.LOOTRUN_QUEUE.addItems(
                RewardPool.fromTitle(event.getScreenTitle()),
                getStacksInBounds(event.getItems(), LootrunRewardPreviewLayout.BOUNDS));
    }

    @SubscribeEvent
    public void onRaidPreviewOpened(RewardPreviewOpenedEvent.Raid event) {
        if (isDuplicate(event)) return;

        QueueScheduler.RAID_QUEUE.addItems(
                RewardPool.fromTitle(event.getScreenTitle()),
                getStacksInBounds(event.getItems(), RaidRewardPreviewLayout.BOUNDS));
    }

    private static List<SimpleItem> getStacksInBounds(List<ItemStack> packetItems, ContainerBounds bounds) {
        List<SimpleItem> containerItems = new ArrayList<>();
        for (int slot : bounds.getSlots()) {
            if (slot < 0 || slot >= packetItems.size()) continue;
            SimpleItem simpleItem = ItemStackUtils.toSimpleItem(packetItems.get(slot));
            if (simpleItem == null) continue;
            containerItems.add(simpleItem);
        }
        return containerItems;
    }

    private boolean isDuplicate(RewardPreviewOpenedEvent event) {
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
