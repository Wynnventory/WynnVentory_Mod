package com.wynnventory.event;

import net.minecraft.world.inventory.Slot;
import net.neoforged.bus.api.Event;

public final class TrademarketTooltipRenderedEvent extends Event {

    private final Slot itemSlot;

    public TrademarketTooltipRenderedEvent(Slot itemSlot) {
        this.itemSlot = itemSlot;
    }

    public Slot getItemSlot() { return itemSlot; }
}