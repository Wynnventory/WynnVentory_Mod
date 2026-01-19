package com.wynnventory.event;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

import java.util.List;

public abstract class TooltipRenderedEvent extends Event {

    private final Slot itemSlot;

    protected TooltipRenderedEvent(Slot itemSlot) {
        this.itemSlot = itemSlot;
    }

    public static class Trademarket extends TooltipRenderedEvent {
        public Trademarket(Slot slot) {
            super(slot);
        }
    }

    public static class Any extends TooltipRenderedEvent {
        private final GuiGraphics graphics;
        private final int mouseX;
        private final int mouseY;

        public Any(Slot slot, GuiGraphics graphics, int mouseX, int mouseY) {
            super(slot);
            this.graphics = graphics;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
        }

        public GuiGraphics getGuiGraphics() { return graphics; }
        public int getMouseX() { return mouseX; }
        public int getMouseY() { return mouseY; }
    }

    public Slot getItemSlot() { return itemSlot; }
}