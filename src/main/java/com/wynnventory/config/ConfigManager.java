package com.wynnventory.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.blaze3d.platform.InputConstants;
import com.wynnventory.WynnventoryMod;
import com.wynnventory.util.KeyMappingUtil;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File("config/wynnventory.json");

    // Singleton instance
    private static ConfigManager instance;

    private boolean showMaxPrice = true;
    private boolean showMinPrice = true;
    private boolean showAveragePrice = true;
    private boolean showAverage80Price = true;
    private boolean showUnidAveragePrice = true;
    private boolean showUnidAverage80Price = true;

    private ConfigManager() {
        loadConfig();
        registerKeybinds();
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    public void loadConfig() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                ConfigManager config = GSON.fromJson(reader, ConfigManager.class);
                this.showMaxPrice = validateValue(config.isShowMaxPrice());
                this.showMinPrice = validateValue(config.isShowMinPrice());
                this.showAveragePrice = validateValue(config.isShowAveragePrice());
                this.showAverage80Price = validateValue(config.isShowAverage80Price());
                this.showUnidAveragePrice = validateValue(config.isShowUnidAveragePrice());
                this.showUnidAverage80Price = validateValue(config.isShowUnidAverage80Price());
            } catch (Exception e) {
                WynnventoryMod.error("Could not load config from: " + CONFIG_FILE);
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

    private void registerKeybinds () {
        KeyMapping openConfigKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.wynnventory.open_config",
                GLFW.GLFW_KEY_N,
                "category.wynnventory.keybinding"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            handleOpenConfigKey(openConfigKey);
        });
    }

    private void handleOpenConfigKey(KeyMapping openConfigKey) {
        if (openConfigKey.consumeClick()) {
            Minecraft.getInstance().setScreen(ConfigScreen.createConfigScreen(Minecraft.getInstance().screen));
        }
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
