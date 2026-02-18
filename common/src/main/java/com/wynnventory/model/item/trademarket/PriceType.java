package com.wynnventory.model.item.trademarket;

import com.wynnventory.core.config.settings.TooltipSettings;
import java.util.function.Function;
import java.util.function.Predicate;

public enum PriceType {
    AVG_80(
            "feature.wynnventory.tooltip.avg80",
            TooltipSettings::isShowAverage80Price,
            TrademarketItemSummary::getAverageMid80PercentPrice),
    UNID_AVG_80(
            "feature.wynnventory.tooltip.unidAvg80",
            TooltipSettings::isShowUnidAverage80Price,
            TrademarketItemSummary::getUnidentifiedAverageMid80PercentPrice),
    AVG(
            "feature.wynnventory.tooltip.avg",
            TooltipSettings::isShowAveragePrice,
            TrademarketItemSummary::getAveragePrice),
    UNID_AVG(
            "feature.wynnventory.tooltip.unidAvg",
            TooltipSettings::isShowUnidAveragePrice,
            TrademarketItemSummary::getUnidentifiedAveragePrice),
    HIGHEST(
            "feature.wynnventory.tooltip.highest",
            TooltipSettings::isShowMaxPrice,
            s -> s.getHighestPrice() == null ? null : (double) s.getHighestPrice()),
    LOWEST(
            "feature.wynnventory.tooltip.lowest",
            TooltipSettings::isShowMinPrice,
            s -> s.getLowestPrice() == null ? null : (double) s.getLowestPrice());

    private final String label;
    private final Predicate<TooltipSettings> enabledCheck;
    private final Function<TrademarketItemSummary, Double> extractor;

    PriceType(
            String label, Predicate<TooltipSettings> enabledCheck, Function<TrademarketItemSummary, Double> extractor) {
        this.label = label;
        this.enabledCheck = enabledCheck;
        this.extractor = extractor;
    }

    public String getLabel() {
        return label;
    }

    public boolean isEnabled(TooltipSettings settings) {
        return enabledCheck.test(settings);
    }

    public Double getValue(TrademarketItemSummary summary) {
        return (summary == null) ? null : extractor.apply(summary);
    }
}
