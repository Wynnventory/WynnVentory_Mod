package com.wynnventory.gui.widget;

import com.wynntils.utils.colors.CustomColor;
import com.wynntils.utils.render.RenderUtils;
import com.wynnventory.gui.Sprite;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.InputWithModifiers;

public class ImageWidget extends WynnventoryButton {
    private final Sprite sprite;

    public ImageWidget(int x, int y, int width, int height, Sprite sprite) {
        super(x, y, width, height, "");
        this.sprite = sprite;
    }

    @Override
    public void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        RenderUtils.drawTexturedRect(
                graphics,
                sprite.resource(),
                CustomColor.NONE,
                getX(),
                getY(),
                getWidth(),
                getHeight(),
                0,
                0,
                sprite.width(),
                sprite.height(),
                sprite.width(),
                sprite.height());
    }

    @Override
    public void onPress(InputWithModifiers input) {
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
