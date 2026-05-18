package com.wynnventory.gui.screen.settings;

import com.wynnventory.core.config.ModConfig;
import com.wynnventory.core.config.settings.PricePredictionSettings;
import java.util.List;
import net.minecraft.client.OptionInstance;

public class PricePredictionSettingsTab implements SettingsTab {
    @Override
    public List<OptionInstance<?>> getOptions() {
        PricePredictionSettings s = ModConfig.getInstance().getPricePredictionSettings();
        return List.of(
                OptionInstance.createBoolean(
                        "gui.wynnventory.settings.prediction.showPricePrediction",
                        s.isShowPricePrediction(),
                        s::setShowPricePrediction),
                OptionInstance.createBoolean(
                        "gui.wynnventory.settings.prediction.showContributingFactors",
                        s.isShowContributingFactors(),
                        s::setShowContributingFactors));
    }
}
