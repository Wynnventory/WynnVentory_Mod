package com.wynnventory.api;

import com.wynnventory.accessor.ItemQueueAccessor;
import com.wynnventory.core.ModInfo;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.Minecraft;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WynnventoryScheduler {

    private static final WynnventoryAPI API = new WynnventoryAPI();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final int SEND_DELAY_MINS = 5;

    private WynnventoryScheduler() {}

    public static void startScheduledTask() {
        scheduler.scheduleAtFixedRate(WynnventoryScheduler::processBufferQueues, 1, SEND_DELAY_MINS, TimeUnit.MINUTES);
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> WynnventoryScheduler.stopScheduledTask());
    }

    public static void stopScheduledTask() {
        ModInfo.logInfo("Shutdown detected...");
        processBufferQueues();
        scheduler.shutdown();

        try {
            if (!scheduler.awaitTermination(SEND_DELAY_MINS, TimeUnit.MINUTES)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }

    private static void processBufferQueues() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getConnection() != null) {
            ItemQueueAccessor accessor = (ItemQueueAccessor) minecraft.getConnection();

            if (!accessor.getQueuedMarketItems().isEmpty()) {
                API.sendTradeMarketResults(accessor.getQueuedMarketItems());
                accessor.getQueuedMarketItems().clear();
            }

            if (!accessor.getQueuedLootpools().isEmpty()) {
                API.sendLootpoolData(accessor.getQueuedLootpools().values().stream().toList());
                accessor.getQueuedLootpools().clear();
            }

            if (!accessor.getQueuedRaidpools().isEmpty()) {
                API.sendRaidpoolData(accessor.getQueuedRaidpools().values().stream().toList());
                accessor.getQueuedRaidpools().clear();
            }

            if(!accessor.getQueuedGambitItems().isEmpty()) {
                API.sendGambitItems(accessor.getQueuedGambitItems());
                accessor.getQueuedGambitItems().clear();
            }
        }
    }
}
