package com.wynnventory.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wynnventory.WynnventoryMod;
import com.wynnventory.api.WynnventoryScheduler;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File("config/wynnventory.json");

    // Boundaries & Defaults
    private static final int MIN_SEND_DELAY_MINS = 1;
    private static final int MAX_SEND_DELAY_MINS = 30;

    public static final int DEFAULT_SEND_DELAY_MINS = 5;

    // Config values
    private int sendDelayMins;

    public static void loadConfig() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                ConfigManager config = GSON.fromJson(reader, ConfigManager.class);

                config.validateConfig();

                WynnventoryScheduler.SEND_DELAY_MINS = config.sendDelayMins;
            } catch (IOException e) {
                WynnventoryMod.error("Could not load config from: " + CONFIG_FILE);
            }
        } else {
            saveConfig(); // save default config
        }
    }

    public static void saveConfig() {
        ConfigManager config = new ConfigManager();
        config.validateConfig();
        config.sendDelayMins = WynnventoryScheduler.SEND_DELAY_MINS;

        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            WynnventoryMod.error("Could not save config to: " + CONFIG_FILE);
        }
    }

    private void validateConfig() {
        if (this.sendDelayMins < MIN_SEND_DELAY_MINS || this.sendDelayMins > MAX_SEND_DELAY_MINS) {
            this.sendDelayMins = DEFAULT_SEND_DELAY_MINS;
        }
    }
}
