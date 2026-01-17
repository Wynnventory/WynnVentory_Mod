package com.wynnventory.queue;

public final class QueueManager {

    public static final RewardPoolQueue LOOTRUN_QUEUE = new RewardPoolQueue();
    public static final RewardPoolQueue RAID_QUEUE = new RewardPoolQueue();
    public static final TrademarketQueue TRADEMARKET_QUEUE = new TrademarketQueue();

    private QueueManager() {}
}
