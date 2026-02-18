package com.wynnventory.gui.widget;

import com.google.common.collect.Lists;
import com.wynntils.utils.colors.CommonColors;
import com.wynntils.utils.colors.CustomColor;
import com.wynntils.utils.mc.ComponentUtils;
import com.wynntils.utils.render.RenderUtils;
import com.wynnventory.gui.Sprite;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;

public class FilterButton extends WynnventoryButton {
    private final BooleanSupplier getter;
    private final Consumer<Boolean> setter;
    private final Runnable onToggle;
    private final Sprite icon;

    public FilterButton(
            int x,
            int y,
            int width,
            int height,
            String label,
            Sprite icon,
            BooleanSupplier getter,
            Consumer<Boolean> setter,
            Runnable onToggle) {
        super(x, y, width, height, label);
        this.icon = icon;
        this.getter = getter;
        this.setter = setter;
        this.onToggle = onToggle;
    }

    @Override
    public void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        boolean state = getter.getAsBoolean();

        // Render icon
        if (icon != null) {
            // Gray out if inactive
            CustomColor tint = state ? CustomColor.NONE : CustomColor.fromChatFormatting(ChatFormatting.DARK_GRAY);
            RenderUtils.drawTexturedRect(
                    graphics,
                    icon.resource(),
                    tint,
                    getX(),
                    getY(),
                    getWidth(),
                    getHeight(),
                    0,
                    0,
                    icon.width(),
                    icon.height(),
                    icon.width(),
                    icon.height());
        }

        // Hover effect
        if (isHovered()) {
            RenderUtils.drawRect(graphics, CommonColors.WHITE.withAlpha(0.3f), getX(), getY(), getWidth(), getHeight());

            graphics.setTooltipForNextFrame(
                    Lists.transform(
                            ComponentUtils.wrapTooltips(
                                    List.of(Component.translatable("gui.wynnventory.reward.button.filter", getLabel())),
                                    200),
                            Component::getVisualOrderText),
                    mouseX,
                    mouseY);
        }
    }

    @Override
    public void onPress(InputWithModifiers input) {
        setter.accept(!getter.getAsBoolean());
        onToggle.run();
    }

    public void onPress() {
        setter.accept(!getter.getAsBoolean());
        onToggle.run();
    }
}
