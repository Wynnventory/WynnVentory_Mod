package com.wynnventory.queue;

public final class QueueManager {

    private static final RewardPoolQueue LOOTRUN_QUEUE = new RewardPoolQueue();

    private QueueManager() {}

    public static RewardPoolQueue lootrun() {
        return LOOTRUN_QUEUE;
    }
}
