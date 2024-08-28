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

        ConfigManager configManager = ConfigManager.getInstance();
        // Entries
        general.addEntry(entryBuilder.startIntSlider(Component.translatable("option.wynnventory.api_delay.get"), configManager.getFetchUserSetting(), configManager.getFetchMinDelay(), configManager.getFetchMaxDelay())
                .setDefaultValue(configManager.getFetchDefaultDelay())
//                .setTooltip(Component.translatable("option.wynnventory.api_delay.tooltip"))
                .setSaveConsumer(newValue -> configManager.setFetchUserSetting(newValue))
                .build());

        general.addEntry(entryBuilder.startIntSlider(Component.translatable("option.wynnventory.api_delay.post"), configManager.getSendUserSetting(), configManager.getSendMinDelay(), configManager.getSendMaxDelay())
                .setDefaultValue(configManager.getSendDefaultDelay())
//                .setTooltip(Component.translatable("option.wynnventory.api_delay.tooltip"))
                .setSaveConsumer(newValue -> configManager.setSendUserSetting(newValue))
                .build());

        builder.setSavingRunnable(ConfigManager.getInstance()::saveConfig);

        return builder.build();
    }
}
