package com.wynnventory.core.event;

import net.neoforged.bus.BusBuilderImpl;
import net.neoforged.bus.EventBus;
import net.neoforged.bus.api.BusBuilder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.Arrays;

public class EventBusWrapper extends EventBus {
    private EventBusWrapper(BusBuilderImpl busBuilder) {
        super(busBuilder);
    }

    public static IEventBus createEventBus() {
        return new EventBusWrapper((BusBuilderImpl) BusBuilder.builder());
    }

    @Override
    public void register(Object target) {
        boolean anyEvents = Arrays.stream(target.getClass().getMethods())
                .anyMatch(method -> method.isAnnotationPresent(SubscribeEvent.class));

        if (!anyEvents) return;

        super.register(target);
    }
}
