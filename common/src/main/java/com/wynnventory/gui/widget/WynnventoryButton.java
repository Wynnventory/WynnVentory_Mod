package com.wynnventory.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public abstract class WynnventoryButton extends AbstractButton {
    protected String label;

    protected WynnventoryButton(int x, int y, int width, int height, String label) {
        super(x, y, width, height, Component.literal(label));
        this.label = label;
    }

    @Override
    public void renderContents(GuiGraphics guiGraphics, int i, int j, float f) {
        renderDefaultSprite(guiGraphics);
        renderDefaultLabel(guiGraphics.textRendererForWidget(this, GuiGraphics.HoveredTextEffects.NONE));
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}

    protected String getLabel() {
        return this.label;
    }
}
