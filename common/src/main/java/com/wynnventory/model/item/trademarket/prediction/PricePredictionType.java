package com.wynnventory.model.item.trademarket.prediction;

import com.wynnventory.core.config.settings.TooltipSettings;
import java.util.function.Function;
import java.util.function.Predicate;

public enum PricePredictionType {
    ESTIMATED_PRICE(
            "feature.wynnventory.tooltip.prediction",
            TooltipSettings::isShowPricePrediction,
            response -> response.getEstimatedPrice() == null ? null : (double) response.getEstimatedPrice()),
    BASELINE_PRICE(
            "feature.wynnventory.tooltip.baseline",
            TooltipSettings::isShowContributingFactors,
            response -> response.getBaselinePrice() == null ? null : (double) response.getBaselinePrice());

    private final String label;
    private final Predicate<TooltipSettings> enabledCheck;
    private final Function<PricePredictionResponse, Double> extractor;

    PricePredictionType(
            String label,
            Predicate<TooltipSettings> enabledCheck,
            Function<PricePredictionResponse, Double> extractor) {
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

    public Double getValue(PricePredictionResponse response) {
        return response == null ? null : extractor.apply(response);
    }
}
