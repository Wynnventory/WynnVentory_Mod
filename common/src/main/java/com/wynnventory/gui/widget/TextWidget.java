package com.wynnventory.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;

public class TextWidget extends WynnventoryButton {
    private final Component text;
    private final int color;
    private final float scale;

    public TextWidget(int x, int y, Component text) {
        this(x, y, text, 0xFFFFFFFF);
    }

    public TextWidget(int x, int y, Component text, int color) {
        this(x, y, text, color, 1.0f);
    }

    public TextWidget(int x, int y, Component text, int color, float scale) {
        super(x, y, (int) (Minecraft.getInstance().font.width(text) * scale), (int) (9 * scale), "");
        this.text = text;
        this.color = color;
        this.scale = scale;
    }

    @Override
    public void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        graphics.pose().pushMatrix();
        graphics.pose().translate((float) getX(), (float) getY());
        graphics.pose().scale(scale, scale);
        graphics.drawString(Minecraft.getInstance().font, text, 0, 0, color);
        graphics.pose().popMatrix();
    }

    @Override
    public void onPress(InputWithModifiers input) {
        // Text widgets are non-interactive
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        // No sound for text widgets
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return false;
    }

    @Override
    public boolean isHovered() {
        return false;
    }
}
