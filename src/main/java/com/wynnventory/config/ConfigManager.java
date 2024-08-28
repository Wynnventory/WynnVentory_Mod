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
    FETCH_CONFIG(new File("config/Wynnventory/fetch_config.json"), 1, 5, 2),
    SEND_CONFIG(new File("config/Wynnventory/send_config.json"), 5, 30, 5);

    // Key Mappings
    private static boolean SHOW_TOOLTIP = false; // Ugly way to detect keypress in screens
    public static boolean KEY_PRESSED = false; // Ugly way to detect keypress in screens

    private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private File configFile;

    // Config values in file
    private int minDelay;
    private int maxDelay;
    private int defaultDelay;
    private int userSetting;

    // Initialize with default values
    ConfigManager(File configFile, int minDelay, int maxDelay, int defaultDelay) {
        this.configFile = configFile;
        this.minDelay = minDelay;
        this.maxDelay = maxDelay;
        this.defaultDelay = defaultDelay;
        this.userSetting = this.defaultDelay;

        loadConfig();
    }

    public void loadConfig() {
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                ConfigManager config = GSON.fromJson(reader, ConfigManager.class);
                this.userSetting = validateUserSetting(config.userSetting);
            } catch (IOException e) {
                WynnventoryMod.error("Could not load config from: " + configFile);
            }
        } else {
            saveConfig();
        }
    }

    private void saveConfig() {
        this.userSetting = validateUserSetting(this.userSetting);

        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            WynnventoryMod.error("Could not save config to: " + configFile);
        }
    }

    public int getMinDelay() {
        return minDelay;
    }

    public int getMaxDelay() {
        return maxDelay;
    }

    public int getDefaultDelay() {
        return defaultDelay;
    }

    public int getUserSetting() {
        return userSetting;
    }

    public int setUserSetting(int userSetting) {
        this.userSetting = validateUserSetting(userSetting);
    }

    private int validateUserSetting(int value) {
        if (value < this.minDelay || value > this.maxDelay) {
            WynnventoryMod.warn("Config value: " + value + " outside of value range: " + this.minDelay + " - " + this.maxDelay + ". Setting to default value: " + this.defaultDelay);
            return this.defaultDelay;
        }

        return value;
    }

    public static void saveConfigs() {
        for(ConfigManager config : ConfigManager.values()) {
            config.saveConfig();
        }
    }

    public static void registerKeybinds() {
        KeyMapping openConfigKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.wynnventory.open_config",
                GLFW.GLFW_KEY_N,
                "category.wynnventory.keybinding"
        ));

        KeyMapping priceTooltipKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.wynnventory.toggle_tooltip",
                GLFW.GLFW_KEY_F,
                "category.wynnventory.keybinding"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            handleOpenConfigKey(openConfigKey);
            handlePriceTooltipKey(client, priceTooltipKey)    
        });
    }

    private static void handleOpenConfigKey(KeyMapping openConfigKey) {
        if (openConfigKey.consumeClick()) {
            Minecraft.getInstance().setScreen(ConfigScreen.createConfigScreen(Minecraft.getInstance().screen));
        }
    }

    private static void handlePriceTooltipKey(Minecraft client, KeyMapping priceTooltipKey) {
        if (client.screen != null || client.player != null) {
            long windowHandle = Minecraft.getInstance().getWindow().getWindow();
            int keyCode = priceTooltipKey.getBoundKey().getValue();

            if (InputUtil.isKeyPressed(windowHandle, keyCode)) {
                if (!KEY_PRESSED) {
                    SHOW_TOOLTIP = !SHOW_TOOLTIP
                }
                KEY_PRESSED = true;
            } else {
                KEY_PRESSED = false;
            }
        }
    }
}
