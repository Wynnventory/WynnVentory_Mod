package com.wynnventory.gui.screen.settings;

import com.wynnventory.core.config.ModConfig;
import com.wynnventory.core.config.settings.RaritySettings;
import com.wynnventory.gui.screen.SettingsScreen;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;

public class RaritySettingsTab implements SettingsTab {
    @Override
    public void init(SettingsScreen screen, int x1, int x2, int y, int w, int h) {
        RaritySettings s = ModConfig.getInstance().getRaritySettings();
        screen.addPublic(CycleButton.onOffBuilder(s.isShowMythic())
                .create(x1, y, w, 20, Component.translatable("gui.wynnventory.settings.rarity.showMythic"), (btn, val) -> s.setShowMythic(val)));
        screen.addPublic(CycleButton.onOffBuilder(s.isShowFabled())
                .create(x2, y, w, 20, Component.translatable("gui.wynnventory.settings.rarity.showFabled"), (btn, val) -> s.setShowFabled(val)));

        y += h;
        screen.addPublic(CycleButton.onOffBuilder(s.isShowLegendary())
                .create(x1, y, w, 20, Component.translatable("gui.wynnventory.settings.rarity.showLegendary"), (btn, val) -> s.setShowLegendary(val)));
        screen.addPublic(CycleButton.onOffBuilder(s.isShowRare())
                .create(x2, y, w, 20, Component.translatable("gui.wynnventory.settings.rarity.showRare"), (btn, val) -> s.setShowRare(val)));

        y += h;
        screen.addPublic(CycleButton.onOffBuilder(s.isShowUnique())
                .create(x1, y, w, 20, Component.translatable("gui.wynnventory.settings.rarity.showUnique"), (btn, val) -> s.setShowUnique(val)));
        screen.addPublic(CycleButton.onOffBuilder(s.isShowCommon())
                .create(x2, y, w, 20, Component.translatable("gui.wynnventory.settings.rarity.showCommon"), (btn, val) -> s.setShowCommon(val)));

        y += h;
        screen.addPublic(CycleButton.onOffBuilder(s.isShowSet())
                .create(x1, y, w, 20, Component.translatable("gui.wynnventory.settings.rarity.showSet"), (btn, val) -> s.setShowSet(val)));
        screen.addPublic(CycleButton.onOffBuilder(s.isShowUnusable())
                .create(x2, y, w, 20, Component.translatable("gui.wynnventory.settings.rarity.showUnusable"), (btn, val) -> s.setShowUnusable(val)));
    }
}
