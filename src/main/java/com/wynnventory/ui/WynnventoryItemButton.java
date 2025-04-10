package com.wynnventory.ui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wynntils.core.components.Services;
import com.wynntils.core.text.StyledText;
import com.wynntils.screens.guides.GuideItemStack;
import com.wynntils.screens.guides.aspect.GuideAspectItemStack;
import com.wynntils.screens.guides.gear.GuideGearItemStack;
import com.wynntils.screens.guides.powder.GuidePowderItemStack;
import com.wynntils.screens.guides.tome.GuideTomeItemStack;
import com.wynntils.utils.MathUtils;
import com.wynntils.utils.colors.CustomColor;
import com.wynntils.utils.mc.KeyboardUtils;
import com.wynntils.utils.render.FontRenderer;
import com.wynntils.utils.render.RenderUtils;
import com.wynntils.utils.render.Texture;
import com.wynntils.utils.render.type.HorizontalAlignment;
import com.wynntils.utils.render.type.TextShadow;
import com.wynnventory.util.HttpUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class WynnventoryItemButton<T extends GuideItemStack> extends WynnventoryButton {

    private static final int BASE_SIZE = 16;
    private final T itemStack;
    private final boolean shiny;

    public WynnventoryItemButton(int x, int y, int width, int height, T itemStack, boolean shiny) {
        super(x, y, width, height, Component.empty());
        this.itemStack = itemStack;
        this.shiny = shiny;

        buildTooltip();
    }

    @Override
    public void onPress() {}

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!KeyboardUtils.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) && !KeyboardUtils.isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT)) {
            return false;
        }

        String name = StyledText.fromComponent(itemStack.getHoverName()).getStringWithoutFormatting();

        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            Util.getPlatform().openUri("https://www.wynnventory.com/history/" + HttpUtil.encodeName(name));
            return true;
        } else if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            Services.Favorites.toggleFavorite(name);
            return true;
        }

        return false;
    }

    @Override
    public void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        PoseStack pose = g.pose();
        CustomColor color = getCustomColor();
        float scale = (float) getWidth() / BASE_SIZE;

        // Draw highlight background
        pose.pushPose();
        pose.translate(getX() - 1, getY() - 1, 0);
        pose.scale(scale, scale, scale);
        RenderUtils.drawTexturedRectWithColor(
                pose,
                Texture.HIGHLIGHT.resource(),
                color.withAlpha(1f),
                0, 0, 0,
                Math.round((getWidth() + 2) / scale),
                Math.round((getHeight() + 2) / scale),
                Texture.HIGHLIGHT.width(),
                Texture.HIGHLIGHT.height()
        );
        pose.popPose();

        // Draw item
        pose.pushPose();
        pose.translate(getX(), getY(), 0);
        pose.scale(scale, scale, scale);
        g.renderItem(itemStack, 0, 0);
        pose.popPose();

        // Draw overlays
        if (shiny) renderText("⬡", pose, CustomColor.fromChatFormatting(ChatFormatting.WHITE), scale);

        if (itemStack instanceof GuidePowderItemStack powder)
            renderText(MathUtils.toRoman(powder.getTier()), pose, color, scale);

        if (itemStack instanceof GuideAspectItemStack aspect)
            renderText(aspect.getAspectInfo().classType().getName().substring(0, 2), pose, color, scale);

        // Favorite icon
        if (Services.Favorites.isFavorite(itemStack)) {
            RenderUtils.drawScalingTexturedRect(
                    pose,
                    Texture.FAVORITE_ICON.resource(),
                    getX() + 12,
                    getY() - 4,
                    200,
                    getWidth() / 2f,
                    getHeight() / 2f,
                    Texture.FAVORITE_ICON.width(),
                    Texture.FAVORITE_ICON.height());
        }
    }

    private void renderText(String text, PoseStack poseStack, CustomColor color, float scale) {
        poseStack.pushPose();
        poseStack.translate(getX(), getY() - (getHeight() / 2f) + 4, 200);
        poseStack.scale(scale, scale, scale);

        FontRenderer.getInstance().renderAlignedTextInBox(
                poseStack,
                StyledText.fromString(text),
                0,
                getWidth(),
                0,
                0,
                color,
                HorizontalAlignment.LEFT,
                TextShadow.OUTLINE
        );

        poseStack.popPose();
    }

    private void buildTooltip() {
        if (itemStack instanceof GuideGearItemStack gear) {
            gear.buildTooltip();
        } else if (itemStack instanceof GuideTomeItemStack tome) {
            tome.buildTooltip();
        }
    }

    private CustomColor getCustomColor() {
        if (itemStack instanceof GuideGearItemStack gear)
            return CustomColor.fromChatFormatting(gear.getGearInfo().tier().getChatFormatting());
        if (itemStack instanceof GuideTomeItemStack tome)
            return CustomColor.fromChatFormatting(tome.getTomeInfo().tier().getChatFormatting());
        if (itemStack instanceof GuideAspectItemStack aspect)
            return CustomColor.fromChatFormatting(aspect.getAspectInfo().gearTier().getChatFormatting());
        return CustomColor.NONE;
    }

    public T getItemStack() {
        return itemStack;
    }
}
