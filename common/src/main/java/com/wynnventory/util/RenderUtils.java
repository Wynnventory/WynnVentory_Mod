package com.wynnventory.util;

import com.wynntils.core.components.Managers;
import com.wynntils.features.tooltips.TooltipFittingFeature;
import com.wynnventory.core.config.ModConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.joml.Vector2i;
import org.joml.Vector2ic;

public abstract class RenderUtils {
    private static final int TOOLTIP_GAP = 7;
    private static final TooltipFittingFeature FITTING_FEATURE =
            Managers.Feature.getFeatureInstance(TooltipFittingFeature.class);

    private RenderUtils() {}

    public static Vector2i calculateTooltipCoords(
            int mouseX,
            int mouseY,
            List<ClientTooltipComponent> vanillaComponents,
            List<ClientTooltipComponent> priceComponents) {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;

        int vanillaW = tooltipWidth(vanillaComponents, font);
        int vanillaH = tooltipHeight(vanillaComponents, font);

        float universalScale = FITTING_FEATURE.isEnabled() ? FITTING_FEATURE.universalScale.get() : 1.0f;

        // If the scale is 1.0, but the tooltip is too tall for the screen,
        // we should calculate what the scale SHOULD be, because Wynntils might not have updated it yet.
        float calculatedVanillaScale = getScaleFactor(vanillaH);
        if (calculatedVanillaScale < universalScale) {
            universalScale = calculatedVanillaScale;
        }

        int scaledVanillaW = (int) (vanillaW * universalScale);
        int scaledVanillaH = (int) (vanillaH * universalScale);

        int screenW = mc.getWindow().getGuiScaledWidth();
        int screenH = mc.getWindow().getGuiScaledHeight();

        // ----------------------------
        // 1) Position the primary tooltip using Minecraft's logic (but with scaled dimensions)
        // ----------------------------
        Vector2ic vanillaPos = DefaultTooltipPositioner.INSTANCE.positionTooltip(
                screenW, screenH, mouseX, mouseY, scaledVanillaW, scaledVanillaH);

        int vanillaX = vanillaPos.x();
        int vanillaY = vanillaPos.y();

        // ----------------------------
        // 2) Position the secondary tooltip
        // ----------------------------
        float priceScale = getScaleFactor(priceComponents);
        int priceW = (int) (tooltipWidth(priceComponents, font) * priceScale);
        int priceH = (int) (tooltipHeight(priceComponents, font) * priceScale);

        if (ModConfig.getInstance().getTooltipSettings().isAnchorTooltips()) {
            return new Vector2i(TOOLTIP_GAP, screenH / 2 - priceH / 2);
        }

        // Position to the right of the vanilla tooltip (+GAP)
        int priceX = vanillaX + scaledVanillaW + TOOLTIP_GAP;
        int priceY = vanillaY;

        // Flip to left if overflowing right edge
        if (priceX + priceW > screenW - 4) {
            priceX = vanillaX - TOOLTIP_GAP - priceW;
        }

        // Clamp inside screen bounds
        priceX = Math.clamp(priceX, 4, screenW - priceW - 4);
        priceY = Math.clamp(priceY, 6, screenH - priceH - 4);

        return new Vector2i((int) (priceX / priceScale), priceY);
    }

    public static void drawTooltip(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            List<Component> vanillaLines,
            List<Component> customLines) {
        List<ClientTooltipComponent> vanillaComponents = RenderUtils.toClientComponents(vanillaLines, Optional.empty());
        List<ClientTooltipComponent> customComponents = RenderUtils.toClientComponents(customLines, Optional.empty());

        Vector2i tooltipCoords =
                RenderUtils.calculateTooltipCoords(mouseX, mouseY, vanillaComponents, customComponents);
        ClientTooltipPositioner fixed = new RenderUtils.FixedTooltipPositioner(tooltipCoords.x, tooltipCoords.y);

        float scale = RenderUtils.getScaleFactor(customComponents);
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().scale(scale, scale);
        guiGraphics.renderTooltip(Minecraft.getInstance().font, customComponents, mouseX, mouseY, fixed, null);
        guiGraphics.pose().popMatrix();
    }

    public static List<ClientTooltipComponent> toClientComponents(
            List<Component> lines, Optional<TooltipComponent> tooltipImage) {
        if (lines == null) return new ArrayList<>();
        List<ClientTooltipComponent> list = new ArrayList<>(lines.size() + (tooltipImage.isPresent() ? 1 : 0));
        for (Component line : lines) {
            if (line != null) {
                list.add(ClientTooltipComponent.create(line.getVisualOrderText()));
            }
        }

        tooltipImage.ifPresent(img -> list.add(list.isEmpty() ? 0 : 1, ClientTooltipComponent.create(img)));

        return list;
    }

    private static float getScaleFactor(int tooltipHeight) {
        int screenH = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        int paddedTooltipHeight = tooltipHeight + 10;
        if (paddedTooltipHeight > screenH) {
            return screenH / (float) paddedTooltipHeight;
        }

        return 1f;
    }

    public static float getScaleFactor(List<ClientTooltipComponent> tooltips) {
        int height = tooltipHeight(tooltips, Minecraft.getInstance().font);

        return getScaleFactor(height);
    }

    public static int tooltipWidth(List<ClientTooltipComponent> comps, Font font) {
        int w = 0;
        for (ClientTooltipComponent c : comps) {
            w = Math.max(w, c.getWidth(font));
        }
        return w;
    }

    private static int tooltipHeight(List<ClientTooltipComponent> comps, Font font) {
        int h = 0;
        for (ClientTooltipComponent c : comps) {
            h += c.getHeight(font);
        }
        return h;
    }

    public static final class FixedTooltipPositioner implements ClientTooltipPositioner {
        private final Vector2i pos;

        public FixedTooltipPositioner(int x, int y) {
            this.pos = new Vector2i(x, y);
        }

        @Override
        public Vector2ic positionTooltip(
                int screenWidth, int screenHeight, int mouseX, int mouseY, int tooltipWidth, int tooltipHeight) {
            return pos;
        }
    }
}
