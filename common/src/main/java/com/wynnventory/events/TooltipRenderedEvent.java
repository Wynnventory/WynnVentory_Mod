package com.wynnventory.events;

import net.minecraft.world.inventory.Slot;
import net.neoforged.bus.api.Event;

public abstract class TooltipRenderedEvent extends Event {
    private final Slot itemSlot;

    protected TooltipRenderedEvent(Slot slot) {
        this.itemSlot = slot;
    }

    public Slot getItemSlot() { return itemSlot; }

    public static class Trademarket extends TooltipRenderedEvent {
        public Trademarket(Slot slot) {
            super(slot);
        }
    }

    public static class PartyFinder extends TooltipRenderedEvent {
        public PartyFinder(Slot slot) {
            super(slot);
        }
    }
}