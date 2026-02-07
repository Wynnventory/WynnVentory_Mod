package com.wynnventory.gui.screen.settings;

import com.wynnventory.core.config.ModConfig;
import com.wynnventory.core.config.settings.RaritySettings;
import net.minecraft.client.OptionInstance;

import java.util.List;

public class RaritySettingsTab implements SettingsTab {
    @Override
    public List<OptionInstance<?>> getOptions() {
        RaritySettings s = ModConfig.getInstance().getRaritySettings();
        return List.of(
                OptionInstance.createBoolean("gui.wynnventory.settings.rarity.showMythic", s.isShowMythic(), s::setShowMythic),
                OptionInstance.createBoolean("gui.wynnventory.settings.rarity.showFabled", s.isShowFabled(), s::setShowFabled),
                OptionInstance.createBoolean("gui.wynnventory.settings.rarity.showLegendary", s.isShowLegendary(), s::setShowLegendary),
                OptionInstance.createBoolean("gui.wynnventory.settings.rarity.showRare", s.isShowRare(), s::setShowRare),
                OptionInstance.createBoolean("gui.wynnventory.settings.rarity.showUnique", s.isShowUnique(), s::setShowUnique),
                OptionInstance.createBoolean("gui.wynnventory.settings.rarity.showCommon", s.isShowCommon(), s::setShowCommon),
                OptionInstance.createBoolean("gui.wynnventory.settings.rarity.showSet", s.isShowSet(), s::setShowSet),
                OptionInstance.createBoolean("gui.wynnventory.settings.rarity.showUnusable", s.isShowUnusable(), s::setShowUnusable)
        );
    }
}
