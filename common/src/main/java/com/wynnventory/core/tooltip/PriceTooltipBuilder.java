package com.wynnventory.core.tooltip;

import com.wynnventory.core.config.ModConfig;
import com.wynnventory.core.config.settings.ColorSettings;
import com.wynnventory.core.config.settings.DisplayOptions;
import com.wynnventory.core.config.settings.TooltipSettings;
import com.wynnventory.model.item.trademarket.TrademarketItemSummary;
import com.wynnventory.util.EmeraldUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.util.ArrayList;
import java.util.List;

public final class PriceTooltipBuilder {

    public List<Component> buildPriceTooltip(TrademarketItemSummary summary, Component title) {
        List<Component> out = new ArrayList<>();

        out.add(title);

        if (summary == null || summary.isEmpty()) {
            out.add(Component.literal("No data yet.").withStyle(ChatFormatting.RED));
            return out;
        }

        TooltipSettings ts = ModConfig.getInstance().getTooltipSettings();
        add(out, ts.isShowAverage80Price(),     "80% avg",      summary.getAverageMid80PercentPrice());
        add(out, ts.isShowUnidAverage80Price(), "Unid 80% avg", summary.getUnidentifiedAverageMid80PercentPrice());
        add(out, ts.isShowAveragePrice(),       "Avg",          summary.getAveragePrice());
        add(out, ts.isShowUnidAveragePrice(),   "Unid Avg",     summary.getUnidentifiedAveragePrice());
        add(out, ts.isShowMaxPrice(),           "Highest",      summary.getHighestPrice());
        add(out, ts.isShowMinPrice(),           "Lowest",       summary.getLowestPrice());

        return out;
    }

    private static void add(List<Component> out, boolean enabled, String label, Integer value) {
        if (!enabled || value == null) return;
        out.add(priceLine(label, value));
    }

    private static void add(List<Component> out, boolean enabled, String label, Double value) {
        if (!enabled || value == null) return;
        out.add(priceLine(label, value.intValue()));
    }

    private static Component priceLine(String label, int value) {
        ColorSettings colors = ModConfig.getInstance().getColorSettings();
        String price = (ModConfig.getInstance().getTooltipSettings().getDisplayFormat() == DisplayOptions.FORMATTED)
                ? EmeraldUtils.getFormattedString(value, false)
                : Integer.toString(value);

        int priceColor = ChatFormatting.GRAY.getColor();
        if (colors.isShowColors() && value >= colors.getColorMinPrice()) {
            priceColor = colors.getHighlightColor();
        }

        MutableComponent line = Component.literal(label + ": ").withStyle(Style.EMPTY.withColor(ChatFormatting.WHITE));
        line.append(Component.literal(price).withStyle(Style.EMPTY.withColor(priceColor)));

        return line;
    }
}
