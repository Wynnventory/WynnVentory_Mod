package com.wynnventory.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfigScreen {

    public static Screen createConfigScreen(Screen parent) {
        ConfigManager configManager = ConfigManager.getInstance();

        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(Component.translatable("title.wynnventory.config"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // Categories
        ConfigCategory generalCategory = builder.getOrCreateCategory(Component.translatable("category.wynnventory.general"));
        ConfigCategory tooltipsCategory = builder.getOrCreateCategory(Component.translatable("category.wynnventory.tooltips"));

        // General config
        generalCategory.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.wynnventory.general.toggle_tooltips"), configManager.isShowTooltips())
                .setDefaultValue(true)
                .setSaveConsumer(configManager::setShowTooltips)
                .build());

        generalCategory.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.wynnventory.general.toggle_boxed_item_tooltips"), configManager.isShowBoxedItemTooltips())
                .setDefaultValue(true)
                .setSaveConsumer(configManager::setShowBoxedItemTooltips)
                .build());

        generalCategory.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.wynnventory.general.toggle_price_fluctuation"), configManager.isShowPriceFluctuation())
                .setDefaultValue(true)
                .setSaveConsumer(configManager::setShowPriceFluctuation)
                .build());

        generalCategory.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.wynnventory.general.anchor_tooltips"), configManager.isAnchorTooltips())
                .setDefaultValue(true)
                .setSaveConsumer(configManager::setAnchorTooltips)
                .build());

        // Tooltip config
        tooltipsCategory.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.wynnventory.tooltip.showMaxPrice"), configManager.isShowMaxPrice())
                .setDefaultValue(true)
                .setSaveConsumer(configManager::setShowMaxPrice)
                .build());

        tooltipsCategory.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.wynnventory.tooltip.showMinPrice"), configManager.isShowMinPrice())
                .setDefaultValue(true)
                .setSaveConsumer(configManager::setShowMinPrice)
                .build());

        tooltipsCategory.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.wynnventory.tooltip.showAvgPrice"), configManager.isShowAveragePrice())
                .setDefaultValue(true)
                .setSaveConsumer(configManager::setShowAveragePrice)
                .build());

        tooltipsCategory.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.wynnventory.tooltip.showAvg80Price"), configManager.isShowAverage80Price())
                .setDefaultValue(true)
                .setSaveConsumer(configManager::setShowAverage80Price)
                .build());

        tooltipsCategory.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.wynnventory.tooltip.showUnidAvgPrice"), configManager.isShowUnidAveragePrice())
                .setDefaultValue(true)
                .setSaveConsumer(configManager::setShowUnidAveragePrice)
                .build());

        tooltipsCategory.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.wynnventory.tooltip.showUnidAvg80Price"), configManager.isShowUnidAverage80Price())
                .setDefaultValue(true)
                .setSaveConsumer(configManager::setShowUnidAverage80Price)
                .build());

        builder.setSavingRunnable(ConfigManager.getInstance()::saveConfig);

        return builder.build();
    }
}
