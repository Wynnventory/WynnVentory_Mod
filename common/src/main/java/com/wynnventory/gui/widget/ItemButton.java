package com.wynnventory.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wynntils.core.components.Services;
import com.wynntils.core.text.StyledText;
import com.wynntils.screens.guides.GuideItemStack;
import com.wynntils.screens.guides.aspect.GuideAspectItemStack;
import com.wynntils.screens.guides.gear.GuideGearItemStack;
import com.wynntils.screens.guides.powder.GuidePowderItemStack;
import com.wynntils.screens.guides.tome.GuideTomeItemStack;
import com.wynntils.utils.MathUtils;
import com.wynntils.utils.colors.CommonColors;
import com.wynntils.utils.colors.CustomColor;
import com.wynntils.utils.mc.KeyboardUtils;
import com.wynntils.utils.render.FontRenderer;
import com.wynntils.utils.render.RenderUtils;
import com.wynntils.utils.render.Texture;
import com.wynntils.utils.render.type.HorizontalAlignment;
import com.wynntils.utils.render.type.TextShadow;
import com.wynntils.utils.render.type.VerticalAlignment;
import com.wynnventory.gui.screen.RewardScreen;
import com.wynnventory.model.item.simple.SimpleGearItem;
import com.wynnventory.model.item.simple.SimpleItem;
import com.wynnventory.model.item.simple.SimpleTierItem;
import com.wynnventory.util.HttpUtils;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.lwjgl.glfw.GLFW;

public class ItemButton<T extends GuideItemStack> extends WynnventoryButton {
    private final T itemStack;
    private final SimpleItem simpleItem;

    public ItemButton(int x, int y, int width, int height, T itemStack, SimpleItem simpleItem) {
        super(x, y, width, height, "");
        this.itemStack = itemStack;
        this.simpleItem = simpleItem;
        buildTooltip();
    }

    @Override
    public void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        PoseStack poseStack = g.pose();

        // Colored highlight like Wynntils (scaled to our box)
        CustomColor color = getCustomColor();
        if (color != CustomColor.NONE) {
            RenderUtils.drawTexturedRectWithColor(
                    poseStack,
                    Texture.HIGHLIGHT.resource(),
                    color,
                    getX() - 1f,
                    getY() - 1f,
                    width + 2f,
                    height + 2f,
                    0,
                    0,
                    18);
        }

        // Draw item (MC item icon anchored at button origin, scaled)
        g.pose().pushPose();
        g.pose().translate(getX(), getY(), 1f);
        float scale = width / 16f;
        g.pose().scale(scale, scale, scale);
        RenderUtils.renderItem(g, itemStack, 0, 0);
        g.pose().popPose();

        if (simpleItem instanceof SimpleTierItem || itemStack instanceof GuidePowderItemStack) {
            renderText(
                    g,
                    String.valueOf(simpleItem.getAmount()),
                    CommonColors.WHITE,
                    HorizontalAlignment.RIGHT,
                    VerticalAlignment.BOTTOM,
                    TextShadow.OUTLINE);
        }
        // Favorite icon overlay (placed relative to button size)
        if (Services.Favorites.isFavorite(itemStack)) {
            float favScale = width / 18f;
            RenderUtils.drawScalingTexturedRect(
                    poseStack,
                    Texture.FAVORITE_ICON.resource(),
                    getX() + (12 * favScale),
                    getY() - (4 * favScale),
                    200,
                    (int) (9 * favScale),
                    (int) (9 * favScale),
                    Texture.FAVORITE_ICON.width(),
                    Texture.FAVORITE_ICON.height());
        }

        if (simpleItem instanceof SimpleGearItem simpleGearItem && simpleGearItem.isShiny()) {
            renderText(g, "⬡", CommonColors.WHITE, TextShadow.NORMAL);
        }

        switch (itemStack) {
            case GuidePowderItemStack powderStack ->
                renderText(g, MathUtils.toRoman(powderStack.getTier()), getCustomColor(), TextShadow.OUTLINE);
            case GuideAspectItemStack aspectStack ->
                renderText(
                        g,
                        aspectStack.getAspectInfo().classType().getName().substring(0, 2),
                        getCustomColor(),
                        TextShadow.OUTLINE);
            default -> {
                // Nothing special to be rendered
            }
        }

        // Ugly approach to prevent price tooltip rendering behind RewardScreen assets
        String screenTitle = Minecraft.getInstance().screen.getTitle().getString();
        if (this.isHovered() && !screenTitle.equals(RewardScreen.CONTAINER_TITLE)) {
            g.renderTooltip(Minecraft.getInstance().font, itemStack, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!KeyboardUtils.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) && !KeyboardUtils.isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT)) {
            return false;
        }

        String unformattedName =
                StyledText.fromComponent(itemStack.getHoverName()).getStringWithoutFormatting();
        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            Util.getPlatform().openUri("https://www.wynnventory.com/history/" + HttpUtils.encode(unformattedName));
            return true;
        } else if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            Services.Favorites.toggleFavorite(unformattedName);
        }

        return true;
    }

    @Override
    public void onPress() {
        // Item buttons are non-interactive
    }

    private void buildTooltip() {
        switch (itemStack) {
            case GuideGearItemStack gear -> gear.buildTooltip();
            case GuideTomeItemStack tome -> tome.buildTooltip();
            default -> {
                // by default no special tooltips need to be generated
            }
        }
    }

    private CustomColor getCustomColor() {
        return switch (itemStack) {
            case GuideGearItemStack gear ->
                CustomColor.fromChatFormatting(gear.getGearInfo().tier().getChatFormatting());
            case GuideTomeItemStack tome ->
                CustomColor.fromChatFormatting(tome.getTomeInfo().tier().getChatFormatting());
            case GuideAspectItemStack aspect ->
                CustomColor.fromChatFormatting(aspect.getAspectInfo().gearTier().getChatFormatting());
            case GuidePowderItemStack powder ->
                CustomColor.fromChatFormatting(powder.getElement().getLightColor());
            default -> CustomColor.NONE;
        };
    }

    private void renderText(GuiGraphics g, String text, CustomColor color, TextShadow shadow) {
        renderText(g, text, color, HorizontalAlignment.LEFT, VerticalAlignment.TOP, shadow);
    }

    private void renderText(
            GuiGraphics g,
            String text,
            CustomColor color,
            HorizontalAlignment hAlign,
            VerticalAlignment vAlign,
            TextShadow shadow) {
        float scale = width / 16f;
        FontRenderer.getInstance()
                .renderAlignedTextInBox(
                        g.pose(),
                        StyledText.fromString(text),
                        getX(),
                        (float) getX() + getWidth(),
                        getY(),
                        (float) getY() + getHeight(),
                        0,
                        color,
                        hAlign,
                        vAlign,
                        shadow,
                        scale);
    }

    public T getItemStack() {
        return itemStack;
    }
}
