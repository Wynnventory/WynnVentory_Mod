package com.wynnventory.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;

public class TextWidget extends WynnventoryButton {
    private final Component text;
    private final int color;

    public TextWidget(int x, int y, Component text) {
        this(x, y, text, 0xFFFFFFFF);
    }

    public TextWidget(int x, int y, Component text, int color) {
        super(x, y, Minecraft.getInstance().font.width(text), 9, "");
        this.text = text;
        this.color = color;
    }

    @Override
    public void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        graphics.drawString(Minecraft.getInstance().font, text, getX(), getY(), color);
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
