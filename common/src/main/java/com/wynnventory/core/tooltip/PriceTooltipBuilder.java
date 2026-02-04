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

        ModConfig cfg = ModConfig.get();
        TooltipSettings ts = cfg.getTooltipSettings();
        ColorSettings cs = cfg.getColorSettings();
        DisplayOptions fmt = ts.getDisplayFormat();

        add(out, ts.isShowAverage80Price(),     "80% avg",      summary.getAverageMid80PercentPrice(), fmt, cs);
        add(out, ts.isShowUnidAverage80Price(), "Unid 80% avg", summary.getUnidentifiedAverageMid80PercentPrice(), fmt, cs);
        add(out, ts.isShowAveragePrice(),       "Avg",          summary.getAveragePrice(), fmt, cs);
        add(out, ts.isShowUnidAveragePrice(),   "Unid Avg",     summary.getUnidentifiedAveragePrice(), fmt, cs);
        add(out, ts.isShowMaxPrice(),           "Highest",      summary.getHighestPrice(), fmt, cs);
        add(out, ts.isShowMinPrice(),           "Lowest",       summary.getLowestPrice(), fmt, cs);

        return out;
    }

    private static void add(List<Component> out, boolean enabled, String label, Integer value, DisplayOptions displayOptions, ColorSettings colorSettings) {
        if (!enabled || value == null) return;
        out.add(priceLine(label, value, displayOptions, colorSettings));
    }

    private static void add(List<Component> out, boolean enabled, String label, Double value, DisplayOptions displayOptions, ColorSettings colorSettings) {
        if (!enabled || value == null) return;
        out.add(priceLine(label, value.intValue(), displayOptions, colorSettings));
    }

    private static Component priceLine(String label, int value, DisplayOptions displayOptions, ColorSettings colors) {
        String price = (displayOptions == DisplayOptions.FORMATTED)
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
