package com.wynnventory.core.input;

import com.mojang.blaze3d.platform.InputConstants;
import com.wynnventory.mixin.OptionsAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;

public final class KeyBindManager {
    private static final EnumMap<KeyBinds, KeyMapping> mappings = new EnumMap<>(KeyBinds.class);

    private KeyBindManager() {}

    public static void registerKeybinds(Options options) {
        if (options == null) return;

        OptionsAccessor acc = (OptionsAccessor) options;
        List<KeyMapping> list = new ArrayList<>(Arrays.asList(acc.getKeyMappings()));

        for (KeyBinds def : KeyBinds.values()) {
            if (containsTranslationKey(list, def.translationKey)) continue;

            KeyMapping mapping = new KeyMapping(
                    def.translationKey, InputConstants.Type.KEYSYM, def.defaultKey, KeyBinds.ROOT_CATEGORY);

            list.add(mapping);
            mappings.put(def, mapping);
        }

        acc.setKeyMappings(list.toArray(KeyMapping[]::new));
        KeyMapping.resetMapping();
    }

    public static KeyMapping get(KeyBinds bind) {
        return mappings.get(bind);
    }

    public static Collection<Map.Entry<KeyBinds, KeyMapping>> all() {
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
}
