package com.wynnventory.feature;

import com.wynnventory.event.LootrunPreviewOpenedEvent;
import com.wynnventory.model.reward.RewardPool;
import com.wynnventory.queue.QueueManager;
import net.neoforged.bus.api.SubscribeEvent;

public class LootRewardHandler {

    @SubscribeEvent
    public void onHandleContainerContent(LootrunPreviewOpenedEvent event) {
        RewardPool pool = RewardPool.fromTitle(event.getScreenTitle());
        QueueManager.lootrun().addItems(pool, event.getItems());
    }
}
