package com.wynnventory.gui.screen.settings;

import com.wynnventory.core.config.ModConfig;
import com.wynnventory.core.config.settings.RewardScreenSettings;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;

import java.util.List;

public class RewardScreenSettingsTab implements SettingsTab {
    @Override
    public List<OptionInstance<?>> getOptions() {
        RewardScreenSettings s = ModConfig.getInstance().getRewardScreenSettings();
        return List.of(
                new OptionInstance<>(
                        "gui.wynnventory.settings.rewardScreen.lootrunColumns",
                        OptionInstance.noTooltip(),
                        (label, value) -> Component.literal(label.getString() + ": " + value),
                        new OptionInstance.IntRange(1, 5),
                        s.getLootrunColumns(),
                        s::setLootrunColumns
                ),
                new OptionInstance<>(
                        "gui.wynnventory.settings.rewardScreen.raidColumns",
                        OptionInstance.noTooltip(),
                        (label, value) -> Component.literal(label.getString() + ": " + value),
                        new OptionInstance.IntRange(1, 4),
                        s.getRaidColumns(),
                        s::setRaidColumns
                )
        );
    }
}
