package com.wynnventory.core.config.settings;

public class PricePredictionSettings {
    private boolean showPricePrediction = true;
    private boolean showContributingFactors = false;

    public boolean isShowPricePrediction() {
        return showPricePrediction;
    }

    public void setShowPricePrediction(boolean showPricePrediction) {
        this.showPricePrediction = showPricePrediction;
    }

    public boolean isShowContributingFactors() {
        return showContributingFactors;
    }

    public void setShowContributingFactors(boolean showContributingFactors) {
        this.showContributingFactors = showContributingFactors;
    }
}
