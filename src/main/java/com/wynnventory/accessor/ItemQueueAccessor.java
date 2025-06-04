package com.wynnventory.accessor;

import com.wynntils.models.items.items.gui.GambitItem;
import com.wynnventory.enums.Region;
import com.wynnventory.model.item.Lootpool;
import com.wynnventory.model.item.SimplifiedCraftingItem;
import com.wynnventory.model.item.SimplifiedGambitItem;
import com.wynnventory.model.item.TradeMarketItem;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;

public interface ItemQueueAccessor {
    List<TradeMarketItem> getQueuedMarketItems();
    List<SimplifiedGambitItem> getQueuedGambitItems();
    Map<String, Lootpool> getQueuedLootpools();
    Map<String, Lootpool> getQueuedRaidpools();
    void addItemToTrademarketQueue(ItemStack stack);
    void addItemToGambitQueue(GambitItem gambitItem);
    void addItemsToLootpoolQueue(Region region, List<ItemStack> items);
}