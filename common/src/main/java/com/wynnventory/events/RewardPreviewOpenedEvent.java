package com.wynnventory.events;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

public abstract class RewardPreviewOpenedEvent extends Event {
    private final List<ItemStack> items;
    private final int containerId;
    private final String screenTitle;

    protected RewardPreviewOpenedEvent(List<ItemStack> items, int containerId, String screenTitle) {
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

    public static class Lootrun extends RewardPreviewOpenedEvent {
        public Lootrun(List<ItemStack> items, int containerId, String screenTitle) {
            super(items, containerId, screenTitle);
        }
    }

    public static class Raid extends RewardPreviewOpenedEvent {
        public Raid(List<ItemStack> items, int containerId, String screenTitle) {
            super(items, containerId, screenTitle);
        }
    }
}
