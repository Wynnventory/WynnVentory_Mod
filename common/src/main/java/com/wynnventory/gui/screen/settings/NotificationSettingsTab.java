package com.wynnventory.gui.screen.settings;

import com.wynnventory.core.config.ModConfig;
import com.wynnventory.core.config.settings.FavouriteNotifierSettings;
import java.util.List;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;

public class NotificationSettingsTab implements SettingsTab {
    @Override
    public List<OptionInstance<?>> getOptions() {
        FavouriteNotifierSettings s = ModConfig.getInstance().getFavouriteNotifierSettings();
        return List.of(
                OptionInstance.createBoolean(
                        "gui.wynnventory.settings.notifications.enableNotifier",
                        s.isEnableNotifier(),
                        s::setEnableNotifier),
                OptionInstance.createBoolean(
                        "gui.wynnventory.settings.notifications.mythicsOnly", s.isMythicsOnly(), s::setMythicsOnly),
                new OptionInstance<>(
                        "gui.wynnventory.settings.notifications.maxToasts",
                        OptionInstance.noTooltip(),
                        (label, value) -> Component.literal(label.getString() + ": " + value),
                        new OptionInstance.IntRange(1, 20),
                        s.getMaxToasts(),
                        s::setMaxToasts));
    }
}
