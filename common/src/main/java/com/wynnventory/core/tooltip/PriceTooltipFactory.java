package com.wynnventory.core.tooltip;

import com.wynntils.models.gear.type.GearInfo;
import com.wynntils.models.items.WynnItem;
import com.wynntils.models.items.items.game.GearBoxItem;
import com.wynntils.screens.guides.GuideItemStack;
import com.wynnventory.core.config.ModConfig;
import com.wynnventory.model.item.trademarket.TrademarketItemSnapshot;
import com.wynnventory.util.ItemStackUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class PriceTooltipFactory {
    private static final Component PRICE_TOOLTIP_TITLE = Component.translatable("feature.wynnventory.tooltip.title")
            .withStyle(ChatFormatting.GOLD)
            .withStyle(ChatFormatting.BOLD);

    private final PriceTooltipBuilder builder;

    public PriceTooltipFactory(PriceTooltipBuilder builder) {
        this.builder = builder;
    }

    public List<Component> getPriceTooltip(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return List.of();

        List<PriceSection> sections = resolveSections(stack);
        if (sections.isEmpty()) return List.of();

        return formatSections(sections);
    }

    private List<PriceSection> resolveSections(ItemStack stack) {
        WynnItem wynnItem = ItemStackUtils.getWynnItem(stack);
        if (wynnItem instanceof GearBoxItem gearBox) {
            if (ModConfig.getInstance().getTooltipSettings().isShowBoxedItemTooltips()) {
                return resolveGearBoxSections(gearBox);
            }

            return List.of();
        }

        TrademarketItemSnapshot snap = TrademarketItemSnapshot.resolveSnapshot(stack);
        if (snap == null || snap.live() == null) return List.of();

        Component itemName = stack instanceof GuideItemStack g ? g.getHoverName() : stack.getCustomName();
        return List.of(new PriceSection(itemName, snap));
    }

    private List<PriceSection> resolveGearBoxSections(GearBoxItem gearBox) {
        Map<GearInfo, TrademarketItemSnapshot> snapshots = TrademarketItemSnapshot.resolveGearBoxItem(gearBox);
        if (snapshots == null || snapshots.isEmpty()) return List.of();

        List<PriceSection> out = new ArrayList<>(snapshots.size());

        for (Map.Entry<GearInfo, TrademarketItemSnapshot> e : snapshots.entrySet()) {
            TrademarketItemSnapshot snap = e.getValue();
            if (snap == null || snap.live() == null) continue;

            GearInfo info = e.getKey();
            Component title =
                    Component.literal(info.name()).withStyle(info.tier().getChatFormatting());

            out.add(new PriceSection(title, snap));
        }

        return out;
    }

    private List<Component> formatSections(List<PriceSection> sections) {
        List<Component> lines = new ArrayList<>();
        lines.add(PRICE_TOOLTIP_TITLE);

        for (int i = 0; i < sections.size(); i++) {
            PriceSection s = sections.get(i);

            lines.addAll(builder.buildPriceTooltip(s.snapshot(), s.title()));

            // separator between sections (empty line)
            if (i < sections.size() - 1) {
                lines.add(Component.empty());
            }
        }

        return lines;
    }

    private record PriceSection(Component title, TrademarketItemSnapshot snapshot) {}
}
