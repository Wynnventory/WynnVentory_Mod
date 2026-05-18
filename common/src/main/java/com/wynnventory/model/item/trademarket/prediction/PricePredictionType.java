package com.wynnventory.model.item.trademarket.prediction;

import com.wynnventory.core.config.settings.PricePredictionSettings;
import java.util.function.Function;
import java.util.function.Predicate;

public enum PricePredictionType {
    ESTIMATED_PRICE(
            "feature.wynnventory.tooltip.prediction",
            PricePredictionSettings::isShowPricePrediction,
            response -> response.getEstimatedPrice() == null ? null : (double) response.getEstimatedPrice());

    private final String label;
    private final Predicate<PricePredictionSettings> enabledCheck;
    private final Function<PricePredictionResponse, Double> extractor;

    PricePredictionType(
            String label,
            Predicate<PricePredictionSettings> enabledCheck,
            Function<PricePredictionResponse, Double> extractor) {
        this.label = label;
        this.enabledCheck = enabledCheck;
        this.extractor = extractor;
    }

    public String getLabel() {
        return label;
    }

    public boolean isEnabled(PricePredictionSettings settings) {
        return enabledCheck.test(settings);
    }

    public Double getValue(PricePredictionResponse response) {
        return response == null ? null : extractor.apply(response);
    }
}
