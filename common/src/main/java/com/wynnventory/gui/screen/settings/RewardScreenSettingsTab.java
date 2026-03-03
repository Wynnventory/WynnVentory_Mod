package com.wynnventory.gui.screen.settings;

import com.mojang.serialization.Codec;
import com.wynnventory.core.config.ModConfig;
import com.wynnventory.core.config.settings.RewardLayoutMode;
import com.wynnventory.core.config.settings.RewardScreenSettings;
import java.util.List;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;

public class RewardScreenSettingsTab implements SettingsTab {
    @Override
    public List<OptionInstance<?>> getOptions() {
        RewardScreenSettings s = ModConfig.getInstance().getRewardScreenSettings();
        return List.of(
                new OptionInstance<>(
                        "gui.wynnventory.settings.rewardscreen.layoutMode",
                        OptionInstance.noTooltip(),
                        (label, value) -> Component.translatable("gui.wynnventory.settings.rewardscreen.layoutMode."
                                + value.toString().toLowerCase()),
                        new OptionInstance.Enum<>(
                                List.of(RewardLayoutMode.values()),
                                Codec.INT.xmap(i -> RewardLayoutMode.values()[i], RewardLayoutMode::ordinal)),
                        s.getLayoutMode(),
                        s::setLayoutMode),
                new OptionInstance<>(
                        "gui.wynnventory.settings.rewardscreen.maxPoolsPerPage",
                        OptionInstance.noTooltip(),
                        (label, value) -> Component.literal(label.getString() + ": " + value),
                        new OptionInstance.IntRange(1, 10),
                        s.getMaxPoolsPerPage(),
                        s::setMaxPoolsPerPage));
    }
}
