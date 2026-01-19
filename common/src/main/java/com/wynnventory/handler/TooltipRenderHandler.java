package com.wynnventory.handler;

import com.wynnventory.core.WynnventoryMod;
import com.wynnventory.event.TooltipRenderedEvent;
import com.wynnventory.model.container.TrademarketContainer;
import com.wynnventory.model.item.simple.SimpleItem;
import com.wynnventory.model.item.trademarket.TradeMarketListing;
import com.wynnventory.queue.QueueManager;
import com.wynnventory.util.ContainerUtil;
import com.wynnventory.util.ItemStackUtils;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;

public final class TooltipRenderHandler {
    private ItemStack lastItem;

    @SubscribeEvent
    public void onTrademarketTooltipRendered(TooltipRenderedEvent.Trademarket event) {
        Slot hoveredItemSlot = event.getItemSlot();
        if (hoveredItemSlot.container instanceof Inventory) return;

        ItemStack hoveredItem = hoveredItemSlot.getItem();
        if (lastItem == hoveredItem) return;
        lastItem = hoveredItem;

        TradeMarketListing listing = TradeMarketListing.from(hoveredItem);
        if (listing == null) return;

        QueueManager.TRADEMARKET_QUEUE.addItem(listing);
    }

    @SubscribeEvent
    public void onTooltipRendered(TooltipRenderedEvent.Any event) {
        SimpleItem simpleItem = ItemStackUtils.toSimpleItem(event.getItemSlot().getItem());

        if(simpleItem == null) return;

        WynnventoryMod.logDebug("Tooltip rendered for item: {}", simpleItem.getName());
    }
}