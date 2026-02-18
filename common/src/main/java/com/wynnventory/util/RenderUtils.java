package com.wynnventory.util;

import com.wynntils.core.components.Managers;
import com.wynntils.features.tooltips.TooltipFittingFeature;
import com.wynnventory.core.config.ModConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
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

        int screenW = mc.getWindow().getGuiScaledWidth();
        int screenH = mc.getWindow().getGuiScaledHeight();

        Vector2ic vanillaPos =
                DefaultTooltipPositioner.INSTANCE.positionTooltip(screenW, screenH, mouseX, mouseY, vanillaW, vanillaH);

        int vanillaX = vanillaPos.x();
        int vanillaY = vanillaPos.y();

        float priceScale = getScaleFactor(priceComponents);
        int priceW = (int) (tooltipWidth(priceComponents, font) * priceScale);
        int priceH = (int) (tooltipHeight(priceComponents, font) * priceScale);

        if (ModConfig.getInstance().getTooltipSettings().isAnchorTooltips()) {
            return new Vector2i(TOOLTIP_GAP, screenH / 2 - priceH / 2);
        }

        // ----------------------------
        // 3) Position to the right of the vanilla tooltip (+GAP), flip/clamp if needed
        // ----------------------------
        int priceX = getScaledXCoordinate(vanillaX, vanillaW, vanillaH);
        int priceY = vanillaY;

        // Flip to left if overflowing right edge
        if (priceX + priceW > screenW - 4) {
            priceX = vanillaX - TOOLTIP_GAP - priceW;
        }

        // Clamp inside screen bounds
        priceX = clamp(priceX, 4, screenW - priceW - 4);
        priceY = clamp(priceY, 6, screenH - priceH - 4);

        return new Vector2i((int) (priceX / priceScale), priceY);
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

    public static int getScaledXCoordinate(int tooltipX, int tooltipWidth, int tooltipHeight) {
        boolean isFittingEnabled = FITTING_FEATURE.isEnabled();
        float universalScale = FITTING_FEATURE.universalScale.get();

        if (isFittingEnabled) {
            float scale = getScaleFactor(tooltipHeight);

            if (scale != universalScale) {
                return (int) (tooltipX + Math.floor(tooltipWidth * scale) + TOOLTIP_GAP);
            }
        }

        return tooltipX + tooltipWidth + TOOLTIP_GAP;
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

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
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
