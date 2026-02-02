package com.wynnventory.core.config.settings;

public class TooltipSettings {
    private boolean showTooltips = true;
    private boolean showBoxedItemTooltips = true;
    private boolean anchorTooltips = true;
    private boolean showPriceFluctuation = true;

    private DisplayOptions displayFormat = DisplayOptions.FORMATTED;

    private boolean showMaxPrice = true;
    private boolean showMinPrice = true;
    private boolean showAveragePrice = false;
    private boolean showAverage80Price = true;
    private boolean showUnidAveragePrice = false;
    private boolean showUnidAverage80Price = true;

    public boolean isShowTooltips() {
        return showTooltips;
    }

    public void setShowTooltips(boolean showTooltips) {
        this.showTooltips = showTooltips;
    }

    public boolean isShowBoxedItemTooltips() {
        return showBoxedItemTooltips;
    }

    public void setShowBoxedItemTooltips(boolean showBoxedItemTooltips) {
        this.showBoxedItemTooltips = showBoxedItemTooltips;
    }

    public boolean isAnchorTooltips() {
        return anchorTooltips;
    }

    public void setAnchorTooltips(boolean anchorTooltips) {
        this.anchorTooltips = anchorTooltips;
    }

    public boolean isShowPriceFluctuation() {
        return showPriceFluctuation;
    }

    public void setShowPriceFluctuation(boolean showPriceFluctuation) {
        this.showPriceFluctuation = showPriceFluctuation;
    }

    public DisplayOptions getDisplayFormat() {
        return displayFormat;
    }

    public void setDisplayFormat(DisplayOptions displayFormat) {
        this.displayFormat = displayFormat;
    }

    public boolean isShowMaxPrice() {
        return showMaxPrice;
    }

    public void setShowMaxPrice(boolean showMaxPrice) {
        this.showMaxPrice = showMaxPrice;
    }

    public boolean isShowMinPrice() {
        return showMinPrice;
    }

    public void setShowMinPrice(boolean showMinPrice) {
        this.showMinPrice = showMinPrice;
    }

    public boolean isShowAveragePrice() {
        return showAveragePrice;
    }

    public void setShowAveragePrice(boolean showAveragePrice) {
        this.showAveragePrice = showAveragePrice;
    }

    public boolean isShowAverage80Price() {
        return showAverage80Price;
    }

    public void setShowAverage80Price(boolean showAverage80Price) {
        this.showAverage80Price = showAverage80Price;
    }

    public boolean isShowUnidAveragePrice() {
        return showUnidAveragePrice;
    }

    public void setShowUnidAveragePrice(boolean showUnidAveragePrice) {
        this.showUnidAveragePrice = showUnidAveragePrice;
    }

    public boolean isShowUnidAverage80Price() {
        return showUnidAverage80Price;
    }

    public void setShowUnidAverage80Price(boolean showUnidAverage80Price) {
        this.showUnidAverage80Price = showUnidAverage80Price;
    }
}
