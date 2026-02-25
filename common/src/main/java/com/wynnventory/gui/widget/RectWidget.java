package com.wynnventory.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class RectWidget extends WynnventoryButton {
    private final int color;

    public RectWidget(int x, int y, int width, int height, int color) {
        super(x, y, width, height, Component.empty());
        this.color = color;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        graphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), color);
    }

    @Override
    public void onPress() {
        // Rect widgets are non-interactive
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return false;
    }
}
