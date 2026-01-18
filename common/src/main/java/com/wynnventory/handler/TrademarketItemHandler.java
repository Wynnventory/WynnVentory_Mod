package com.wynnventory.handler;

import com.wynnventory.event.TooltipRenderedEvent;
import com.wynnventory.model.container.TrademarketContainer;
import com.wynnventory.model.item.trademarket.TradeMarketListing;
import com.wynnventory.queue.QueueManager;
import com.wynnventory.util.ContainerUtil;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;

public final class TrademarketItemHandler {
    private ItemStack lastItem;

    @SubscribeEvent
    public void onTooltipRendered(TooltipRenderedEvent event) {
        ContainerUtil container = ContainerUtil.current();
        if (container == null) return;
        if (!TrademarketContainer.matchesTitle(container.title)) return;

        Slot hoveredItemSlot = event.getItemSlot();
        if (hoveredItemSlot.container instanceof Inventory) return;

        ItemStack hoveredItem = hoveredItemSlot.getItem();
        if (lastItem == hoveredItem) return;
        lastItem = hoveredItem;

        TradeMarketListing listing = TradeMarketListing.from(hoveredItem);
        if (listing == null) return;

        QueueManager.TRADEMARKET_QUEUE.addItem(listing);
    }
}