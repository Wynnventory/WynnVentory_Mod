package com.wynnventory.feature.crowdsource;

import com.wynntils.models.containers.type.ContainerBounds;
import com.wynntils.models.items.WynnItem;
import com.wynntils.models.items.items.gui.GambitItem;
import com.wynntils.utils.wynn.ItemUtils;
import com.wynnventory.events.RaidLobbyPopulatedEvent;
import com.wynnventory.events.RewardPreviewOpenedEvent;
import com.wynnventory.events.TrademarketTooltipRenderedEvent;
import com.wynnventory.model.container.LootrunRewardPreviewLayout;
import com.wynnventory.model.container.RaidRewardPreviewLayout;
import com.wynnventory.model.item.simple.SimpleGambitItem;
import com.wynnventory.model.item.simple.SimpleItem;
import com.wynnventory.model.item.trademarket.TrademarketListing;
import com.wynnventory.model.reward.RewardPool;
import com.wynnventory.util.ItemStackUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;

public class CrowdSourceFeature {
    private int lastHandledLootContentId = -2;
    private Map<Integer, ItemStack> lastHandledLootItems = Map.of();
    private ItemStack lastHoveredMarketItem;

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

    @SubscribeEvent
    public void onTrademarketTooltipRendered(TrademarketTooltipRenderedEvent event) {
        ItemStack hoveredItem = getItemFromSlot(event.getItemSlot());
        if (hoveredItem == null) return;

        TrademarketListing listing = TrademarketListing.from(hoveredItem);
        if (listing == null) return;

        QueueScheduler.TRADEMARKET_QUEUE.addItem(listing);
    }

    @SubscribeEvent
    public void onRaidLobbyPopulated(RaidLobbyPopulatedEvent event) {
        for (ItemStack stack : event.getItems()) {
            WynnItem wynnItem = ItemStackUtils.getWynnItem(stack);
            if (wynnItem instanceof GambitItem item) {
                QueueScheduler.GAMBIT_QUEUE.addItem(new SimpleGambitItem(item));
            }
        }
    }

    private static List<SimpleItem> getStacksInBounds(Map<Integer, ItemStack> packetItems, ContainerBounds bounds) {
        List<SimpleItem> containerItems = new ArrayList<>();

        for (Map.Entry<Integer, ItemStack> e : packetItems.entrySet()) {
            if (!bounds.getSlots().contains(e.getKey())) continue;
            SimpleItem simpleItem = ItemStackUtils.toSimpleItem(e.getValue());

            if (simpleItem != null) containerItems.add(simpleItem);
        }

        return containerItems;
    }

    private ItemStack getItemFromSlot(Slot slot) {
        if (slot.container instanceof Inventory) return null;

        ItemStack hoveredItem = slot.getItem();
        if (lastHoveredMarketItem == hoveredItem) return hoveredItem;

        lastHoveredMarketItem = hoveredItem;

        return hoveredItem;
    }

    private boolean isDuplicate(RewardPreviewOpenedEvent event) {
        int containerId = event.getContainerId();
        var items = event.getItems();

        if (containerId == lastHandledLootContentId
                && ItemUtils.isItemListsEqual(
                        items.values().stream().toList(),
                        lastHandledLootItems.values().stream().toList())) {
            return true;
        }

        lastHandledLootContentId = containerId;
        lastHandledLootItems = items;
        return false;
    }
}
