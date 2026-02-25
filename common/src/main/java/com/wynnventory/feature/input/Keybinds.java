package com.wynnventory.feature.input;

import com.wynnventory.core.config.ModConfig;
import com.wynnventory.gui.screen.RewardScreen;
import org.lwjgl.glfw.GLFW;

public enum Keybinds {
    OPEN_REWARD_POOL("key.wynnventory.openRewardScreen", GLFW.GLFW_KEY_N, false, RewardScreen::open),
    SETTINGS_TOGGLE_TOOLTIPS("key.wynnventory.toggleTooltips", GLFW.GLFW_KEY_PERIOD, true, ModConfig::toggleTooltips),
    SETTINGS_TOGGLE_BOXED_TOOLTIPS(
            "key.wynnventory.toggleBoxedTooltips", GLFW.GLFW_KEY_COMMA, true, ModConfig::toggleBoxedTooltips);

    public static final String ROOT_CATEGORY = "Wynnventory";

    public final String translationKey;
    public final int defaultKey;
    public final boolean allowInInventory;
    public final Runnable callback;

    Keybinds(String translationKey, int defaultKey, boolean allowInInventory, Runnable callback) {
        this.translationKey = translationKey;
        this.defaultKey = defaultKey;
        this.allowInInventory = allowInInventory;
        this.callback = callback;
    }
}
