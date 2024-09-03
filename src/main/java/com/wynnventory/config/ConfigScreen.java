package com.wynnventory.config;

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
        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.wynnventory.tooltip.showMaxPrice"), configManager.isShowMaxPrice())
                .setDefaultValue(true)
                .setSaveConsumer(configManager::setShowMaxPrice)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.wynnventory.tooltip.showMinPrice"), configManager.isShowMinPrice())
                .setDefaultValue(true)
                .setSaveConsumer(configManager::setShowMinPrice)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.wynnventory.tooltip.showAvgPrice"), configManager.isShowAveragePrice())
                .setDefaultValue(true)
                .setSaveConsumer(configManager::setShowAveragePrice)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.wynnventory.tooltip.showAvg80Price"), configManager.isShowAverage80Price())
                .setDefaultValue(true)
                .setSaveConsumer(configManager::setShowAverage80Price)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.wynnventory.tooltip.showUnidAvgPrice"), configManager.isShowUnidAveragePrice())
                .setDefaultValue(true)
                .setSaveConsumer(configManager::setShowUnidAveragePrice)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.wynnventory.tooltip.showUnidAvg80Price"), configManager.isShowUnidAverage80Price())
                .setDefaultValue(true)
                .setSaveConsumer(configManager::setShowUnidAverage80Price)
                .build());

        builder.setSavingRunnable(ConfigManager.getInstance()::saveConfig);

        return builder.build();
    }
}
