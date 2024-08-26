package com.wynnventory.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfigScreen {
    private static String prevValue = "";

    public static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("title.wynnventory.config"));

        // General Category
        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("category.wynnventory.general"));
        ConfigCategory general2 = builder.getOrCreateCategory(Component.translatable("category.wynnventory.general2"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
//        general.addEntry(entryBuilder.startStrField(Component.translatable("option.examplemod.optionA"), currentValue)
        general.addEntry(entryBuilder.startStrField(Component.translatable("option.wynnventory.optionA"), prevValue)
                .setDefaultValue("This is the default value") // Recommended: Used when user click "Reset"
                .setTooltip(Component.translatable("This option is awesome!")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> prevValue = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config

        builder.setSavingRunnable(ConfigManager::saveConfig); // Save the config when the screen is closed

        return builder.build();
    }
}
