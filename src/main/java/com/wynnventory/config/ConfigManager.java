package com.wynnventory.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.blaze3d.platform.InputConstants;
import com.wynnventory.WynnventoryMod;
import com.wynnventory.api.WynnventoryScheduler;
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

public enum ConfigManager {
    WYNNVENTORY_CONFIG;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File("config/wynnventory.json");

    // Boundaries
    public static final int MIN_SEND_DELAY_MINS = 5;
    public static final int MAX_SEND_DELAY_MINS = 30;
    public static final int MIN_FETCH_DELAY_MINS = 1;
    public static final int MAX_FETCH_DELAY_MINS = 5;

    // Defaults
    public static final int DEFAULT_OPEN_CONFIG_KEY = GLFW.GLFW_KEY_N;
    public static final int DEFAULT_DISPLAY_PRICE_TOOLTIP = GLFW.GLFW_KEY_F;
    public static final int DEFAULT_SEND_DELAY_MINS = 5;
    public static final int DEFAULT_FETCH_DELAY_MINS = 2;

    // Key Mappings
    private KeyMapping openConfigKey;
    private KeyMapping priceTooltipKey;
    private boolean showTooltip = false; // Ugly way to detect keypress in screens
    private boolean keyPressed = false; // Ugly way to detect keypress in screens

    // Config values in file
    private int sendDelayMins;
    private int fetchDelayMins;

    ConfigManager() { }

    public void loadConfig() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                ConfigManager config = GSON.fromJson(reader, ConfigManager.class);
                this.sendDelayMins = config.sendDelayMins;
                this.fetchDelayMins = config.fetchDelayMins;
                validateConfig();
            } catch (IOException e) {
                WynnventoryMod.error("Could not load config from: " + CONFIG_FILE);
            }
        }

        registerKeybinds();
    }

    public void saveConfig() {
        validateConfig();
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            WynnventoryMod.error("Could not save config to: " + CONFIG_FILE);
        }
    }

    private void registerKeybinds() {
        openConfigKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.wynnventory.open_config",
                ConfigManager.DEFAULT_OPEN_CONFIG_KEY,
                "category.wynnventory.keybinding"
        ));
        priceTooltipKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.wynnventory.toggle_tooltip",
                ConfigManager.DEFAULT_DISPLAY_PRICE_TOOLTIP,
                "category.wynnventory.keybinding"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openConfigKey.consumeClick()) {
                Minecraft.getInstance().setScreen(ConfigScreen.createConfigScreen(Minecraft.getInstance().screen));
            }
            if (client.screen != null || client.player != null) {
                long windowHandle = Minecraft.getInstance().getWindow().getWindow();
                int keyCode = Objects.requireNonNull(KeyMappingUtil.getBoundKey(priceTooltipKey)).getValue();

                if (InputConstants.isKeyDown(windowHandle, keyCode)) {
                    if (!keyPressed) {
                        showTooltip = !showTooltip;
                    }
                    keyPressed = true;
                } else {
                    keyPressed = false;
                }
            }
        });
    }

    private void validateConfig() {
        this.sendDelayMins = validateValue(this.sendDelayMins, MIN_SEND_DELAY_MINS, MAX_SEND_DELAY_MINS, DEFAULT_SEND_DELAY_MINS);
        this.fetchDelayMins = validateValue(this.fetchDelayMins, MIN_FETCH_DELAY_MINS, MAX_FETCH_DELAY_MINS, DEFAULT_FETCH_DELAY_MINS);
    }

    public int getSendDelayMins() {
        return sendDelayMins;
    }

    public void setSendDelayMins(int sendDelayMins) {
        this.sendDelayMins = validateValue(sendDelayMins, MIN_SEND_DELAY_MINS, MAX_SEND_DELAY_MINS, DEFAULT_SEND_DELAY_MINS);
    }

    public int getFetchDelayMins() {
        return fetchDelayMins;
    }

    public void setFetchDelayMins(int fetchDelayMins) {
        this.fetchDelayMins = validateValue(fetchDelayMins, MIN_FETCH_DELAY_MINS, MAX_FETCH_DELAY_MINS, DEFAULT_FETCH_DELAY_MINS);
    }

    public KeyMapping getOpenConfigKey() {
        return openConfigKey;
    }

    public KeyMapping getPriceTooltipKey() {
        return priceTooltipKey;
    }

    public boolean isShowTooltip() {
        return showTooltip;
    }

    public boolean isKeyPressed() {
        return keyPressed;
    }

    private int validateValue(int value, int minValue, int maxValue, int defaultValue) {
        if (value == null || value < minValue || value > maxValue) {
            WynnventoryMod.warn("Config value: " + value + " outside of value range: " + minValue + " - " + maxValue + ". Setting to default value: " + defaultValue);
            return defaultValue;
        }

        return value;
    }
}
