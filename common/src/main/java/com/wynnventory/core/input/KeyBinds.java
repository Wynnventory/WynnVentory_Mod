package com.wynnventory.core.input;

import com.wynnventory.core.WynnventoryMod;
import com.wynnventory.core.config.ModConfig;
import com.wynnventory.gui.screen.RewardScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public enum KeyBinds {
    OPEN_REWARD_POOL("key.wynnventory.openRewardScreen", GLFW.GLFW_KEY_N, false, RewardScreen::open),
    SETTINGS_TOGGLE_TOOLTIPS("key.wynnventory.toggleTooltips", GLFW.GLFW_KEY_PERIOD, true, ModConfig::toggleTooltips),
    SETTINGS_TOGGLE_BOXED_TOOLTIPS(
            "key.wynnventory.toggleBoxedTooltips", GLFW.GLFW_KEY_COMMA, true, ModConfig::toggleBoxedTooltips);

    public static final KeyMapping.Category ROOT_CATEGORY =
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath(WynnventoryMod.MOD_ID, "root"));

    public final String translationKey;
    public final int defaultKey;
    public final boolean allowInInventory;
    public final Runnable callback;

    KeyBinds(String translationKey, int defaultKey, boolean allowInInventory, Runnable callback) {
        this.translationKey = translationKey;
        this.defaultKey = defaultKey;
        this.allowInInventory = allowInInventory;
        this.callback = callback;
    }
}
