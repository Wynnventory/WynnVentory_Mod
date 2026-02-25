package com.wynnventory.gui.widget;

import com.wynntils.utils.colors.CustomColor;
import com.wynntils.utils.render.RenderUtils;
import com.wynnventory.gui.Sprite;
import net.minecraft.client.gui.GuiGraphics;

public class ImageWidget extends WynnventoryButton {
    private final Sprite sprite;

    public ImageWidget(int x, int y, int width, int height, Sprite sprite) {
        super(x, y, width, height, "");
        this.sprite = sprite;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        RenderUtils.drawTexturedRectWithColor(
                graphics.pose(),
                sprite.resource(),
                CustomColor.NONE,
                getX(),
                getY(),
                getWidth(),
                getHeight(),
                0,
                0,
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
