package com.wynnventory.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class InventoryKeyPressEvent extends Event implements ICancellableEvent {
    private final int keyCode;
    private final int scanCode;
    private final int modifiers;

    public InventoryKeyPressEvent(int keyCode, int scanCode, int modifiers) {
        this.keyCode = keyCode;
        this.scanCode = scanCode;
        this.modifiers = modifiers;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public int getScanCode() {
        return scanCode;
    }

    public int getModifiers() {
        return modifiers;
    }
}
