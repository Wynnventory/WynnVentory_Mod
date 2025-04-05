package com.wynnventory.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "wynnventory")
public class ConfigManager implements ConfigData {
    @ConfigEntry.Category(ConfigCategory.CATEGORY_GENERAL)
    private boolean showTooltips = true;

    @ConfigEntry.Category(ConfigCategory.CATEGORY_GENERAL)
    private boolean showBoxedItemTooltips = true;

    @ConfigEntry.Category(ConfigCategory.CATEGORY_GENERAL)
    private boolean showPriceFluctuation = true;

    @ConfigEntry.Category(ConfigCategory.CATEGORY_GENERAL)
    private boolean anchorTooltips = true;

    // Color settings grouped in a collapsible object
    @ConfigEntry.Category(ConfigCategory.CATEGORY_GENERAL)
    @ConfigEntry.Gui.CollapsibleObject
    private ColorSettings colorSettings = new ColorSettings();

    @ConfigEntry.Category(ConfigCategory.CATEGORY_TOOLTIP)
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    private EmeraldDisplayOption displayDropdown = EmeraldDisplayOption.OPTION_BOTH;

    @ConfigEntry.Category(ConfigCategory.CATEGORY_TOOLTIP)
    private boolean showMaxPrice = false;

    @ConfigEntry.Category(ConfigCategory.CATEGORY_TOOLTIP)
    private boolean showMinPrice = false;

    @ConfigEntry.Category(ConfigCategory.CATEGORY_TOOLTIP)
    private boolean showAveragePrice = false;

    @ConfigEntry.Category(ConfigCategory.CATEGORY_TOOLTIP)
    private boolean showAverage80Price = true;

    @ConfigEntry.Category(ConfigCategory.CATEGORY_TOOLTIP)
    private boolean showUnidAveragePrice = false;

    @ConfigEntry.Category(ConfigCategory.CATEGORY_TOOLTIP)
    private boolean showUnidAverage80Price = true;

    // Static getter to retrieve the instance managed by AutoConfig
    public static ConfigManager getInstance() {
        return AutoConfig.getConfigHolder(ConfigManager.class).getConfig();
    }

    // Getters and setters for general settings
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
    public boolean isShowPriceFluctuation() {
        return showPriceFluctuation;
    }
    public void setShowPriceFluctuation(boolean showPriceFluctuation) {
        this.showPriceFluctuation = showPriceFluctuation;
    }
    public boolean isAnchorTooltips() {
        return anchorTooltips;
    }
    public void setAnchorTooltips(boolean anchorTooltips) {
        this.anchorTooltips = anchorTooltips;
    }

    // Getters and setters for tooltip config
    public EmeraldDisplayOption getPriceFormat() {
        return displayDropdown;
    }

    public void setMyDropdownOption(EmeraldDisplayOption myDropdownOption) {
        this.displayDropdown = myDropdownOption;
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

    // Getter and setter for ColorSettings
    public ColorSettings getColorSettings() {
        return colorSettings;
    }
    public void setColorSettings(ColorSettings colorSettings) {
        this.colorSettings = colorSettings;
    }

    public static class ColorSettings {
        private boolean showColors = false;
        private int colorMinPrice = 4096;

        @ConfigEntry.ColorPicker
        private int highlightColor = 16711884;

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
}
