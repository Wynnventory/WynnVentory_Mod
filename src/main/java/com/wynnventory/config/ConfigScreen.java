package com.wynnventory.config;

import com.wynnventory.api.WynnventoryScheduler;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfigScreen {

    public static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("title.wynnventory.config"));

        // Categories
        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("category.wynnventory.general"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // Entries
        general.addEntry(entryBuilder.startIntSlider(Component.translatable("option.wynnventory.api_delay.get"), ConfigManager.FETCH_DELAY_MINS, ConfigManager.MIN_FETCH_DELAY_MINS, ConfigManager.MAX_FETCH_DELAY_MINS)
                .setDefaultValue(ConfigManager.DEFAULT_FETCH_DELAY_MINS)
//                .setTooltip(Component.translatable("option.wynnventory.api_delay.tooltip"))
                .setSaveConsumer(newValue -> ConfigManager.FETCH_DELAY_MINS = newValue)
                .build());

        general.addEntry(entryBuilder.startIntSlider(Component.translatable("option.wynnventory.api_delay.post"), ConfigManager.SEND_DELAY_MINS, ConfigManager.MIN_SEND_DELAY_MINS, ConfigManager.MAX_SEND_DELAY_MINS)
                .setDefaultValue(ConfigManager.DEFAULT_SEND_DELAY_MINS)
//                .setTooltip(Component.translatable("option.wynnventory.api_delay.tooltip"))
                .setSaveConsumer(newValue -> ConfigManager.SEND_DELAY_MINS = newValue)
                .build());

        builder.setSavingRunnable(ConfigManager::saveConfig);

        return builder.build();
    }
}
