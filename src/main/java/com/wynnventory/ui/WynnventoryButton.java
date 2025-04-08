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
import com.wynnventory.config.ConfigManager;
import com.wynnventory.util.HttpUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public class WynnventoryButton<E extends GuideItemStack> extends AbstractButton {
    private final E itemStack;
    private final boolean shiny;
    // Assume that the original (unscaled) size of the item rendering is 16 pixels.
    private static final int ORIGINAL_ITEM_SIZE = 16;

    public WynnventoryButton(int x, int y, int width, int height, E itemStack, Screen screen, boolean shiny) {
        super(x, y, width, height, Component.literal("Guide GearItemStack Button"));
        this.itemStack = itemStack;
        this.shiny = shiny;
        buildTooltip();
    }

    @Override
    public void onPress() {
        // No action by default.
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        PoseStack poseStack = guiGraphics.pose();

        CustomColor color = getCustomColor();

        // Calculate a scaling factor for both item rendering and the highlight.
        // ORIGINAL_ITEM_SIZE is assumed to be the native unscaled size (16 pixels).
        float scaleFactor = (float) getWidth() / (float) ORIGINAL_ITEM_SIZE;

        // Render the highlight texture with proper scaling.
        poseStack.pushPose();
        // Translate to the button's top-left, offset by (-1,-1) for the highlight.
        poseStack.translate(getX() - 1, getY() - 1, 0);
        // Apply the same scale as for the item.
        poseStack.scale(scaleFactor, scaleFactor, scaleFactor);
        // The highlight should cover (this.width + 2) x (this.height + 2) pixels,
        // so we compute the source dimensions by dividing by the scale factor.
        int highlightWidth = Math.round((this.width + 2) / scaleFactor);
        int highlightHeight = Math.round((this.height + 2) / scaleFactor);
        RenderUtils.drawTexturedRectWithColor(
                poseStack,
                Texture.HIGHLIGHT.resource(),
                color.withAlpha(1f),
                0, 0, 0,
                highlightWidth, highlightHeight,
                Texture.HIGHLIGHT.width(),
                Texture.HIGHLIGHT.height());
        poseStack.popPose();

        // Render the item with scaling.
        poseStack.pushPose();
        poseStack.translate(getX(), getY(), 0);
        poseStack.scale(scaleFactor, scaleFactor, scaleFactor);
        guiGraphics.renderItem(itemStack, 0, 0);
        poseStack.popPose();

        // Render additional text overlays scaled by the same factor.
        if (shiny) {
            renderText("⬡", poseStack, CustomColor.fromChatFormatting(ChatFormatting.WHITE), scaleFactor);
        }
        if (itemStack instanceof GuidePowderItemStack powderItemStack) {
            renderText(MathUtils.toRoman(powderItemStack.getTier()), poseStack, color, scaleFactor);
        }
        if (itemStack instanceof GuideAspectItemStack aspectItemStack) {
            renderText(aspectItemStack.getAspectInfo().classType().getName().substring(0, 2), poseStack, color, scaleFactor);
        }
        // Render the favorite icon if the item is marked as favorite.
        if (Services.Favorites.isFavorite(itemStack)) {
            RenderUtils.drawScalingTexturedRect(
                    poseStack,
                    Texture.FAVORITE_ICON.resource(),
                    getX() + 12,
                    getY() - 4,
                    200,
                    (float) this.width / 2,
                    (float) this.height / 2,
                    Texture.FAVORITE_ICON.width(),
                    Texture.FAVORITE_ICON.height());
        }
    }

    private void renderText(String text, PoseStack poseStack, CustomColor color, float scale) {
        renderTextAt(text, poseStack, color, getX(), getY() - (getHeight() / 2f) + 4, scale);
    }

    /**
     * Renders text in a box centered in the button.
     * The method pushes a new transform, translates to the desired position, scales by the provided factor,
     * and then renders the text using the FontRenderer.
     *
     * @param text   the text to render
     * @param poseStack the current PoseStack
     * @param color  the color to render the text in
     * @param xPos   the X-coordinate to position the text (unscaled)
     * @param yPos   the Y-coordinate to position the text (unscaled)
     * @param scale  the scale factor to apply (same as the button's item scaling)
     */
    private void renderTextAt(String text, PoseStack poseStack, CustomColor color, float xPos, float yPos, float scale) {
        poseStack.pushPose();
        // Translate to the desired unscaled position.
        poseStack.translate(xPos, yPos, 200);
        // Apply scaling: note that subsequent coordinates are now in scaled space.
        poseStack.scale(scale, scale, scale);
        // Determine a box width in scaled space.
        // Since our button width is getWidth() (unscaled), after scaling the box width becomes getWidth()/scale.
        int boxWidth = Math.round(getWidth() / scale);
        // Render the text centered in the box.
        FontRenderer.getInstance().renderAlignedTextInBox(
                poseStack,
                StyledText.fromString(text),
                0, // left coordinate of the box in scaled space
                boxWidth, // right coordinate of the box
                0, // y coordinate in scaled space
                0, // z offset (if needed)
                color,
                HorizontalAlignment.LEFT,
                TextShadow.OUTLINE);
        poseStack.popPose();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        // Narration not implemented.
    }

    @Override
    public boolean isHovered() {
        return super.isHovered();
    }

    public E getItemStack() {
        return itemStack;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return super.charTyped(codePoint, modifiers);
    }

    @Nullable
    @Override
    public ComponentPath getCurrentFocusPath() {
        return super.getCurrentFocusPath();
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!KeyboardUtils.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) && !KeyboardUtils.isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT)) {
            return false;
        }

        String unformattedName = StyledText.fromComponent(itemStack.getHoverName()).getStringWithoutFormatting();
        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            Util.getPlatform().openUri("https://www.wynnventory.com/history/" + HttpUtil.encodeName(unformattedName));
            return true;
        } else if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            Services.Favorites.toggleFavorite(unformattedName);
        }

        return true;
    }

    public void buildTooltip() {
        if (itemStack instanceof GuideGearItemStack guideGearItemStack) {
            guideGearItemStack.buildTooltip();
        } else if (itemStack instanceof GuideTomeItemStack guideTomeItemStack) {
            guideTomeItemStack.buildTooltip();
        }
    }

    public CustomColor getCustomColor() {
        if (itemStack instanceof GuideGearItemStack guideGearItemStack) {
            return CustomColor.fromChatFormatting(guideGearItemStack.getGearInfo().tier().getChatFormatting());
        } else if (itemStack instanceof GuideTomeItemStack guideTomeItemStack) {
            return CustomColor.fromChatFormatting(guideTomeItemStack.getTomeInfo().tier().getChatFormatting());
        } else if (itemStack instanceof GuideAspectItemStack guideAspectItemStack) {
            return CustomColor.fromChatFormatting(guideAspectItemStack.getAspectInfo().gearTier().getChatFormatting());
        }
        return CustomColor.NONE;
    }
}
