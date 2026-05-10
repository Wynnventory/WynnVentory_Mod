package com.wynnventory.feature.tooltip.price;

import com.wynntils.models.emeralds.type.EmeraldUnits;
import com.wynnventory.core.config.ModConfig;
import com.wynnventory.core.config.settings.DisplayOptions;
import com.wynnventory.core.config.settings.PriceHighlightSettings;
import com.wynnventory.core.config.settings.TooltipSettings;
import com.wynnventory.model.item.trademarket.PriceType;
import com.wynnventory.model.item.trademarket.TrademarketItemSnapshot;
import com.wynnventory.model.item.trademarket.prediction.PriceContribution;
import com.wynnventory.model.item.trademarket.prediction.PricePredictionResponse;
import com.wynnventory.model.item.trademarket.prediction.PricePredictionType;
import com.wynnventory.util.EmeraldUtils;
import com.wynnventory.util.StringUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

    public List<Component> buildPricePredictionTooltip(
            PricePredictionResponse prediction, Map<String, String> statDisplayNames) {
        if (prediction == null) return List.of();

        List<Component> out = new ArrayList<>();
        TooltipSettings ts = ModConfig.getInstance().getTooltipSettings();

        for (PricePredictionType type : PricePredictionType.values()) {
            add(out, type.isEnabled(ts), type.getLabel(), type.getValue(prediction), 0d);
        }

        if (out.isEmpty()) return List.of();

        if (prediction.getContributions() != null
                && !prediction.getContributions().isEmpty()) {
            out.add(Component.translatable("feature.wynnventory.tooltip.contributions")
                    .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
            prediction.getContributions().stream()
                    .filter(PriceTooltipBuilder::isRenderableContribution)
                    .sorted(Comparator.comparing(
                                    PriceContribution::getPriceMultiplier,
                                    Comparator.nullsLast(Comparator.reverseOrder()))
                            .thenComparing(PriceContribution::getApiName, Comparator.nullsLast(String::compareTo)))
                    .map(contribution -> contributionLine(contribution, statDisplayNames))
                    .forEach(out::add);
        }

        return out;
    }

    private static void add(List<Component> out, boolean enabled, String label, Double live, Double history) {
        if (!enabled || live == null) return;
        out.add(priceLine(label, live.intValue(), history == null ? 0 : history.intValue()));
    }

    private static Component priceLine(String label, int live, int history) {
        PriceHighlightSettings colors = ModConfig.getInstance().getPriceHighlightSettings();
        String price = formatPrice(live);

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

    private static String formatPrice(int price) {
        return (ModConfig.getInstance().getTooltipSettings().getDisplayFormat() == DisplayOptions.FORMATTED)
                ? EmeraldUtils.getFormattedString(price, false)
                : StringUtils.formatNumber(price) + EmeraldUnits.EMERALD.getSymbol();
    }

    private static boolean isRenderableContribution(PriceContribution contribution) {
        return contribution != null
                && contribution.getApiName() != null
                && !contribution.getApiName().isBlank()
                && (contribution.getRollPercentage() != null || contribution.getPriceMultiplier() != null);
    }

    private static Component contributionLine(PriceContribution contribution, Map<String, String> statDisplayNames) {
        MutableComponent line = Component.literal("  ");
        line.append(Component.literal(displayName(contribution.getApiName(), statDisplayNames))
                .withStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)));

        if (contribution.getRollPercentage() != null) {
            line.append(Component.literal(": ").withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY)));
            line.append(Component.literal(formatPercentage(contribution.getRollPercentage()))
                    .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
        }

        if (contribution.getPriceMultiplier() != null) {
            line.append(Component.literal(" (").withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY)));
            line.append(Component.literal("x" + formatMultiplier(contribution.getPriceMultiplier()))
                    .withStyle(Style.EMPTY.withColor(multiplierColor(contribution.getPriceMultiplier()))));
            line.append(Component.literal(")").withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY)));
        }

        return line;
    }

    private static String displayName(String apiName, Map<String, String> statDisplayNames) {
        if (statDisplayNames == null) return apiName;

        String displayName = statDisplayNames.get(apiName);
        return displayName == null || displayName.isBlank() ? apiName : displayName;
    }

    private static String formatPercentage(double percentage) {
        return String.format(Locale.ROOT, "%.0f%%", percentage);
    }

    private static String formatMultiplier(double multiplier) {
        return String.format(Locale.ROOT, "%.2f", multiplier);
    }

    private static ChatFormatting multiplierColor(double multiplier) {
        if (multiplier > 1.0d) return ChatFormatting.GREEN;
        if (multiplier < 1.0d) return ChatFormatting.RED;

        return ChatFormatting.GRAY;
    }
}
