package com.wynnventory.core.input;

import com.wynnventory.core.WynnventoryMod;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public enum KeyBinds {
    OPEN_REWARD_POOL("key.wynnventory.openRewardScreen", GLFW.GLFW_KEY_N, false),
    SETTINGS_TOGGLE_TOOLTIPS("key.wynnventory.settings.toggleTooltips", GLFW.GLFW_KEY_PERIOD, true),
    SETTINGS_TOGGLE_BOXED_TOOLTIPS("key.wynnventory.settings.toggleBoxedTooltips", GLFW.GLFW_KEY_COMMA, true);

    public static final KeyMapping.Category ROOT_CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(WynnventoryMod.MOD_ID, "root"));

    public final String translationKey;
    public final int defaultKey;
    public final boolean allowInInventory;

    KeyBinds(String translationKey, int defaultKey, boolean allowInInventory) {
        this.translationKey = translationKey;
        this.defaultKey = defaultKey;
        this.allowInInventory = allowInInventory;
    }
}
