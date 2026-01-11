package com.wynnventory.core;

import com.wynnventory.LootRewardHandler;
import com.wynnventory.core.event.EventBusWrapper;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.IEventBus;

public final class WynnventoryMod {
    public static final String MOD_ID = "wynnventory";
    private static IEventBus eventBus;

    private WynnventoryMod() {}

    public static void init() {
        WynnventoryMod.eventBus = EventBusWrapper.createEventBus();

        eventBus.register(new LootRewardHandler());
    }

    public static <T extends Event> boolean postEvent(T event) {
        try {
            eventBus.post(event);
            return event instanceof ICancellableEvent cancellableEvent && cancellableEvent.isCanceled();
        } catch (Throwable t) {
            return false;
        }
    }
}
