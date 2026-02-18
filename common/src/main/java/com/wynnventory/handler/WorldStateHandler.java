package com.wynnventory.handler;

import com.wynntils.models.worlds.event.WorldStateEvent;
import com.wynntils.models.worlds.type.WorldState;
import com.wynnventory.core.feature.FavouriteNotifier;
import com.wynnventory.core.feature.ModUpdater;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class WorldStateHandler {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onWorldStateChange(WorldStateEvent e) {
        if (e.isFirstJoinWorld() || e.getNewState() == WorldState.WORLD) {
            if (e.isFirstJoinWorld()) ModUpdater.checkForUpdates();
            FavouriteNotifier.checkFavourites();
        }
    }
}
