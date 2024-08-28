package com.wynnventory.util;

import com.wynnventory.WynnventoryMod;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;

import java.lang.reflect.Field;

public class KeyMappingUtil {

    public static InputConstants.Key getBoundKey(KeyMapping keyMapping) {
        try {
            Field keyField = KeyMapping.class.getDeclaredField("key");
            keyField.setAccessible(true);
            return (InputConstants.Key) keyField.get(keyMapping);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            WynnventoryMod.error("Could not get bound key for: " + keyMapping.getName());
            return null;
        }
    }
}