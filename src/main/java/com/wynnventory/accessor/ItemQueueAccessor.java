package com.wynnventory.accessor;

import com.wynnventory.model.item.LootpoolItem;
import com.wynnventory.model.item.TradeMarketItem;

import java.util.List;

public interface ItemQueueAccessor {
    List<TradeMarketItem> getQueuedMarketItems();
    List<LootpoolItem> getQueuedLootItems();
}