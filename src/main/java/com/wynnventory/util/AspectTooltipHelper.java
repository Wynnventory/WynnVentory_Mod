package com.wynnventory.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wynntils.utils.mc.McUtils;
import com.wynnventory.enums.ClassIcon;
import com.wynnventory.model.item.Lootpool;
import com.wynnventory.model.item.LootpoolItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class AspectTooltipHelper {
    private static final ChatFormatting MYTHIC_COLOR = ItemStackUtils.getRarityColor("Mythic");

    private AspectTooltipHelper() {
    }

    public static void renderAspectTooltip(GuiGraphics graphics, int mouseX, int mouseY, Lootpool pool) {
        List<Component> tooltipLines = buildLines(pool);
        if (tooltipLines.isEmpty()) return;

        drawAlignedTooltip(graphics, mouseX, mouseY, tooltipLines);
    }

    private static List<Component> buildLines(Lootpool pool) {
        List<LootpoolItem> mythics = pool.getMythicAspects();

        if (mythics.isEmpty()) {
            return Collections.emptyList();
        }

        Component header = Component.literal("Mythic Aspects")
                .withStyle(MYTHIC_COLOR, ChatFormatting.BOLD);

        List<Component> bullets = mythics.stream()
                .map(i -> {
                    ClassIcon ic = ClassIcon.fromAspectType(i.getType());
                    String txt = (ic != null ? ic.get() + " " : "") + i.getName();
                    return Component.literal("• " + txt)
                            .withStyle(ChatFormatting.GRAY);
                })
                .collect(Collectors.toList());

        List<Component> out = new ArrayList<>(bullets.size() + 1);
        out.add(header);
        out.addAll(bullets);
        return out;
    }

    public static void drawAlignedTooltip(GuiGraphics g, int mouseX, int mouseY, List<Component> tooltipLines) {
        drawAlignedTooltip(g, mouseX, mouseY,
                Collections.emptyList(),  // primary empty
                tooltipLines);
    }

    private static void drawAlignedTooltip(GuiGraphics g, int mouseX, int mouseY, List<Component> primaryLines, List<Component> tooltipLines) {
        Font font = McUtils.mc().font;
        int sw = g.guiWidth();
        int sh = g.guiHeight();
        int gs = (int) McUtils.window().getGuiScale();
        int gap = 5 * gs;

        // 1) Measure vanilla tooltip (if any)
        boolean hasPrimary = !primaryLines.isEmpty();
        Dimension pDim = hasPrimary
                ? PriceTooltipHelper.calculateTooltipDimension(primaryLines, font)
                : new Dimension(0, 0);

        // 2) Inflate by MC's own 3px border + 2px fudge
        int primaryBoxWidth = hasPrimary
                ? pDim.width + 3 + 2
                : 0;

        // 3) Decide which side vanilla draws to

        // 4) We draw on the opposite side
        boolean drawLeft = !hasPrimary
                || (mouseX + primaryBoxWidth + 3 <= sw);

        // 5) Available width over there
        int availW = drawLeft
                ? (mouseX - gap)
                : ((sw - gap) - (mouseX + primaryBoxWidth));

        // 6) Scale to fit availW x 80% height
        float scale = PriceTooltipHelper.calculateScaleFactor(
                tooltipLines,
                Math.round(sh * 0.8f),
                availW,
                0.4f, 1f,
                font
        );

        // 7) Final dimensions
        Dimension raw = PriceTooltipHelper.calculateTooltipDimension(tooltipLines, font);
        int w = Math.round(raw.width * scale);
        int h = Math.round(raw.height * scale);

        // 8) Compute X (clamped)
        float x = drawLeft
                ? mouseX - gap - w
                : mouseX + primaryBoxWidth + gap;
        x = Mth.clamp(x, gap, sw - gap - w);

        // 9) Compute Y (clamped)
        float y = (mouseY + h > sh - gap)
                ? (sh - gap - h)
                : mouseY;
        y = Mth.clamp(y, gap, sh - gap - h);

        // 10) Render
        PoseStack ps = g.pose();
        ps.pushPose();
        ps.translate(x, y, 0);
        ps.scale(scale, scale, 1f);
        g.renderComponentTooltip(font, tooltipLines, 0, 0);
        ps.popPose();
    }
}
