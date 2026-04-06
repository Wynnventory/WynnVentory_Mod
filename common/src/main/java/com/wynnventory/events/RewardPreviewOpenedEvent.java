package com.wynnventory.events;

import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

public abstract class RewardPreviewOpenedEvent extends Event {
    private final ItemStack item;
    private final int containerId;
    private final String screenTitle;

    protected RewardPreviewOpenedEvent(ItemStack item, int containerId, String screenTitle) {
        this.item = item;
        this.containerId = containerId;
        this.screenTitle = screenTitle;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getContainerId() {
        return containerId;
    }

    public String getScreenTitle() {
        return screenTitle;
    }

    public static class Lootrun extends RewardPreviewOpenedEvent {
        public Lootrun(ItemStack item, int containerId, String screenTitle) {
            super(item, containerId, screenTitle);
        }
    }

    public static class Raid extends RewardPreviewOpenedEvent {
        public Raid(ItemStack item, int containerId, String screenTitle) {
            super(item, containerId, screenTitle);
        }
    }
}
