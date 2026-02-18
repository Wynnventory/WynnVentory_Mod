package com.wynnventory.core.tooltip;

import com.wynnventory.core.config.ModConfig;
import com.wynnventory.core.config.settings.DisplayOptions;
import com.wynnventory.core.config.settings.PriceHighlightSettings;
import com.wynnventory.core.config.settings.TooltipSettings;
import com.wynnventory.model.item.trademarket.PriceType;
import com.wynnventory.model.item.trademarket.TrademarketItemSnapshot;
import com.wynnventory.util.EmeraldUtils;
import com.wynnventory.util.StringUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public final class PriceTooltipBuilder {
    public List<Component> buildPriceTooltip(TrademarketItemSnapshot snapshot, Component title) {
        List<Component> out = new ArrayList<>();
        out.add(title);

        if (snapshot == null || snapshot.live() == null || snapshot.live().isEmpty()) {
            out.add(Component.literal("No data yet.").withStyle(ChatFormatting.RED));
            return out;
        }

        TooltipSettings ts = ModConfig.getInstance().getTooltipSettings();

        for (PriceType type : PriceType.values()) {
            if (type.isEnabled(ts)) {
                TrademarketItemSnapshot.PriceData data = snapshot.getPriceData(type);
                add(out, true, type.getLabel(), data.live(), data.historic());
            }
        }

        return out;
    }

    private static void add(List<Component> out, boolean enabled, String label, Double live, Double history) {
        if (!enabled || live == null) return;
        out.add(priceLine(label, live.intValue(), history == null ? 0 : history.intValue()));
    }

    private static Component priceLine(String label, int live, int history) {
        PriceHighlightSettings colors = ModConfig.getInstance().getPriceHighlightSettings();
        String price = (ModConfig.getInstance().getTooltipSettings().getDisplayFormat() == DisplayOptions.FORMATTED)
                ? EmeraldUtils.getFormattedString(live, false)
                : StringUtils.formatNumber(live);

        int priceColor = ChatFormatting.GRAY.getColor();
        if (colors.isShowColors() && live >= colors.getColorMinPrice()) {
            priceColor = colors.getHighlightColor();
        }

        MutableComponent line = Component.translatable(label).withStyle(Style.EMPTY.withColor(ChatFormatting.WHITE));
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
