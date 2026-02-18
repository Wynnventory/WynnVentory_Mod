package com.wynnventory.events;

import net.minecraft.world.inventory.Slot;
import net.neoforged.bus.api.Event;

public class TrademarketTooltipRenderedEvent extends Event {
    private final Slot itemSlot;

    public TrademarketTooltipRenderedEvent(Slot slot) {
        this.itemSlot = slot;
    }

    public Slot getItemSlot() {
        return itemSlot;
    }
}
