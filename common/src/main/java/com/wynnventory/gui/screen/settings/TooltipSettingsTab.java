package com.wynnventory.gui.screen.settings;

import com.mojang.serialization.Codec;
import com.wynnventory.core.config.ModConfig;
import com.wynnventory.core.config.settings.DisplayOptions;
import com.wynnventory.core.config.settings.TooltipSettings;
import java.util.List;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;

public class TooltipSettingsTab implements SettingsTab {
    @Override
    public List<OptionInstance<?>> getOptions() {
        TooltipSettings s = ModConfig.getInstance().getTooltipSettings();
        return List.of(
                OptionInstance.createBoolean(
                        "gui.wynnventory.settings.tooltip.showTooltips", s.isShowTooltips(), s::setShowTooltips),
                OptionInstance.createBoolean(
                        "gui.wynnventory.settings.tooltip.showBoxedItemTooltips",
                        s.isShowBoxedItemTooltips(),
                        s::setShowBoxedItemTooltips),
                OptionInstance.createBoolean(
                        "gui.wynnventory.settings.tooltip.anchorTooltips", s.isAnchorTooltips(), s::setAnchorTooltips),
                OptionInstance.createBoolean(
                        "gui.wynnventory.settings.tooltip.showPriceFluctuation",
                        s.isShowPriceFluctuation(),
                        s::setShowPriceFluctuation),
                new OptionInstance<>(
                        "gui.wynnventory.settings.tooltip.displayFormat",
                        OptionInstance.noTooltip(),
                        (label, value) -> Component.translatable("gui.wynnventory.settings.tooltip.displayFormat."
                                + value.toString().toLowerCase()),
                        new OptionInstance.Enum<>(
                                List.of(DisplayOptions.values()),
                                Codec.INT.xmap(i -> DisplayOptions.values()[i], DisplayOptions::ordinal)),
                        s.getDisplayFormat(),
                        s::setDisplayFormat),
                OptionInstance.createBoolean(
                        "gui.wynnventory.settings.tooltip.showMaxPrice", s.isShowMaxPrice(), s::setShowMaxPrice),
                OptionInstance.createBoolean(
                        "gui.wynnventory.settings.tooltip.showMinPrice", s.isShowMinPrice(), s::setShowMinPrice),
                OptionInstance.createBoolean(
                        "gui.wynnventory.settings.tooltip.showAveragePrice",
                        s.isShowAveragePrice(),
                        s::setShowAveragePrice),
                OptionInstance.createBoolean(
                        "gui.wynnventory.settings.tooltip.showAverage80Price",
                        s.isShowAverage80Price(),
                        s::setShowAverage80Price),
                OptionInstance.createBoolean(
                        "gui.wynnventory.settings.tooltip.showUnidAveragePrice",
                        s.isShowUnidAveragePrice(),
                        s::setShowUnidAveragePrice),
                OptionInstance.createBoolean(
                        "gui.wynnventory.settings.tooltip.showUnidAverage80Price",
                        s.isShowUnidAverage80Price(),
                        s::setShowUnidAverage80Price));
    }
}
