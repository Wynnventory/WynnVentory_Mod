package com.wynnventory.util;

import com.wynntils.core.components.Managers;
import com.wynntils.features.tooltips.TooltipFittingFeature;
import com.wynnventory.model.item.trademarket.TrademarketItemSnapshot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.util.List;

public abstract class RenderUtils {


    public static Vector2i calculateTooltipCoords(int mouseX, int mouseY, List<ClientTooltipComponent> vanillaComponents, List<ClientTooltipComponent> priceComponents, TrademarketItemSnapshot snapshot) {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;

        int vanillaW = tooltipWidth(vanillaComponents, font);
        int vanillaH = tooltipHeight(vanillaComponents, font);

        int screenW = mc.getWindow().getGuiScaledWidth();
        int screenH = mc.getWindow().getGuiScaledHeight();

        Vector2ic vanillaPos = DefaultTooltipPositioner.INSTANCE.positionTooltip(
                screenW, screenH, mouseX, mouseY, vanillaW, vanillaH
        );

        int vanillaX = vanillaPos.x();
        int vanillaY = vanillaPos.y();

        // ----------------------------
        // 2) Create your "price tooltip"
        // ----------------------------
        int priceW = tooltipWidth(priceComponents, font);
        int priceH = tooltipHeight(priceComponents, font);

        // ----------------------------
        // 3) Position to the right of the vanilla tooltip (+gap), flip/clamp if needed
        // ----------------------------
        final int gap = 7;

        int priceX = vanillaX + vanillaW + gap;
        int priceY = vanillaY;

        TooltipFittingFeature feature = Managers.Feature.getFeatureInstance(TooltipFittingFeature.class);
        boolean isFittingEnabled = feature.isEnabled();
        float scale = feature.universalScale.get(); //1f
        if (isFittingEnabled) {
            int scaledTooltipHeight = vanillaH + 10;
            if (scaledTooltipHeight > screenH) {
                scale = screenH / (float) scaledTooltipHeight;
            }

            if (scale != 1f) {
                priceX = (int) (vanillaX + Math.floor(vanillaW * scale) + gap);
            }
        }

        // Flip to left if overflowing right edge
        if (priceX + priceW > screenW - 4) {
            priceX = vanillaX - gap - priceW;
        }

        // Clamp inside screen bounds
        priceX = clamp(priceX, 4, screenW - priceW - 4);
        priceY = clamp(priceY, 6, screenH - priceH - 4);


        return new Vector2i(priceX, priceY);
    }

    private static int tooltipWidth(List<ClientTooltipComponent> comps, Font font) {
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
}
