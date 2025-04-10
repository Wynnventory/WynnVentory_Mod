package com.wynnventory.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wynntils.utils.mc.McUtils;
import com.wynnventory.enums.ClassIcon;
import com.wynnventory.model.item.GroupedLootpool;
import com.wynnventory.model.item.LootpoolItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class AspectTooltipHelper {

    private static final ChatFormatting MYTHIC_COLOR = ItemStackUtils.getRarityColor("Mythic");

    private AspectTooltipHelper() {}

    public static void renderAspectTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, GroupedLootpool pool) {
        List<Component> tooltipLines = buildMythicAspectTooltip(pool);
        if (tooltipLines.isEmpty()) return;

        drawAlignedTooltip(guiGraphics, mouseX, mouseY, tooltipLines);
    }

    private static List<Component> buildMythicAspectTooltip(GroupedLootpool pool) {
        List<LootpoolItem> mythicAspects = pool.getGroupItems().stream()
                .flatMap(group -> group.getLootItems().stream())
                .filter(AspectTooltipHelper::isMythicAspect)
                .sorted(Comparator.comparing(LootpoolItem::getName))
                .toList();

        if (mythicAspects.isEmpty()) return List.of();

        List<Component> lines = new ArrayList<>();
        lines.add(Component.literal("Mythic Aspects").withStyle(MYTHIC_COLOR, ChatFormatting.BOLD));

        for (LootpoolItem item : mythicAspects) {
            ClassIcon icon = ClassIcon.fromAspectType(item.getType());
            String display = (icon != null ? icon.get() + " " : "") + item.getName();
            lines.add(Component.literal("• " + display).withStyle(ChatFormatting.GRAY));
        }

        return lines;
    }

    private static boolean isMythicAspect(LootpoolItem item) {
        return "Mythic".equalsIgnoreCase(item.getRarity())
                && "AspectItem".equalsIgnoreCase(item.getItemType());
    }

    private static void drawAlignedTooltip(GuiGraphics graphics, int mouseX, int mouseY, List<Component> lines) {
        Font font = McUtils.mc().font;
        int screenW = McUtils.window().getGuiScaledWidth();
        int screenH = McUtils.window().getGuiScaledHeight();
        int scale = (int) McUtils.window().getGuiScale();
        int gap = 5 * scale;

        assert Minecraft.getInstance().player != null;
        List<Component> primaryTooltip = Screen.getTooltipFromItem(
                Minecraft.getInstance(),
                Minecraft.getInstance().player.getMainHandItem()
        );
        Dimension primarySize = PriceTooltipHelper.calculateTooltipDimension(primaryTooltip, font);
        Dimension aspectSize = PriceTooltipHelper.calculateTooltipDimension(lines, font);

        boolean hasMoreSpaceLeft = (mouseX - gap) >= (screenW - (mouseX + primarySize.width + gap));
        float posX = hasMoreSpaceLeft
                ? mouseX - gap - aspectSize.width
                : mouseX + primarySize.width + gap;

        float posY = mouseY + aspectSize.height > screenH
                ? screenH - aspectSize.height - gap
                : mouseY;

        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(posX, posY, 0);
        graphics.renderComponentTooltip(font, lines, 0, 0);
        pose.popPose();
    }
}