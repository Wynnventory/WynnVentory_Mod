package com.wynnventory.core.queue;

public final class QueueManager {

    public static final RewardPoolQueue LOOTRUN_QUEUE = new RewardPoolQueue();
    public static final RewardPoolQueue RAID_QUEUE = new RewardPoolQueue();
    public static final TrademarketQueue TRADEMARKET_QUEUE = new TrademarketQueue();
    public static final GambitQueue GAMBIT_QUEUE = new GambitQueue();

    private QueueManager() {}
}
