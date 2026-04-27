package com.wynnventory.events;

import java.util.Map;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

public abstract class RewardPreviewOpenedEvent extends Event {
    private final Map<Integer, ItemStack> items;
    private final int containerId;
    private final String screenTitle;

    protected RewardPreviewOpenedEvent(Map<Integer, ItemStack> items, int containerId, String screenTitle) {
        this.items = items;
        this.containerId = containerId;
        this.screenTitle = screenTitle;
    }

    public Map<Integer, ItemStack> getItems() {
        return items;
    }

    public int getContainerId() {
        return containerId;
    }

    public String getScreenTitle() {
        return screenTitle;
    }

    public static class Lootrun extends RewardPreviewOpenedEvent {
        public Lootrun(Map<Integer, ItemStack> items, int containerId, String screenTitle) {
            super(items, containerId, screenTitle);
        }
    }

    public static class Raid extends RewardPreviewOpenedEvent {
        public Raid(Map<Integer, ItemStack> items, int containerId, String screenTitle) {
            super(items, containerId, screenTitle);
        }
    }
}
