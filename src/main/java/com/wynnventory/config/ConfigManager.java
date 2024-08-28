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
    private static final File CONFIG_FILE = new File("config/Wynnventory/config.json");

    // Key Mappings
    public static boolean SHOW_TOOLTIP = false;
    public static boolean KEY_PRESSED = false;

    // Config values for FETCH_CONFIG
    private transient final int fetchMinDelay = 1
    private transient final int fetchMaxDelay = 5
    private transient final int fetchDefaultDelay = 2
    private int fetchUserSetting = this.fetchDefaultDelay;

    // Config values for SEND_CONFIG
    private transient final int sendMinDelay = 5;
    private transient final int sendMaxDelay = 30;
    private transient final int sendDefaultDelay = 5;
    private int sendUserSetting = this.sendDefaultDelay;

    // Singleton instance
    private static ConfigManager instance;

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
                ConfigManager config = GSON.fromJson(reader, ConfigManager.class);
                this.fetchUserSetting = validateFetchUserSetting(config.getFetchUserSetting());
                this.sendUserSetting = validateSendUserSetting(config.getSendUserSetting());
            } catch (IOException e) {
                WynnventoryMod.error("Could not load config from: " + CONFIG_FILE);
            }
        } else {
            saveConfig(); // Save default config if not found
        }

        registerKeybinds();
    }

    public void saveConfig() {
        this.fetchUserSetting = validateFetchUserSetting(this.fetchUserSetting);
        this.sendUserSetting = validateSendUserSetting(this.sendUserSetting);

        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            WynnventoryMod.error("Could not save config to: " + CONFIG_FILE);
        }
    }

    private int validateValue(int value, int min, int max, int defaultValue) {
        if (value < min || value > max) {
            WynnventoryMod.warn("Config value: " + value + " outside of value range: " + min + " - " + max + ". Setting to default value: " + defaultValue);
            return defaultValue;
        }

        return value;
    }

    private int validateFetchUserSetting(int value) {
        return validateValue(value, fetchMinDelay, fetchMaxDelay, fetchDefaultDelay) {
    }

    private int validateSendUserSetting(int value) {
        return validateValue(value, sendMinDelay, sendMaxDelay, sendDefaultDelay) {
    }

    private void registerKeybinds() {
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
            handlePriceTooltipKey(client, priceTooltipKey);
        });
    }

    private void handleOpenConfigKey(KeyMapping openConfigKey) {
        if (openConfigKey.consumeClick()) {
            Minecraft.getInstance().setScreen(ConfigScreen.createConfigScreen(Minecraft.getInstance().screen));
        }
    }

    private void handlePriceTooltipKey(Minecraft client, KeyMapping priceTooltipKey) {
        if (client.screen != null || client.player != null) {
            long windowHandle = Minecraft.getInstance().getWindow().getWindow();
            int keyCode = Objects.requireNonNull(KeyMappingUtil.getBoundKey(priceTooltipKey)).getValue();

            if (InputConstants.isKeyDown(windowHandle, keyCode)) {
                if (!KEY_PRESSED) {
                    SHOW_TOOLTIP = !SHOW_TOOLTIP;
                }
                KEY_PRESSED = true;
            } else {
                KEY_PRESSED = false;
            }
        }
    }

        // Getters and Setters for FETCH_CONFIG
    public int getFetchMinDelay() {
        return fetchMinDelay;
    }

    public int getFetchMaxDelay() {
        return fetchMaxDelay;
    }

    public int getFetchDefaultDelay() {
        return fetchDefaultDelay;
    }

    public int getFetchUserSetting() {
        return fetchUserSetting;
    }

    public void setFetchUserSetting(int fetchUserSetting) {
        this.fetchUserSetting = validateFetchUserSetting(fetchUserSetting);
    }

    // Getters and Setters for SEND_CONFIG
    public int getSendMinDelay() {
        return sendMinDelay;
    }

    public int getSendMaxDelay() {
        return sendMaxDelay;
    }

    public int getSendDefaultDelay() {
        return sendDefaultDelay;
    }

    public int getSendUserSetting() {
        return sendUserSetting;
    }

    public void setSendUserSetting(int sendUserSetting) {
        this.sendUserSetting = validateSendUserSetting(sendUserSetting);
    }
}
