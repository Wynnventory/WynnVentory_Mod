package com.wynnventory.api;

import com.wynnventory.accessor.ItemQueueAccessor;
import com.wynnventory.config.ConfigManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.Minecraft;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WynnventoryScheduler {

    private static final WynnventoryAPI API = new WynnventoryAPI();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void startScheduledTask() {
        scheduler.scheduleAtFixedRate(WynnventoryScheduler::processMarketAndLootItems, 1, ConfigManager.SEND_DELAY_MINS, TimeUnit.MINUTES);
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            WynnventoryScheduler.stopScheduledTask();
        });
    }

    public static void stopScheduledTask() {
        processMarketAndLootItems();
        scheduler.shutdown();

        try {
            if (!scheduler.awaitTermination(ConfigManager.SEND_DELAY_MINS, TimeUnit.MINUTES)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }

    private static void processMarketAndLootItems() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getConnection() != null) {
            ItemQueueAccessor accessor = (ItemQueueAccessor) minecraft.getConnection();
            if (accessor != null) {
                if (!accessor.getQueuedMarketItems().isEmpty()) {
                    API.sendTradeMarketResults(accessor.getQueuedMarketItems());
                    accessor.getQueuedMarketItems().clear();
                }
                if (!accessor.getQueuedLootItems().isEmpty()) {
                    API.sendLootpoolData(accessor.getQueuedLootItems());
                    accessor.getQueuedLootItems().clear();
                }
            }
        }
    }
}
