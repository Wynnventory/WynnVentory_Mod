package com.wynnventory.ui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wynntils.utils.mc.McUtils;
import com.wynntils.utils.render.RenderUtils;
import com.wynnventory.enums.Sprite;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class WynnventoryImageButton extends AbstractWynnventoryButton {
    private final Sprite sprite;
    private final Runnable onClick;
    private String description;

    public WynnventoryImageButton(int x, int y, int width, int height, Sprite sprite, Runnable onClick) {
        super(x, y, width, height, Component.empty());
        this.sprite = sprite;
        this.onClick = onClick;
    }

    public WynnventoryImageButton(int x, int y, int width, int height, Sprite sprite, Runnable onClick, String description) {
        this(x, y, width, height, sprite, onClick);
        this.description = description;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(getX(), getY(), 0);

        int u = isHoveredOrFocused() ? sprite.width() / 2 : 0;
        RenderUtils.drawTexturedRect(
                pose,
                sprite.resource(),
                0, 0, 0,
                getWidth(), getHeight(),
                u, 0,
                sprite.width() / 2, sprite.height(),
                sprite.width(), sprite.height()
        );
        pose.popPose();

        if (isHovered() && description != null) {
            graphics.renderTooltip(
                   McUtils.mc().font,
                    Component.literal(description),
                    mouseX,
                    mouseY
            );
        }
    }

    @Override
    public void onPress() {
        onClick.run();
    }
}
