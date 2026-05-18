package com.wynnventory.gui.screen.settings;

import com.mojang.serialization.Codec;
import com.wynnventory.core.config.ModConfig;
import com.wynnventory.core.config.settings.DisplayOptions;
import com.wynnventory.core.config.settings.PriceDetailLevel;
import com.wynnventory.core.config.settings.TooltipSettings;
import com.wynnventory.gui.screen.SettingsScreen;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.network.chat.Component;

public class TooltipSettingsTab implements SettingsTab {
    private SettingsScreen screen;

    @Override
    public List<OptionInstance<?>> getOptions() {
        return List.of();
    }

    @Override
    public void initCustomWidgets(SettingsScreen screen, OptionsList list) {
        this.screen = screen;
    }

    private void refresh() {
        if (screen != null) {
            Minecraft.getInstance().setScreen(new SettingsScreen(screen.getParent()));
        }
    }

    @Override
    public void addOptions(OptionsList list) {
        TooltipSettings s = ModConfig.getInstance().getTooltipSettings();

        // --- Display Settings ---
        list.addHeader(Component.translatable("gui.wynnventory.settings.tooltip.header.display"));
        list.addSmall(
                OptionInstance.createBoolean(
                        "gui.wynnventory.settings.tooltip.showTooltips",
                        OptionInstance.cachedConstantTooltip(
                                Component.translatable("gui.wynnventory.settings.tooltip.showTooltips.tooltip")),
                        s.isShowTooltips(),
                        s::setShowTooltips),
                new OptionInstance<>(
                        "gui.wynnventory.settings.tooltip.displayFormat",
                        OptionInstance.cachedConstantTooltip(
                                Component.translatable("gui.wynnventory.settings.tooltip.displayFormat.tooltip")),
                        (label, value) -> Component.translatable("gui.wynnventory.settings.tooltip.displayFormat."
                                + value.toString().toLowerCase()),
                        new OptionInstance.Enum<>(
                                List.of(DisplayOptions.values()),
                                Codec.INT.xmap(i -> DisplayOptions.values()[i], DisplayOptions::ordinal)),
                        s.getDisplayFormat(),
                        s::setDisplayFormat));
        list.addSmall(
                OptionInstance.createBoolean(
                        "gui.wynnventory.settings.tooltip.showBoxedItemTooltips",
                        OptionInstance.cachedConstantTooltip(Component.translatable(
                                "gui.wynnventory.settings.tooltip.showBoxedItemTooltips.tooltip")),
                        s.isShowBoxedItemTooltips(),
                        s::setShowBoxedItemTooltips),
                OptionInstance.createBoolean(
                        "gui.wynnventory.settings.tooltip.anchorTooltips",
                        OptionInstance.cachedConstantTooltip(
                                Component.translatable("gui.wynnventory.settings.tooltip.anchorTooltips.tooltip")),
                        s.isAnchorTooltips(),
                        s::setAnchorTooltips));

        // --- Price Data ---
        list.addHeader(Component.translatable("gui.wynnventory.settings.tooltip.header.price"));

        list.addSmall(
                new OptionInstance<>(
                        "gui.wynnventory.settings.tooltip.priceDetailLevel",
                        OptionInstance.cachedConstantTooltip(
                                Component.translatable("gui.wynnventory.settings.tooltip.priceDetailLevel.tooltip")),
                        (label, value) -> Component.translatable("gui.wynnventory.settings.tooltip.priceDetailLevel."
                                + value.toString().toLowerCase()),
                        new OptionInstance.Enum<>(
                                List.of(PriceDetailLevel.values()),
                                Codec.INT.xmap(i -> PriceDetailLevel.values()[i], PriceDetailLevel::ordinal)),
                        s.getPriceDetailLevel(),
                        value -> {
                            s.setPriceDetailLevel(value);
                            refresh();
                        }),
                OptionInstance.createBoolean(
                        "gui.wynnventory.settings.tooltip.showPriceFluctuation",
                        OptionInstance.cachedConstantTooltip(Component.translatable(
                                "gui.wynnventory.settings.tooltip.showPriceFluctuation.tooltip")),
                        s.isShowPriceFluctuation(),
                        s::setShowPriceFluctuation));

        if (s.getPriceDetailLevel() == PriceDetailLevel.CUSTOM) {
            list.addSmall(
                    OptionInstance.createBoolean(
                            "gui.wynnventory.settings.tooltip.separateUnidSettings",
                            OptionInstance.cachedConstantTooltip(Component.translatable(
                                    "gui.wynnventory.settings.tooltip.separateUnidSettings.tooltip")),
                            s.isSeparateUnidSettings(),
                            value -> {
                                s.setSeparateUnidSettings(value);
                                refresh();
                            }),
                    null);

            List<OptionInstance<?>> customOptions = new ArrayList<>();
            customOptions.add(OptionInstance.createBoolean(
                    "gui.wynnventory.settings.tooltip.showMinPrice",
                    OptionInstance.cachedConstantTooltip(
                            Component.translatable("gui.wynnventory.settings.tooltip.showMinPrice.tooltip")),
                    s.isShowMinPrice(),
                    s::setShowMinPrice));
            customOptions.add(OptionInstance.createBoolean(
                    "gui.wynnventory.settings.tooltip.showMovingMedian",
                    OptionInstance.cachedConstantTooltip(
                            Component.translatable("gui.wynnventory.settings.tooltip.showMovingMedian.tooltip")),
                    s.isShowMovingMedian(),
                    s::setShowMovingMedian));
            customOptions.add(OptionInstance.createBoolean(
                    "gui.wynnventory.settings.tooltip.showAverage80Price",
                    OptionInstance.cachedConstantTooltip(
                            Component.translatable("gui.wynnventory.settings.tooltip.showAverage80Price.tooltip")),
                    s.isShowAverage80Price(),
                    s::setShowAverage80Price));
            customOptions.add(OptionInstance.createBoolean(
                    "gui.wynnventory.settings.tooltip.showMedian",
                    OptionInstance.cachedConstantTooltip(
                            Component.translatable("gui.wynnventory.settings.tooltip.showMedian.tooltip")),
                    s.isShowMedian(),
                    s::setShowMedian));
            customOptions.add(OptionInstance.createBoolean(
                    "gui.wynnventory.settings.tooltip.showAveragePrice",
                    OptionInstance.cachedConstantTooltip(
                            Component.translatable("gui.wynnventory.settings.tooltip.showAveragePrice.tooltip")),
                    s.isShowAveragePrice(),
                    s::setShowAveragePrice));
            customOptions.add(OptionInstance.createBoolean(
                    "gui.wynnventory.settings.tooltip.showMaxPrice",
                    OptionInstance.cachedConstantTooltip(
                            Component.translatable("gui.wynnventory.settings.tooltip.showMaxPrice.tooltip")),
                    s.isShowMaxPrice(),
                    s::setShowMaxPrice));

            for (int i = 0; i < customOptions.size(); i += 2) {
                OptionInstance<?> opt1 = customOptions.get(i);
                OptionInstance<?> opt2 = (i + 1 < customOptions.size()) ? customOptions.get(i + 1) : null;
                list.addSmall(opt1, opt2);
            }

            if (s.isSeparateUnidSettings()) {
                list.addHeader(Component.translatable("gui.wynnventory.settings.tooltip.header.unid_price"));
                List<OptionInstance<?>> unidOptions = new ArrayList<>();
                unidOptions.add(OptionInstance.createBoolean(
                        "gui.wynnventory.settings.tooltip.showUnidMinPrice",
                        OptionInstance.cachedConstantTooltip(
                                Component.translatable("gui.wynnventory.settings.tooltip.showUnidMinPrice.tooltip")),
                        s.isShowUnidentifiedMinPrice(),
                        s::setShowUnidentifiedMinPrice));
                unidOptions.add(OptionInstance.createBoolean(
                        "gui.wynnventory.settings.tooltip.showUnidMovingMedian",
                        OptionInstance.cachedConstantTooltip(Component.translatable(
                                "gui.wynnventory.settings.tooltip.showUnidMovingMedian.tooltip")),
                        s.isShowUnidMovingMedian(),
                        s::setShowUnidMovingMedian));
                unidOptions.add(OptionInstance.createBoolean(
                        "gui.wynnventory.settings.tooltip.showUnidAverage80Price",
                        OptionInstance.cachedConstantTooltip(Component.translatable(
                                "gui.wynnventory.settings.tooltip.showUnidAverage80Price.tooltip")),
                        s.isShowUnidAverage80Price(),
                        s::setShowUnidAverage80Price));
                unidOptions.add(OptionInstance.createBoolean(
                        "gui.wynnventory.settings.tooltip.showUnidMedian",
                        OptionInstance.cachedConstantTooltip(
                                Component.translatable("gui.wynnventory.settings.tooltip.showUnidMedian.tooltip")),
                        s.isShowUnidMedian(),
                        s::setShowUnidMedian));
                unidOptions.add(OptionInstance.createBoolean(
                        "gui.wynnventory.settings.tooltip.showUnidAveragePrice",
                        OptionInstance.cachedConstantTooltip(Component.translatable(
                                "gui.wynnventory.settings.tooltip.showUnidAveragePrice.tooltip")),
                        s.isShowUnidAveragePrice(),
                        s::setShowUnidAveragePrice));
                unidOptions.add(OptionInstance.createBoolean(
                        "gui.wynnventory.settings.tooltip.showUnidMaxPrice",
                        OptionInstance.cachedConstantTooltip(
                                Component.translatable("gui.wynnventory.settings.tooltip.showUnidMaxPrice.tooltip")),
                        s.isShowUnidentifiedMaxPrice(),
                        s::setShowUnidentifiedMaxPrice));

                for (int i = 0; i < unidOptions.size(); i += 2) {
                    OptionInstance<?> opt1 = unidOptions.get(i);
                    OptionInstance<?> opt2 = (i + 1 < unidOptions.size()) ? unidOptions.get(i + 1) : null;
                    list.addSmall(opt1, opt2);
                }
            }
        }
    }
}
