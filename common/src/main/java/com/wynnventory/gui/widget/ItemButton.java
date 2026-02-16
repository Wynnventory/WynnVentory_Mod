package com.wynnventory.gui.widget;

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
import com.wynnventory.util.HttpUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;

public class ItemButton<T extends GuideItemStack> extends WynnventoryButton {
    private final T itemStack;
    private boolean shiny = false;

    public ItemButton(int x, int y, int width, int height, T itemStack, boolean shiny) {
        super(x, y, width, height, "");
        this.itemStack = itemStack;
        this.shiny = shiny;
        buildTooltip();
    }

    @Override
    public void renderContents(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        // Colored highlight like Wynntils (scaled to our box)
        CustomColor color = getCustomColor();
        RenderUtils.drawTexturedRect(
                g,
                Texture.HIGHLIGHT.identifier(),
                color,
                getX() - 1,
                getY() - 1,
                width + 2,
                height + 2,
                0,
                0,
                18,
                18,
                Texture.HIGHLIGHT.width(),
                Texture.HIGHLIGHT.height());

        // Draw item (MC item icon anchored at button origin, scaled)
        g.pose().pushMatrix();
        g.pose().translate(getX(), getY());
        float scale = width / 16f;
        g.pose().scale(scale, scale);
        RenderUtils.renderItem(g, itemStack, 0, 0);
        g.pose().popMatrix();

        // Favorite icon overlay (placed relative to button size)
        if (Services.Favorites.isFavorite(itemStack)) {
            float favScale = width / 18f;
            RenderUtils.drawScalingTexturedRect(
                    g,
                    Texture.FAVORITE_ICON.identifier(),
                    getX() + (int) (12 * favScale),
                    getY() - (int) (4 * favScale),
                    (int) (9 * favScale),
                    (int) (9 * favScale),
                    Texture.FAVORITE_ICON.width(),
                    Texture.FAVORITE_ICON.height());
        }

        if (shiny) {
            renderText(g, "⬡", CommonColors.WHITE, HorizontalAlignment.LEFT, VerticalAlignment.TOP, TextShadow.NORMAL);
        }

        if(itemStack instanceof GuidePowderItemStack powderStack) {
            renderText(g, MathUtils.toRoman(powderStack.getTier()), powderStack.getElement().getColor(), HorizontalAlignment.RIGHT, VerticalAlignment.BOTTOM, TextShadow.OUTLINE);
        } else if (itemStack instanceof GuideAspectItemStack aspectStack) {
            renderText(g, aspectStack.getAspectInfo().classType().getName().substring(0, 2), CommonColors.WHITE, HorizontalAlignment.RIGHT, VerticalAlignment.BOTTOM, TextShadow.OUTLINE);
        }

        // Causes price tooltip to render behind ItemButton textures. Maybe fix later?
//        if (this.isHovered()) {
//            g.setTooltipForNextFrame(Minecraft.getInstance().font, itemStack, mouseX, mouseY);
//        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        if (!KeyboardUtils.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) && !KeyboardUtils.isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT)) {
            return false;
        }

        String unformattedName = StyledText.fromComponent(itemStack.getHoverName()).getStringWithoutFormatting();
        if (event.button() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            Util.getPlatform().openUri("https://www.wynnventory.com/history/" + HttpUtils.encode(unformattedName));
            return true;
        } else if (event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            Services.Favorites.toggleFavorite(unformattedName);
        }

        return true;
    }

    @Override
    public void onPress(InputWithModifiers inputWithModifiers) { }

    private void buildTooltip() {
        switch (itemStack) {
            case GuideGearItemStack gear -> gear.buildTooltip();
            case GuideTomeItemStack tome -> tome.buildTooltip();
            default -> {}
        }
    }

    private CustomColor getCustomColor() {
        return switch (itemStack) {
            case GuideGearItemStack gear -> CustomColor.fromChatFormatting(gear.getGearInfo().tier().getChatFormatting());
            case GuideTomeItemStack tome -> CustomColor.fromChatFormatting(tome.getTomeInfo().tier().getChatFormatting());
            case GuideAspectItemStack aspect -> CustomColor.fromChatFormatting(aspect.getAspectInfo().gearTier().getChatFormatting());
            case GuidePowderItemStack powder -> CustomColor.fromChatFormatting(powder.getElement().getLightColor());
            default -> CustomColor.NONE;
        };
    }

    private void renderText(GuiGraphics g, String text, CustomColor color, HorizontalAlignment hAlignment, VerticalAlignment vAlignment, TextShadow shadow) {
        FontRenderer.getInstance()
                .renderAlignedTextInBox(
                        g,
                        StyledText.fromString(text),
                        getX(),
                        getX() + getWidth(),
                        getY(),
                        getY() + getHeight(),
                        0,
                        color,
                        hAlignment,
                        vAlignment,
                        shadow,
                        0.8f);
    }

    public T getItemStack() {
        return itemStack;
    }
}
