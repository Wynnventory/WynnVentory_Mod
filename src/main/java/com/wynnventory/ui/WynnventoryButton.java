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

    public WynnventoryButton(int x, int y, int width, int height, E itemStack, Screen screen, boolean shiny) {
        super(x, y, width, height, Component.literal("Guide GearItemStack Button"));
        this.itemStack = itemStack;
        this.shiny = shiny;
        buildTooltip();
    }

    @Override
    public void onPress() {

    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        PoseStack poseStack = guiGraphics.pose();

        CustomColor color = getCustomColor();

        RenderUtils.drawTexturedRectWithColor(
                poseStack,
                Texture.HIGHLIGHT.resource(),
                color.withAlpha(1f),
                getX() - 1,
                getY() - 1,
                0,
                this.width + 2,
                this.height + 2,
                Texture.HIGHLIGHT.width(),
                Texture.HIGHLIGHT.height());

        RenderUtils.renderItem(guiGraphics, itemStack, getX(), getY());

        if (shiny) {
            renderText("⬡", poseStack, CustomColor.fromChatFormatting(ChatFormatting.WHITE));
        }

        if(itemStack instanceof GuidePowderItemStack powderItemStack) {
            renderText(MathUtils.toRoman(powderItemStack.getTier()), poseStack, color);
        }

        if(itemStack instanceof GuideAspectItemStack aspectItemStack) {
            renderText(aspectItemStack.getAspectInfo().classType().getName().substring(0,2), poseStack, color);
        }

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

    private void renderText(String text, PoseStack poseStack, CustomColor color) {
        renderTextAt(text, poseStack, color, getX(), getY() - (getHeight() / 2f) + 4);
    }

    private void renderTextAt(String text, PoseStack poseStack, CustomColor color, float xPos, float yPos) {
        poseStack.pushPose();
        poseStack.translate(0, 0, 200);
        FontRenderer.getInstance()
                .renderAlignedTextInBox(
                        poseStack,
                        StyledText.fromString(text),
                        xPos,
                        xPos,
                        yPos,
                        0,
                        color,
                        HorizontalAlignment.CENTER,
                        TextShadow.OUTLINE);
        poseStack.popPose();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

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
        if(itemStack instanceof GuideGearItemStack guideGearItemStack) {
            guideGearItemStack.buildTooltip();
        }

        else if(itemStack instanceof GuideTomeItemStack guideTomeItemStack) {
            guideTomeItemStack.buildTooltip();
        }
    }

    public CustomColor getCustomColor() {
        if(itemStack instanceof GuideGearItemStack guideGearItemStack) {
            return CustomColor.fromChatFormatting(guideGearItemStack.getGearInfo().tier().getChatFormatting());
        }

        else if(itemStack instanceof GuideTomeItemStack guideTomeItemStack) {
            return CustomColor.fromChatFormatting(guideTomeItemStack.getTomeInfo().tier().getChatFormatting());
        }

        else if (itemStack instanceof GuideAspectItemStack guideAspectItemStack) {
            return CustomColor.fromChatFormatting(guideAspectItemStack.getAspectInfo().gearTier().getChatFormatting());
        }

        return CustomColor.NONE;
    }
}
