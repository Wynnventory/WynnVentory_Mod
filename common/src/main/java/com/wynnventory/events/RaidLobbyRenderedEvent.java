package com.wynnventory.events;

import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.bus.api.Event;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class RaidLobbyRenderedEvent extends Event {
    private final GuiGraphics guiGraphics;
    private final int mouseX;
    private final int mouseY;
    private final float partialTick;
    private CallbackInfo ci;

    public RaidLobbyRenderedEvent(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        this.guiGraphics = guiGraphics;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.partialTick = partialTick;
        this.ci = ci;
    }

    public GuiGraphics getGuiGraphics() { return guiGraphics; }
    public int getMouseX() { return mouseX; }
    public int getMouseY() { return mouseY; }
    public float getPartialTick() { return partialTick; }
    public CallbackInfo getCi() { return ci; }
}
