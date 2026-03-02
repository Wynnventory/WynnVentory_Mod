package com.wynnventory.gui.screen.settings;

import com.wynnventory.core.config.ModConfig;
import com.wynnventory.core.config.settings.RewardScreenSettings;
import java.util.List;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;

public class RewardSettingsTab implements SettingsTab {
    @Override
    public List<OptionInstance<?>> getOptions() {
        RewardScreenSettings s = ModConfig.getInstance().getRewardScreenSettings();
        return List.of(new OptionInstance<>(
                "gui.wynnventory.settings.reward.maxPoolsPerPage",
                OptionInstance.noTooltip(),
                (label, value) -> Component.literal(label.getString() + ": " + value),
                new OptionInstance.IntRange(1, 10),
                s.getMaxPoolsPerPage(),
                s::setMaxPoolsPerPage));
    }
}
