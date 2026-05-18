package com.wynnventory.gui.screen.settings;

import com.wynnventory.core.config.ModConfig;
import com.wynnventory.core.config.settings.PricePredictionSettings;
import java.util.List;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;

public class PricePredictionSettingsTab implements SettingsTab {
    @Override
    public List<OptionInstance<?>> getOptions() {
        PricePredictionSettings s = ModConfig.getInstance().getPricePredictionSettings();
        return List.of(
                OptionInstance.createBoolean(
                        "gui.wynnventory.settings.prediction.showPricePrediction",
                        OptionInstance.cachedConstantTooltip(Component.translatable(
                                "gui.wynnventory.settings.prediction.showPricePrediction.tooltip")),
                        s.isShowPricePrediction(),
                        s::setShowPricePrediction),
                OptionInstance.createBoolean(
                        "gui.wynnventory.settings.prediction.showContributingFactors",
                        OptionInstance.cachedConstantTooltip(Component.translatable(
                                "gui.wynnventory.settings.prediction.showContributingFactors.tooltip")),
                        s.isShowContributingFactors(),
                        s::setShowContributingFactors));
    }
}
