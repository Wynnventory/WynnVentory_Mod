package com.wynnventory.feature;

import com.wynnventory.event.LootrunPreviewOpenedEvent;
import com.wynnventory.model.reward.RewardScreen;
import net.neoforged.bus.api.SubscribeEvent;

public class LootRewardHandler {

    @SubscribeEvent
    public void onHandleContainerContent(LootrunPreviewOpenedEvent event) {
        System.out.println("THIS IS: " + RewardScreen.fromTitle(event.getScreenTitle()));
    }
}
