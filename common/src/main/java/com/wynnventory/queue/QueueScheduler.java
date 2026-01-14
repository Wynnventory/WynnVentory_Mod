package com.wynnventory.queue;

import com.wynnventory.api.WynnventoryApi;
import com.wynnventory.core.WynnventoryMod;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class QueueScheduler {

    private static final WynnventoryApi API = new WynnventoryApi();
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final int SEND_DELAY_MINS = 1;

    private QueueScheduler() {}

    public static void startScheduledTask() {
        WynnventoryMod.logDebug("Starting queue scheduler with {} mins delay", SEND_DELAY_MINS);
        scheduler.scheduleAtFixedRate(QueueScheduler::processQueuedItems, 1, SEND_DELAY_MINS, TimeUnit.MINUTES);
        addShutdownHook();
    }

    private static void stopScheduledTask() {
        WynnventoryMod.logInfo("Shutdown detected...");
        processQueuedItems();
        scheduler.shutdown();

        try {
            if (!scheduler.awaitTermination(SEND_DELAY_MINS, TimeUnit.MINUTES)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }

    private static void processQueuedItems() {
        WynnventoryMod.logDebug("Processing queued items");
        // TODO: Process lootpool items
    }

    private static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(QueueScheduler::stopScheduledTask));
    }
}