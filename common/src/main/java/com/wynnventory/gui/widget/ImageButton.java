package com.wynnventory.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wynntils.utils.mc.McUtils;
import com.wynntils.utils.render.RenderUtils;
import com.wynnventory.gui.Sprite;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ImageButton extends WynnventoryButton {
    private final Sprite sprite;
    private final Button.OnPress onPress;

    public ImageButton(int x, int y, int width, int height, Sprite sprite, Button.OnPress onPress, Component tooltip) {
        super(x, y, width, height, tooltip);
        this.sprite = sprite;
        this.onPress = onPress;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(getX(), getY(), 0);

        int spriteStart = isHovered() ? sprite.width() / 2 : 0;

        RenderUtils.drawTexturedRect(
                pose,
                sprite.resource(),
                0,
                0,
                0,
                getWidth(),
                getHeight(),
                spriteStart,
                0,
                sprite.width() / 2,
                sprite.height(),
                sprite.width(),
                sprite.height());
        pose.popPose();

        if (isHovered() && !getMessage().equals(Component.empty())) {
            graphics.renderTooltip(McUtils.mc().font, getMessage(), mouseX, mouseY);
        }
    }

    @Override
    public void onPress() {
        this.onPress.onPress(null);
    }
}
