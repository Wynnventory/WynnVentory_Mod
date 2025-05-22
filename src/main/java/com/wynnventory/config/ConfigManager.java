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

    // Favourite Notifier settings
    @ConfigEntry.Category(ConfigCategory.CATEGORY_GENERAL)
    @ConfigEntry.Gui.CollapsibleObject
    private FavouriteNotifierSettings favouriteNotifierSettings = new FavouriteNotifierSettings();

    @ConfigEntry.Category(ConfigCategory.CATEGORY_TOOLTIP)
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    private EmeraldDisplayOption displayDropdown = EmeraldDisplayOption.BOTH;

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

    @ConfigEntry.Category(ConfigCategory.CATEGORY_GENERAL)
    @ConfigEntry.Gui.Excluded
    private RarityConfig rarityConfig = new RarityConfig();

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

    public ColorSettings getColorSettings() {
        return colorSettings;
    }
    public void setColorSettings(ColorSettings colorSettings) {
        this.colorSettings = colorSettings;
    }

    public FavouriteNotifierSettings getFavouriteNotifierSettings() {
        return favouriteNotifierSettings;
    }
    public void setFavouriteNotifierSettings(FavouriteNotifierSettings favouriteNotifierSettings) {
        this.favouriteNotifierSettings = favouriteNotifierSettings;
    }

    public RarityConfig getRarityConfig() {
        return rarityConfig;
    }
    public void setRarityConfig(RarityConfig rarityConfig) {
        this.rarityConfig = rarityConfig;
    }

    public static class ColorSettings {
        private boolean showColors = false;
        private int colorMinPrice = 4096;

        @ConfigEntry.ColorPicker
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

    public static class RarityConfig implements ConfigData {

        // Controls whether Mythic items are shown
        private boolean showMythic = true;

        // Controls whether Fabled items are shown
        private boolean showFabled = true;

        // Controls whether Legendary items are shown
        private boolean showLegendary = true;

        // Controls whether Rare items are shown
        private boolean showRare = true;

        // Controls whether Unique items are shown
        private boolean showUnique = true;

        // Controls whether Unique items are shown
        private boolean showCommon = true;

        // Controls whether Unique items are shown
        private boolean showSet = true;

        private boolean showUnusable = true;

        // Getters and setters

        public boolean getShowMythic() {
            return showMythic;
        }

        public void setShowMythic(boolean showMythic) {
            this.showMythic = showMythic;
        }

        public boolean getShowFabled() {
            return showFabled;
        }

        public void setShowFabled(boolean showFabled) {
            this.showFabled = showFabled;
        }

        public boolean getShowLegendary() {
            return showLegendary;
        }

        public void setShowLegendary(boolean showLegendary) {
            this.showLegendary = showLegendary;
        }

        public boolean getShowRare() {
            return showRare;
        }

        public void setShowRare(boolean showRare) {
            this.showRare = showRare;
        }

        public boolean getShowUnique() {
            return showUnique;
        }

        public void setShowUnique(boolean showUnique) {
            this.showUnique = showUnique;
        }

        public boolean getShowCommon() {
            return showCommon;
        }

        public void setShowCommon(boolean showCommon) {
            this.showCommon = showCommon;
        }

        public boolean getShowSet() {
            return showSet;
        }

        public void setShowSet(boolean showSet) {
            this.showSet = showSet;
        }

        public boolean getShowUnusable() {
            return showUnusable;
        }

        public void setShowUnusable(boolean showUnusable) {
            this.showUnusable = showUnusable;
        }
    }

    public static class FavouriteNotifierSettings {
        private boolean enableNotifier = true;
        private int maxToasts = 5;
        private boolean mythicsOnly = false;

        public void setEnableNotifier(boolean enableNotifier) {
            this.enableNotifier = enableNotifier;
        }
        public boolean isEnableNotifier() {
            return enableNotifier;
        }
        public int getMaxToasts() {
            return maxToasts;
        }
        public void setMaxToasts(int maxToasts) {
            this.maxToasts = maxToasts;
        }
        public boolean isMythicsOnly() {
            return mythicsOnly;
        }
        public void setMythicsOnly(boolean mythicsOnly) {
            this.mythicsOnly = mythicsOnly;
        }
    }
}
