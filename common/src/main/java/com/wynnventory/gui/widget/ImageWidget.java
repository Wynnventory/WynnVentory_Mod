package com.wynnventory.gui.widget;

import com.wynntils.utils.render.RenderUtils;
import com.wynnventory.gui.Sprite;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class ImageWidget extends WynnventoryButton {
    private final Sprite sprite;

    public ImageWidget(int x, int y, int width, int height, Sprite sprite) {
        super(x, y, width, height, Component.empty());
        this.sprite = sprite;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        int spriteStart = isHovered() ? sprite.width() / 2 : 0;
        RenderUtils.drawTexturedRect(
                graphics.pose(),
                sprite.resource(),
                getX(),
                getY(),
                0,
                getWidth(),
                getHeight(),
                spriteStart,
                0,
                sprite.width(),
                sprite.height(),
                sprite.width(),
                sprite.height());
    }

    @Override
    public void onPress() {
        // Image widgets are non-interactive
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
