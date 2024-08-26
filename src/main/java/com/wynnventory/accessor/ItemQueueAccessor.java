package com.wynnventory.accessor;

import net.minecraft.world.item.ItemStack;
import java.util.List;

public interface ItemQueueAccessor {
    List<ItemStack> getQueuedMarketItems();
    List<ItemStack> getQueuedLootItems();
    void clearBuffers();
}