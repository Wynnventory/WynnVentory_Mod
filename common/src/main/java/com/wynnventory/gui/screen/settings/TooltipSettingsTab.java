package com.wynnventory.gui.screen.settings;

import com.wynnventory.core.config.ModConfig;
import com.wynnventory.core.config.settings.DisplayOptions;
import com.wynnventory.core.config.settings.TooltipSettings;
import com.wynnventory.gui.screen.SettingsScreen;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;

public class TooltipSettingsTab implements SettingsTab {
    @Override
    public void init(SettingsScreen screen, int x1, int x2, int y, int w, int h) {
        TooltipSettings s = ModConfig.getInstance().getTooltipSettings();
        screen.addPublic(CycleButton.onOffBuilder(s.isShowTooltips())
                .create(x1, y, w, 20, Component.translatable("gui.wynnventory.settings.tooltip.showTooltips"), (btn, val) -> s.setShowTooltips(val)));
        screen.addPublic(CycleButton.onOffBuilder(s.isShowBoxedItemTooltips())
                .create(x2, y, w, 20, Component.translatable("gui.wynnventory.settings.tooltip.showBoxedItemTooltips"), (btn, val) -> s.setShowBoxedItemTooltips(val)));

        y += h;
        screen.addPublic(CycleButton.onOffBuilder(s.isAnchorTooltips())
                .create(x1, y, w, 20, Component.translatable("gui.wynnventory.settings.tooltip.anchorTooltips"), (btn, val) -> s.setAnchorTooltips(val)));
        screen.addPublic(CycleButton.onOffBuilder(s.isShowPriceFluctuation())
                .create(x2, y, w, 20, Component.translatable("gui.wynnventory.settings.tooltip.showPriceFluctuation"), (btn, val) -> s.setShowPriceFluctuation(val)));

        y += h;
        screen.addPublic(CycleButton.builder((DisplayOptions val) -> Component.translatable("gui.wynnventory.settings.tooltip.displayFormat." + val.name().toLowerCase()), s.getDisplayFormat())
                .withValues(DisplayOptions.values())
                .create(x1, y, w, 20, Component.translatable("gui.wynnventory.settings.tooltip.displayFormat"), (btn, val) -> s.setDisplayFormat(val)));

        y += h * 2;
        screen.addPublic(CycleButton.onOffBuilder(s.isShowMaxPrice())
                .create(x1, y, w, 20, Component.translatable("gui.wynnventory.settings.tooltip.showMaxPrice"), (btn, val) -> s.setShowMaxPrice(val)));
        screen.addPublic(CycleButton.onOffBuilder(s.isShowMinPrice())
                .create(x2, y, w, 20, Component.translatable("gui.wynnventory.settings.tooltip.showMinPrice"), (btn, val) -> s.setShowMinPrice(val)));

        y += h;
        screen.addPublic(CycleButton.onOffBuilder(s.isShowAveragePrice())
                .create(x1, y, w, 20, Component.translatable("gui.wynnventory.settings.tooltip.showAveragePrice"), (btn, val) -> s.setShowAveragePrice(val)));
        screen.addPublic(CycleButton.onOffBuilder(s.isShowAverage80Price())
                .create(x2, y, w, 20, Component.translatable("gui.wynnventory.settings.tooltip.showAverage80Price"), (btn, val) -> s.setShowAverage80Price(val)));

        y += h;
        screen.addPublic(CycleButton.onOffBuilder(s.isShowUnidAveragePrice())
                .create(x1, y, w, 20, Component.translatable("gui.wynnventory.settings.tooltip.showUnidAveragePrice"), (btn, val) -> s.setShowUnidAveragePrice(val)));
        screen.addPublic(CycleButton.onOffBuilder(s.isShowUnidAverage80Price())
                .create(x2, y, w, 20, Component.translatable("gui.wynnventory.settings.tooltip.showUnidAverage80Price"), (btn, val) -> s.setShowUnidAverage80Price(val)));
    }
}
