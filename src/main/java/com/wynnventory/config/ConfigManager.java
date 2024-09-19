package com.wynnventory.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wynnventory.WynnventoryMod;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File("config/wynnventory.json");

    // Singleton instance
    private static ConfigManager instance;

    // General
    private boolean showTooltips = false;
//    private boolean showBoxedItemTooltips = true;
//    private boolean anchorTooltips = true;
//
//    // Tooltip config
//    private boolean showMaxPrice = true;
//    private boolean showMinPrice = true;
//    private boolean showAveragePrice = true;
//    private boolean showAverage80Price = true;
//    private boolean showUnidAveragePrice = true;
//    private boolean showUnidAverage80Price = true;

    private ConfigManager() { }

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    public void loadConfig() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                ObjectMapper objectMapper = new ObjectMapper();
                ConfigManager config = objectMapper.readValue(reader, ConfigManager.class);
//                this.showTooltips = validateValue(config.isShowTooltips());
                this.showTooltips = false;
//                this.showBoxedItemTooltips = validateValue(config.isShowBoxedItemTooltips());
//                this.anchorTooltips = validateValue(config.isAnchorTooltips());
//                this.showMaxPrice = validateValue(config.isShowMaxPrice());
//                this.showMinPrice = validateValue(config.isShowMinPrice());
//                this.showAveragePrice = validateValue(config.isShowAveragePrice());
//                this.showAverage80Price = validateValue(config.isShowAverage80Price());
//                this.showUnidAveragePrice = validateValue(config.isShowUnidAveragePrice());
//                this.showUnidAverage80Price = validateValue(config.isShowUnidAverage80Price());
            } catch (Exception e) {
                WynnventoryMod.error("Could not load config from: " + CONFIG_FILE, e);
                saveConfig();
            }
        } else {
            saveConfig(); // Save default config if not found
        }
    }

    public void saveConfig() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            WynnventoryMod.error("Could not save config to: " + CONFIG_FILE);
        }
    }

    private boolean validateValue(Object value) {
        if (value instanceof Boolean bool) {
            WynnventoryMod.warn("Config value: " + value);
            return bool;
        }

        return true;
    }

    public boolean isShowTooltips() {
        return showTooltips;
    }

    public void setShowTooltips(boolean showTooltips) {
        this.showTooltips = showTooltips;
    }

//    public boolean isShowBoxedItemTooltips() {
//        return showBoxedItemTooltips;
//    }
//
//    public void setShowBoxedItemTooltips(boolean showBoxedItemTooltips) {
//        this.showBoxedItemTooltips = showBoxedItemTooltips;
//    }
//
//    public boolean isAnchorTooltips() {
//        return anchorTooltips;
//    }
//
//    public void setAnchorTooltips(boolean anchorTooltips) {
//        this.anchorTooltips = anchorTooltips;
//    }
//
//    public boolean isShowMaxPrice() {
//        return showMaxPrice;
//    }
//
//    public void setShowMaxPrice(boolean showMaxPrice) {
//        this.showMaxPrice = showMaxPrice;
//    }
//
//    public boolean isShowMinPrice() {
//        return showMinPrice;
//    }
//
//    public void setShowMinPrice(boolean showMinPrice) {
//        this.showMinPrice = showMinPrice;
//    }
//
//    public boolean isShowAveragePrice() {
//        return showAveragePrice;
//    }
//
//    public void setShowAveragePrice(boolean showAveragePrice) {
//        this.showAveragePrice = showAveragePrice;
//    }
//
//    public boolean isShowAverage80Price() {
//        return showAverage80Price;
//    }
//
//    public void setShowAverage80Price(boolean showAverage80Price) {
//        this.showAverage80Price = showAverage80Price;
//    }
//
//    public boolean isShowUnidAveragePrice() {
//        return showUnidAveragePrice;
//    }
//
//    public void setShowUnidAveragePrice(boolean showUnidAveragePrice) {
//        this.showUnidAveragePrice = showUnidAveragePrice;
//    }
//
//    public boolean isShowUnidAverage80Price() {
//        return showUnidAverage80Price;
//    }
//
//    public void setShowUnidAverage80Price(boolean showUnidAverage80Price) {
//        this.showUnidAverage80Price = showUnidAverage80Price;
//    }
}
