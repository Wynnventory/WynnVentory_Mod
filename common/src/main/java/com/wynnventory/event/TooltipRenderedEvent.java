package com.wynnventory.event;

import net.minecraft.world.inventory.Slot;
import net.neoforged.bus.api.Event;

public class TooltipRenderedEvent extends Event {

    private final Slot itemSlot;

    public TooltipRenderedEvent(Slot itemSlot) {
        this.itemSlot = itemSlot;
    }

    public Slot getItemSlot() { return itemSlot; }
}