package com.wynnventory.events;

import net.minecraft.client.input.KeyEvent;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class InventoryKeyPressEvent extends Event implements ICancellableEvent {
    private final KeyEvent keyEvent;

    public InventoryKeyPressEvent(KeyEvent keyEvent) {
        this.keyEvent = keyEvent;
    }

    public KeyEvent getKeyEvent() {
        return keyEvent;
    }
}
