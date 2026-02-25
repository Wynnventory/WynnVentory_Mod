package com.wynnventory.events;

import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.bus.api.Event;

public class RaidLobbyScreenRenderEvent extends Event {
    private final GuiGraphics graphics;
    private final int mouseX;
    private final int mouseY;
    private final float delta;

    public RaidLobbyScreenRenderEvent(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        this.graphics = graphics;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.delta = delta;
    }

    public GuiGraphics getGraphics() {
        return graphics;
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    public float getDelta() {
        return delta;
    }
}
