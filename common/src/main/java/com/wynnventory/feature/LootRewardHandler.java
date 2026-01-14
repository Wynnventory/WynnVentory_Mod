package com.wynnventory.feature;

import com.wynnventory.event.LootrunPreviewOpenedEvent;
import com.wynnventory.model.reward.RewardPool;
import com.wynnventory.queue.QueueManager;
import net.neoforged.bus.api.SubscribeEvent;

public class LootRewardHandler {
    private static final int REWARD_CONTAINER_SLOTS = 54; // TODO: Rework to use bounds

    @SubscribeEvent
    public void onHandleContainerContent(LootrunPreviewOpenedEvent event) {
        RewardPool pool = RewardPool.fromTitle(event.getScreenTitle());
        QueueManager.lootrun().addItems(pool, event.getItems().subList(0, REWARD_CONTAINER_SLOTS));
    }
}
