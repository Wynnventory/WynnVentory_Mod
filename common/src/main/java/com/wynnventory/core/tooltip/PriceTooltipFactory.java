package com.wynnventory.core.tooltip;

import com.wynntils.models.gear.type.GearInfo;
import com.wynntils.models.items.WynnItem;
import com.wynntils.models.items.items.game.GearBoxItem;
import com.wynnventory.model.item.trademarket.TrademarketItemSnapshot;
import com.wynnventory.model.item.trademarket.TrademarketItemSummary;
import com.wynnventory.util.ItemStackUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class PriceTooltipFactory {

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
            return resolveGearBoxSections(gearBox);
        }

        TrademarketItemSnapshot snap = TrademarketItemSnapshot.resolveSnapshot(stack);
        if (snap == null || snap.live() == null) return List.of();

        return List.of(new PriceSection(stack.getCustomName(), snap.live()));
    }

    private List<PriceSection> resolveGearBoxSections(GearBoxItem gearBox) {
        Map<GearInfo, TrademarketItemSnapshot> snapshots = TrademarketItemSnapshot.resolveGearBoxItem(gearBox);
        if (snapshots == null || snapshots.isEmpty()) return List.of();

        List<PriceSection> out = new ArrayList<>(snapshots.size());

        for (Map.Entry<GearInfo, TrademarketItemSnapshot> e : snapshots.entrySet()) {
            TrademarketItemSnapshot snap = e.getValue();
            if (snap == null || snap.live() == null) continue;

            GearInfo info = e.getKey();
            Component title = Component.literal(info.name())
                    .withStyle(info.tier().getChatFormatting());

            out.add(new PriceSection(title, snap.live()));
        }

        return out;
    }

    private List<Component> formatSections(List<PriceSection> sections) {
        List<Component> lines = new ArrayList<>();

        for (int i = 0; i < sections.size(); i++) {
            PriceSection s = sections.get(i);

            lines.addAll(builder.buildPriceTooltip(s.summary(), s.title()));

            // separator between sections (empty line)
            if (i < sections.size() - 1) {
                lines.add(Component.empty());
            }
        }

        return lines;
    }

    private record PriceSection(Component title, TrademarketItemSummary summary) {}
}
