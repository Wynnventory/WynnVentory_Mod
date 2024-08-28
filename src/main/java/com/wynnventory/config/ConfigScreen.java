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
        general.addEntry(entryBuilder.startIntSlider(Component.translatable("option.wynnventory.api_delay.get"), ConfigManager.FETCH_CONFIG.getUserSetting(), ConfigManager.FETCH_CONFIG.getMinDelay(), ConfigManager.FETCH_CONFIG.getMaxDelay())
                .setDefaultValue(ConfigManager.FETCH_CONFIG.getDefaultDelay())
//                .setTooltip(Component.translatable("option.wynnventory.api_delay.tooltip"))
                .setSaveConsumer(newValue -> ConfigManager.WYNNVENTORY_CONFIG.setFetchDelayMins(newValue))
                .build());

        general.addEntry(entryBuilder.startIntSlider(Component.translatable("option.wynnventory.api_delay.post"), ConfigManager.SEND_CONFIG.getUserSetting(), ConfigManager.SEND_CONFIG.getMinDelay(), ConfigManager.SEND_CONFIG.getMaxDelay())
                .setDefaultValue(ConfigManager.SEND_CONFIG.getDefaultDelay())
//                .setTooltip(Component.translatable("option.wynnventory.api_delay.tooltip"))
                .setSaveConsumer(newValue -> ConfigManager.WYNNVENTORY_CONFIG.setSendDelayMins(newValue))
                .build());

        builder.setSavingRunnable(ConfigManager.WYNNVENTORY_CONFIG::saveConfig);

        return builder.build();
    }
}
