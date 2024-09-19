package com.wynnventory.accessor;

import com.wynnventory.model.item.Lootpool;
import com.wynnventory.model.item.LootpoolItem;
import com.wynnventory.model.item.TradeMarketItem;

import java.util.List;
import java.util.Map;

public interface ItemQueueAccessor {
    List<TradeMarketItem> getQueuedMarketItems();
    Map<String, Lootpool> getQueuedLootpools();
    Map<String, Lootpool> getQueuedRaidpools();
}