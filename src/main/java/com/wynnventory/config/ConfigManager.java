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

    // Key Mappings
    public static boolean SHOW_TOOLTIP = false;
    public static boolean KEY_PRESSED = true;

    public static final int SEND_MIN_DELAY_MINS = 5;
    public static final int SEND_MAX_DELAY_MINS = 30;
    public static final int SEND_DEFAULT_DELAY_MINS = 5;


    public static final int FETCH_MIN_DELAY_MINS = 1;
    public static final int FETCH_MAX_DELAY_MINS = 5;
    public static final int FETCH_DEFAULT_DELAY_MINS = 2;

    // Singleton instance
    private static ConfigManager instance;

    private int sendUserSetting = SEND_DEFAULT_DELAY_MINS;
    private int fetchUserSetting = FETCH_DEFAULT_DELAY_MINS;

    private ConfigManager() {
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
                this.fetchUserSetting = validateFetchUserSetting(config.getFetchUserSetting());
                this.sendUserSetting = validateSendUserSetting(config.getSendUserSetting());
            } catch (IOException | NullPointerException e) {
                WynnventoryMod.error("Could not load config from: " + CONFIG_FILE);
                saveConfig();
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
        return validateValue(value, FETCH_MIN_DELAY_MINS, FETCH_MAX_DELAY_MINS, FETCH_DEFAULT_DELAY_MINS);
    }

    private int validateSendUserSetting(int value) {
        return validateValue(value, SEND_MIN_DELAY_MINS, SEND_MAX_DELAY_MINS, SEND_DEFAULT_DELAY_MINS);
    }

    private void registerKeybinds () {
        KeyMapping openConfigKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.wynnventory.open_config",
                GLFW.GLFW_KEY_N,
                "category.wynnventory.keybinding"
        ));

        KeyMapping priceTooltipKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.wynnventory.toggle_tooltip",
                GLFW.GLFW_KEY_PERIOD,
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
        if (client.screen != null && client.player != null && KeyMappingUtil.getBoundKey(priceTooltipKey) != null) {
            System.out.println(priceTooltipKey.toString());
            System.out.println(KeyMappingUtil.getBoundKey(priceTooltipKey).toString());
            System.out.println(Objects.requireNonNull(KeyMappingUtil.getBoundKey(priceTooltipKey)).getValue());
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

    public int getFetchUserSetting () {
        return fetchUserSetting;
    }

    public void setFetchUserSetting ( int fetchUserSetting){
        this.fetchUserSetting = validateFetchUserSetting(fetchUserSetting);
    }

    public int getSendUserSetting () {
        return sendUserSetting;
    }

    public void setSendUserSetting ( int sendUserSetting){
        this.sendUserSetting = validateSendUserSetting(sendUserSetting);
    }
}
