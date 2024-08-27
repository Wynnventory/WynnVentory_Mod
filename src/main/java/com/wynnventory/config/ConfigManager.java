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

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File("config/wynnventory.json");

    public static KeyMapping OPEN_CONFIG_KEY;
    public static KeyMapping PRICE_TOOLTIP_KEY;
    public static boolean SHOW_TOOLTIP = false; // Ugly way to detect keypress in screens
    private static boolean wasKeyPressed = false; // Ugly way to detect keypress in screens

    // Boundaries & Defaults
    public static final int MIN_SEND_DELAY_MINS = 5;
    public static final int MAX_SEND_DELAY_MINS = 30;
    public static final int MIN_FETCH_DELAY_MINS = 1;
    public static final int MAX_FETCH_DELAY_MINS = 5;

    public static final int DEFAULT_OPEN_CONFIG_KEY = GLFW.GLFW_KEY_N;
    public static final int DEFAULT_DISPLAY_PRICE_TOOLTIP = GLFW.GLFW_KEY_F;
    public static final int DEFAULT_SEND_DELAY_MINS = 5;
    public static final int DEFAULT_FETCH_DELAY_MINS = 2;

    // Config values in file
    public static int SEND_DELAY_MINS;
    private int sendDelayMins;
    public static int FETCH_DELAY_MINS;
    private int fetchDelayMins;

    public static void loadConfig() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                ConfigManager config = GSON.fromJson(reader, ConfigManager.class);

                config = validateConfig(config);
                config.registerKeybinds();

                SEND_DELAY_MINS = config.sendDelayMins;
                FETCH_DELAY_MINS = config.fetchDelayMins;
            } catch (IOException e) {
                WynnventoryMod.error("Could not load config from: " + CONFIG_FILE);
            }
        } else {
            saveConfig(); // save default config
        }
    }

    public static void saveConfig() {
        ConfigManager config = new ConfigManager();

        config.sendDelayMins = SEND_DELAY_MINS;
        config.fetchDelayMins = FETCH_DELAY_MINS;
        config = validateConfig(config);

        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            WynnventoryMod.error("Could not save config to: " + CONFIG_FILE);
        }
    }

    public static ConfigManager validateConfig(ConfigManager config) {
        if (config == null) config = new ConfigManager();
        if (config.sendDelayMins < MIN_SEND_DELAY_MINS || config.sendDelayMins > MAX_SEND_DELAY_MINS) {
            config.sendDelayMins = DEFAULT_SEND_DELAY_MINS;
        }
        if (config.fetchDelayMins < MIN_FETCH_DELAY_MINS || config.fetchDelayMins > MAX_FETCH_DELAY_MINS) {
            config.fetchDelayMins = DEFAULT_FETCH_DELAY_MINS;
        }
        SEND_DELAY_MINS = config.sendDelayMins;
        FETCH_DELAY_MINS = config.fetchDelayMins;
        return config;
    }

    private void registerKeybinds() {
        OPEN_CONFIG_KEY = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.wynnventory.open_config",
                ConfigManager.DEFAULT_OPEN_CONFIG_KEY,
                "category.wynnventory.keybinding"
        ));
        PRICE_TOOLTIP_KEY = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.wynnventory.toggle_tooltip",
                ConfigManager.DEFAULT_DISPLAY_PRICE_TOOLTIP,
                "category.wynnventory.keybinding"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (OPEN_CONFIG_KEY.consumeClick()) {
                Minecraft.getInstance().setScreen(ConfigScreen.createConfigScreen(Minecraft.getInstance().screen));
            }
            if (client.screen != null || client.player != null) {
                long windowHandle = Minecraft.getInstance().getWindow().getWindow();
                int keyCode = Objects.requireNonNull(KeyMappingUtil.getBoundKey(PRICE_TOOLTIP_KEY)).getValue();

                if (InputConstants.isKeyDown(windowHandle, keyCode)) {
                    if (!wasKeyPressed) {
                        SHOW_TOOLTIP = !SHOW_TOOLTIP;
                    }
                    wasKeyPressed = true;
                } else {
                    wasKeyPressed = false;
                }
            }
        });
    }
}
