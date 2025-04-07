package com.wynnventory.input;

import com.wynnventory.model.keymapping.StickyKeyMapping;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class KeyBindingManager {

    public static KeyMapping OPEN_POOLS;
    public static StickyKeyMapping TOGGLE_TOOLTIP;
    public static StickyKeyMapping TOGGLE_BOXED_TOOLTIP;

    public static void register() {
        OPEN_POOLS = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.wynnventory.open_config", GLFW.GLFW_KEY_N, "category.wynnventory.keybinding"
        ));

        TOGGLE_TOOLTIP = (StickyKeyMapping) KeyBindingHelper.registerKeyBinding(
                new StickyKeyMapping("key.wynnventory.toggle_tooltips", GLFW.GLFW_KEY_PERIOD,
                        "category.wynnventory.keybinding", () -> true)
        );

        TOGGLE_BOXED_TOOLTIP = (StickyKeyMapping) KeyBindingHelper.registerKeyBinding(
                new StickyKeyMapping("key.wynnventory.toggle_boxed_item_tooltips", GLFW.GLFW_KEY_COMMA,
                        "category.wynnventory.keybinding", () -> true)
        );
    }
}