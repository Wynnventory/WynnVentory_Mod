package com.wynnventory.events;

import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

import java.util.List;

public class RaidLobbyPopulatedEvent extends Event {
    private final List<ItemStack> items;
    private final int containerId;
    private final String screenTitle;

    public RaidLobbyPopulatedEvent(List<ItemStack> items, int containerId, String screenTitle) {
        this.items = items;
        this.containerId = containerId;
        this.screenTitle = screenTitle;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public int getContainerId() {
        return containerId;
    }

    public String getScreenTitle() {
        return screenTitle;
    }
}