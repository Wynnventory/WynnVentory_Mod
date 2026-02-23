package com.wynnventory.feature.input;

import com.mojang.blaze3d.platform.InputConstants;
import com.wynnventory.events.ClientTickEvent;
import com.wynnventory.events.InventoryKeyPressEvent;
import com.wynnventory.mixin.OptionsAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.neoforged.bus.api.SubscribeEvent;

public final class KeybindFeature {
    private static final EnumMap<Keybinds, KeyMapping> mappings = new EnumMap<>(Keybinds.class);

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen instanceof ChatScreen) return;
        if (mc.screen != null && mc.screen.getFocused() instanceof EditBox) return;

        for (Map.Entry<Keybinds, KeyMapping> entry : KeybindFeature.all()) {
            while (entry.getValue().consumeClick()) {
                handle(entry.getKey());
            }
        }
    }

    @SubscribeEvent
    public void onInventoryKeyPress(InventoryKeyPressEvent event) {
        for (Map.Entry<Keybinds, KeyMapping> entry : KeybindFeature.all()) {
            if (!entry.getKey().allowInInventory) continue;
            if (entry.getValue().matches(event.getKeyEvent())) {
                handle(entry.getKey());
            }
        }
    }

    public static void registerKeybinds(Options options) {
        if (options == null) return;

        OptionsAccessor acc = (OptionsAccessor) options;
        List<KeyMapping> list = new ArrayList<>(Arrays.asList(acc.getKeyMappings()));

        for (Keybinds def : Keybinds.values()) {
            if (containsTranslationKey(list, def.translationKey)) continue;

            KeyMapping mapping = new KeyMapping(
                    def.translationKey, InputConstants.Type.KEYSYM, def.defaultKey, Keybinds.ROOT_CATEGORY);

            list.add(mapping);
            mappings.put(def, mapping);
        }

        acc.setKeyMappings(list.toArray(KeyMapping[]::new));
        KeyMapping.resetMapping();
    }

    private static Collection<Map.Entry<Keybinds, KeyMapping>> all() {
        return mappings.entrySet();
    }

    private static boolean containsTranslationKey(List<KeyMapping> list, String translationKey) {
        for (KeyMapping km : list) {
            if (km != null && translationKey.equals(km.getName())) {
                return true;
            }
        }
        return false;
    }

    private void handle(Keybinds key) {
        if (key.callback != null) {
            key.callback.run();
        }
    }
}
