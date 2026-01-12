package com.wynnventory.event;

import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

import java.util.List;

public class LootrunPreviewOpenedEvent extends Event {
    private final List<ItemStack> items;
    private final ItemStack carriedItem;
    private final int containerId;
    private final int stateId;
    private final String screenTitle;

    public LootrunPreviewOpenedEvent(List<ItemStack> items, ItemStack carriedItem, int containerId, int stateId, String screenTitle) {
        this.items = items;
        this.carriedItem = carriedItem;
        this.containerId = containerId;
        this.stateId = stateId;
        this.screenTitle = screenTitle;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public ItemStack getCarriedItem() {
        return carriedItem;
    }

    public int getContainerId() {
        return containerId;
    }

    public int getStateId() {
        return stateId;
    }

    public String getScreenTitle() {
        return  screenTitle;
    }
}