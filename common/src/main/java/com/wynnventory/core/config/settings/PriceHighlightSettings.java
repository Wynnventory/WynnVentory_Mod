package com.wynnventory.core.config.settings;

public final class PriceHighlightSettings {
    private boolean showColors = false;
    private int colorMinPrice = 4096;
    private int highlightColor = 65484;

    public boolean isShowColors() {
        return showColors;
    }

    public void setShowColors(boolean showColors) {
        this.showColors = showColors;
    }

    public int getColorMinPrice() {
        return colorMinPrice;
    }

    public void setColorMinPrice(int colorMinPrice) {
        this.colorMinPrice = colorMinPrice;
    }

    public int getHighlightColor() {
        return highlightColor;
    }

    public void setHighlightColor(int highlightColor) {
        this.highlightColor = highlightColor;
    }
}
