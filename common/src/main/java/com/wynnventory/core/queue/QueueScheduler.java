package com.wynnventory.core.queue;

import com.wynnventory.api.Endpoint;
import com.wynnventory.api.WynnventoryApi;
import com.wynnventory.core.WynnventoryMod;
import com.wynnventory.model.item.simple.SimpleGambitItem;
import com.wynnventory.model.item.simple.SimpleItem;
import com.wynnventory.model.item.trademarket.TrademarketListing;
import com.wynnventory.model.reward.RewardPool;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class QueueScheduler {
    private static final WynnventoryApi API = new WynnventoryApi();
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final int SEND_DELAY_MINS = 5;

    public static final RewardPoolQueue LOOTRUN_QUEUE = new RewardPoolQueue();
    public static final RewardPoolQueue RAID_QUEUE = new RewardPoolQueue();
    public static final TrademarketQueue TRADEMARKET_QUEUE = new TrademarketQueue();
    public static final GambitQueue GAMBIT_QUEUE = new GambitQueue();

    private QueueScheduler() {}

    public static void startScheduledTask() {
        WynnventoryMod.logDebug("Starting queue scheduler with {} mins delay", SEND_DELAY_MINS);
        scheduler.scheduleAtFixedRate(
                () -> {
                    try {
                        sendQueuedItems();
                    } catch (Exception t) {
                        WynnventoryMod.logError("QueueScheduler crashed!", t);
                        throw t;
                    }
                },
                1,
                SEND_DELAY_MINS,
                TimeUnit.MINUTES);
        addShutdownHook();
    }

    private static void stopScheduledTask() {
        WynnventoryMod.logInfo("Shutdown detected...");
        sendQueuedItems();
        scheduler.shutdown();

        try {
            if (!scheduler.awaitTermination(SEND_DELAY_MINS, TimeUnit.MINUTES)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }

    public static void sendQueuedItems() {
        Map<RewardPool, Set<SimpleItem>> lootrunItems = LOOTRUN_QUEUE.drainAll();
        Map<RewardPool, Set<SimpleItem>> raidItems = RAID_QUEUE.drainAll();
        Set<TrademarketListing> trademarketItems = TRADEMARKET_QUEUE.drainAll();
        Set<SimpleGambitItem> gambitItems = GAMBIT_QUEUE.drainAll();
        WynnventoryMod.logDebug(
                "Processing {} lootrun pool, {} raid reward pools, {} trademarket items, {} gambit items",
                lootrunItems.size(),
                raidItems.size(),
                trademarketItems.size(),
                gambitItems.size());
        if (!lootrunItems.isEmpty()) API.sendRewardPoolData(lootrunItems, Endpoint.LOOTPOOL_ITEMS);
        if (!raidItems.isEmpty()) API.sendRewardPoolData(raidItems, Endpoint.RAIDPOOL_ITEMS);
        if (!trademarketItems.isEmpty()) API.sendTradeMarketData(trademarketItems);
        if (!gambitItems.isEmpty()) API.sendGambitData(gambitItems);
    }

    private static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(QueueScheduler::stopScheduledTask));
    }
}
