package com.wynnventory.gui.screen.settings;

import com.wynnventory.core.config.ModConfig;
import com.wynnventory.core.config.settings.FavouriteNotifierSettings;
import net.minecraft.client.OptionInstance;

import java.util.List;

public class NotificationSettingsTab implements SettingsTab {
    @Override
    public List<OptionInstance<?>> getOptions() {
        FavouriteNotifierSettings s = ModConfig.getInstance().getFavouriteNotifierSettings();
        return List.of(
                OptionInstance.createBoolean("gui.wynnventory.settings.notifications.enableNotifier", s.isEnableNotifier(), s::setEnableNotifier),
                OptionInstance.createBoolean("gui.wynnventory.settings.notifications.mythicsOnly", s.isMythicsOnly(), s::setMythicsOnly)
        );
    }
}
