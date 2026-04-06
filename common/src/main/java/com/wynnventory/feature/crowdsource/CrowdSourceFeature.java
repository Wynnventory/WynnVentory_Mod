package com.wynnventory.feature.crowdsource;

import com.wynntils.models.items.WynnItem;
import com.wynntils.models.items.items.gui.GambitItem;
import com.wynnventory.events.RaidLobbyPopulatedEvent;
import com.wynnventory.events.RewardPreviewOpenedEvent;
import com.wynnventory.events.TrademarketTooltipRenderedEvent;
import com.wynnventory.model.item.simple.SimpleGambitItem;
import com.wynnventory.model.item.trademarket.TrademarketListing;
import com.wynnventory.model.reward.RewardPool;
import com.wynnventory.util.ItemStackUtils;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;

public class CrowdSourceFeature {
    private ItemStack lastHoveredMarketItem;

    @SubscribeEvent
    public void onLootrunPreviewOpened(RewardPreviewOpenedEvent.Lootrun event) {
        QueueScheduler.LOOTRUN_QUEUE.addItem(
                RewardPool.fromTitle(event.getScreenTitle()), ItemStackUtils.toSimpleItem(event.getItem()));
    }

    @SubscribeEvent
    public void onRaidPreviewOpened(RewardPreviewOpenedEvent.Raid event) {
        QueueScheduler.RAID_QUEUE.addItem(
                RewardPool.fromTitle(event.getScreenTitle()), ItemStackUtils.toSimpleItem(event.getItem()));
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

    private ItemStack getItemFromSlot(Slot slot) {
        if (slot.container instanceof Inventory) return null;

        ItemStack hoveredItem = slot.getItem();
        if (lastHoveredMarketItem == hoveredItem) return hoveredItem;

        lastHoveredMarketItem = hoveredItem;
        return hoveredItem;
    }
}
