package com.wynnventory.gui.screen.settings;

import com.wynnventory.core.config.ModConfig;
import com.wynnventory.core.config.settings.FavouriteNotifierSettings;
import com.wynnventory.gui.screen.SettingsScreen;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;

public class NotificationSettingsTab implements SettingsTab {
    @Override
    public void init(SettingsScreen screen, int x1, int x2, int y, int w, int h) {
        FavouriteNotifierSettings s = ModConfig.getInstance().getFavouriteNotifierSettings();
        screen.addPublic(CycleButton.onOffBuilder(s.isEnableNotifier())
                .create(x1, y, w, 20, Component.translatable("gui.wynnventory.settings.notifications.enableNotifier"), (btn, val) -> s.setEnableNotifier(val)));
        screen.addPublic(CycleButton.onOffBuilder(s.isMythicsOnly())
                .create(x2, y, w, 20, Component.translatable("gui.wynnventory.settings.notifications.mythicsOnly"), (btn, val) -> s.setMythicsOnly(val)));
    }
}
