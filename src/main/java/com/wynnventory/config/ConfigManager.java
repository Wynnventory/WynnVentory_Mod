package com.wynnventory.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File("config/yourmodname.json");

    public static boolean exampleBoolean = true;
    public static int exampleInt = 10;
    public static Item exampleItem = Items.DIAMOND;

    public static void loadConfig() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                ConfigManager config = GSON.fromJson(reader, ConfigManager.class);
                exampleBoolean = config.exampleBoolean;
                exampleInt = config.exampleInt;
                exampleItem = config.exampleItem;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveConfig() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(new ConfigManager(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}