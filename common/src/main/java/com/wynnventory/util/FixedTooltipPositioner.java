package com.wynnventory.util;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import org.joml.Vector2i;
import org.joml.Vector2ic;

public final class FixedTooltipPositioner implements ClientTooltipPositioner {
    private final Vector2i pos;

    public FixedTooltipPositioner(int x, int y) {
        this.pos = new Vector2i(x, y);
    }

    @Override
    public Vector2ic positionTooltip(
            int screenWidth,
            int screenHeight,
            int mouseX,
            int mouseY,
            int tooltipWidth,
            int tooltipHeight
    ) {
        return pos;
    }
}