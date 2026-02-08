package com.wynnventory.handler;

import com.wynntils.models.worlds.event.WorldStateEvent;
import com.wynntils.models.worlds.type.WorldState;
import com.wynnventory.core.ModUpdater;
import net.neoforged.bus.api.SubscribeEvent;

public class WorldStateHandler {

    @SubscribeEvent
    public void onWorldStateChange(WorldStateEvent e) {
        if (e.isFirstJoinWorld() || e.getNewState() == WorldState.WORLD) {
            ModUpdater.checkForUpdates();
        }
    }
}
