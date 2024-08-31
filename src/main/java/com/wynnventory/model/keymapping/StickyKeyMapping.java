package com.wynnventory.model.keymapping;

import net.minecraft.client.ToggleKeyMapping;

import java.util.function.BooleanSupplier;

public class StickyKeyMapping extends ToggleKeyMapping {
    private boolean previousState = false;

    public StickyKeyMapping(String name, int keyCode, String category, BooleanSupplier needsToggle) {
        super(name, keyCode, category, needsToggle);
    }

    /**
     * This method checks if the state of the key has changed since the last tick.
     *
     * @return true if the state has changed, false otherwise.
     */
    public boolean hasStateChanged() {
        boolean currentState = super.isDown();

        boolean hasChanged = currentState != previousState;

        previousState = currentState;

        return hasChanged;
    }
}
