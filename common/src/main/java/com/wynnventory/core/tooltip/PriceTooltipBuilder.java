package com.wynnventory.core.tooltip;

import com.wynnventory.core.config.ModConfig;
import com.wynnventory.core.config.settings.ColorSettings;
import com.wynnventory.core.config.settings.DisplayOptions;
import com.wynnventory.core.config.settings.TooltipSettings;
import com.wynnventory.model.item.trademarket.TrademarketItemSnapshot;
import com.wynnventory.model.item.trademarket.TrademarketItemSummary;
import com.wynnventory.util.EmeraldUtils;
import com.wynnventory.util.StringUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.util.ArrayList;
import java.util.List;

public final class PriceTooltipBuilder {

    public List<Component> buildPriceTooltip(TrademarketItemSnapshot snapshot, Component title) {
        List<Component> out = new ArrayList<>();

        out.add(title);

        if (snapshot == null || snapshot.live() == null || snapshot.live().isEmpty()) {
            out.add(Component.literal("No data yet.").withStyle(ChatFormatting.RED));
            return out;
        }

        TooltipSettings ts = ModConfig.getInstance().getTooltipSettings();
        add(out, ts.isShowAverage80Price(),     "80% avg",      snapshot.live().getAverageMid80PercentPrice(),              snapshot.historic().getAverageMid80PercentPrice());
        add(out, ts.isShowUnidAverage80Price(), "Unid 80% avg", snapshot.live().getUnidentifiedAverageMid80PercentPrice(),  snapshot.historic().getUnidentifiedAverageMid80PercentPrice());
        add(out, ts.isShowAveragePrice(),       "Avg",          snapshot.live().getAveragePrice(),                          snapshot.historic().getAveragePrice());
        add(out, ts.isShowUnidAveragePrice(),   "Unid Avg",     snapshot.live().getUnidentifiedAveragePrice(),              snapshot.historic().getUnidentifiedAveragePrice());
        add(out, ts.isShowMaxPrice(),           "Highest",      snapshot.live().getHighestPrice(),                          snapshot.historic().getHighestPrice());
        add(out, ts.isShowMinPrice(),           "Lowest",       snapshot.live().getLowestPrice(),                           snapshot.historic().getLowestPrice());

        return out;
    }

    private static void add(List<Component> out, boolean enabled, String label, Integer live, Integer history) {
        if (!enabled || live == null) return;
        out.add(priceLine(label, live, history == null ? 0 : history));
    }

    private static void add(List<Component> out, boolean enabled, String label, Double value, Double history) {
        if (!enabled || value == null) return;
        add(out, true, label, value.intValue(), history == null ? null : history.intValue());
    }

    private static Component priceLine(String label, int live, int history) {
        ColorSettings colors = ModConfig.getInstance().getColorSettings();
        String price = (ModConfig.getInstance().getTooltipSettings().getDisplayFormat() == DisplayOptions.FORMATTED)
                ? EmeraldUtils.getFormattedString(live, false)
                : StringUtils.formatNumber(live);

        int priceColor = ChatFormatting.GRAY.getColor();
        if (colors.isShowColors() && live >= colors.getColorMinPrice()) {
            priceColor = colors.getHighlightColor();
        }

        MutableComponent line = Component.literal(label + ": ").withStyle(Style.EMPTY.withColor(ChatFormatting.WHITE));
        line.append(Component.literal(price).withStyle(Style.EMPTY.withColor(priceColor)));

        if (ModConfig.getInstance().getTooltipSettings().isShowPriceFluctuation() && history > 0) {
            int percentage = (int) Math.round(((double) (live - history) / history) * 100);
            if (percentage != 0) {
                String sign = percentage > 0 ? "+" : "";
                ChatFormatting color = percentage > 0 ? ChatFormatting.GREEN : ChatFormatting.RED;
                line.append(Component.literal(" (" + sign + percentage + "%)").withStyle(Style.EMPTY.withColor(color)));
            }
        }

        return line;
    }
}
