package com.wynnventory.util;

import com.wynntils.core.text.StyledText;
import com.wynntils.models.items.WynnItem;
import com.wynntils.models.items.WynnItemData;
import com.wynnventory.WynnventoryMod;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Field;

public class ItemStackUtils {

    public static WynnItem getWynntilsAnnotation(ItemStack itemStack) {
        try {
            Field wynntilsAnnotation = ItemStack.class.getDeclaredField("wynntilsAnnotation");
            wynntilsAnnotation.setAccessible(true);

            return (WynnItem) wynntilsAnnotation.get(itemStack);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            WynnventoryMod.error("Error trying to get wynntilsAnnotation.", e);
            return null;
        }
    }

    public static StyledText getWynntilsOriginalName(ItemStack itemStack) {
        try {
            Field wynntilsOriginalName = ItemStack.class.getDeclaredField("wynntilsOriginalName");
            wynntilsOriginalName.setAccessible(true);

            return (StyledText) wynntilsOriginalName.get(itemStack);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            WynnventoryMod.error("Error trying to get wynntilsOriginalName.", e);
            return null;
        }
    }

    public static WynnItem getWynnItem(ItemStack itemStack) {
        WynnItem wynnItem = getWynntilsAnnotation(itemStack);
        assert wynnItem != null;
        ItemStack item = wynnItem.getData().get(WynnItemData.ITEMSTACK_KEY);
        return (WynnItem) ItemStackUtils.getWynntilsAnnotation(item);
    }
}