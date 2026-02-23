package com.wynnventory.gui.widget;

import com.wynntils.utils.colors.CustomColor;
import com.wynntils.utils.render.RenderUtils;
import com.wynnventory.gui.Sprite;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

public class ImageButton extends WynnventoryButton {
    private final Sprite sprite;
    private final Button.OnPress onPress;

    public ImageButton(int x, int y, int width, int height, Sprite sprite, Button.OnPress onPress, Component tooltip) {
        super(x, y, width, height, "");
        this.sprite = sprite;
        this.onPress = onPress;
        if (tooltip != null) {
            this.setTooltip(Tooltip.create(tooltip));
        }
    }

    @Override
    public void onPress() {
        this.onPress.onPress(null);
    }

    @Override
    public void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        int spriteStart = isHovered() ? sprite.width() / 2 : 0;

        RenderUtils.drawTexturedRect(
                graphics.pose(),
                sprite.resource(),
                CustomColor.NONE,
                getX(),
                getY(),
                getWidth(),
                getHeight(),
                spriteStart,
                0,
                sprite.width() / 2f,
                sprite.height(),
                sprite.width(),
                sprite.height());
    }
}
