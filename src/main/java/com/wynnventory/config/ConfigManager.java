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
    FETCH_CONFIG(new File("config/Wynnventory/wynnventory_fetch.json"), 1, 5, 2),
    SEND_CONFIG(new File("config/Wynnventory/wynnventory_send.json"), 5, 30, 5);

    // Key Mappings
    private KeyMapping openConfigKey;
    private KeyMapping priceTooltipKey;
    private boolean showTooltip = false; // Ugly way to detect keypress in screens
    private boolean keyPressed = false; // Ugly way to detect keypress in screens

    private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private File configFile;

    // Config values in file
    private int minDelay;
    private int maxDelay;
    private int defaultDelay;
    private int userSetting;

    ConfigManager(File configFile, int minDelay, int maxDelay, int defaultDelay) {
        this.configFile = configFile;
        this.minDelay = minDelay;
        this.maxDelay = maxDelay;
        this.defaultDelay = defaultDelay;
    }

    public void loadConfig() {
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                ConfigManager config = GSON.fromJson(reader, ConfigManager.class);
                this.minDelay = config.minDelay;
                this.maxDelay = config.maxDelay;
                this.defaultDelay = config.defaultDelay;
                this.userSetting = validateUserSetting(this.userSetting);
            } catch (IOException e) {
                WynnventoryMod.error("Could not load config from: " + configFile);
            }
        }

        registerKeybinds();
    }

    public void saveConfig() {
        this.userSetting = validateUserSetting(this.userSetting);

        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            WynnventoryMod.error("Could not save config to: " + configFile);
        }
    }

    private void registerKeybinds() {
        openConfigKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.wynnventory.open_config",
                ConfigManager.GLFW.GLFW_KEY_N,
                "category.wynnventory.keybinding"
        ));
        priceTooltipKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.wynnventory.toggle_tooltip",
                ConfigManager.GLFW.GLFW_KEY_F,
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

    public boolean isShowTooltip() {
        return showTooltip;
    }

    public boolean isKeyPressed() {
        return keyPressed;
    }

    private int validateUserSetting(int value) {
        if (value < this.minDelay || value > this.maxDelay) {
            WynnventoryMod.warn("Config value: " + value + " outside of value range: " + this.minDelay + " - " + this.maxDelay + ". Setting to default value: " + this.defaultDelay);
            return this.defaultDelay;
        }

        return value;
    }
}
