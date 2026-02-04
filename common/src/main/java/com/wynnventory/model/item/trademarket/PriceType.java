package com.wynnventory.model.item.trademarket;

import com.wynnventory.core.config.settings.TooltipSettings;

import java.util.function.Function;
import java.util.function.Predicate;

public enum PriceType {
    AVG_80("80% avg", TooltipSettings::isShowAverage80Price, TrademarketItemSummary::getAverageMid80PercentPrice),
    UNID_AVG_80("Unid 80% avg", TooltipSettings::isShowUnidAverage80Price, TrademarketItemSummary::getUnidentifiedAverageMid80PercentPrice),
    AVG("Avg", TooltipSettings::isShowAveragePrice, TrademarketItemSummary::getAveragePrice),
    UNID_AVG("Unid Avg", TooltipSettings::isShowUnidAveragePrice, TrademarketItemSummary::getUnidentifiedAveragePrice),
    HIGHEST("Highest", TooltipSettings::isShowMaxPrice, s -> s.getHighestPrice() == null ? null : (double) s.getHighestPrice()),
    LOWEST("Lowest", TooltipSettings::isShowMinPrice, s -> s.getLowestPrice() == null ? null : (double) s.getLowestPrice());

    private final String label;
    private final Predicate<TooltipSettings> enabledCheck;
    private final Function<TrademarketItemSummary, Double> extractor;

    PriceType(String label, Predicate<TooltipSettings> enabledCheck, Function<TrademarketItemSummary, Double> extractor) {
        this.label = label;
        this.enabledCheck = enabledCheck;
        this.extractor = extractor;
    }

    public String getLabel() { return label; }
    public boolean isEnabled(TooltipSettings settings) { return enabledCheck.test(settings); }
    public Double getValue(TrademarketItemSummary summary) {
        return (summary == null) ? null : extractor.apply(summary);
    }
}