package com.wynnventory.util;

import com.wynnventory.api.WynnventoryAPI;
import com.wynnventory.enums.PoolType;
import com.wynnventory.model.item.GroupedLootpool;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LootpoolManager {
    private static final WynnventoryAPI API = new WynnventoryAPI();
    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    private static List<GroupedLootpool> lootrunPools = List.of();
    private static List<GroupedLootpool> raidPools = List.of();

    private LootpoolManager() {}

    public static void reloadAllPools() {
        CompletableFuture.supplyAsync(() -> API.getLootpools(PoolType.LOOTRUN), EXECUTOR)
                .thenAccept(result -> lootrunPools = result);
        CompletableFuture.supplyAsync(() -> API.getLootpools(PoolType.RAID), EXECUTOR)
                .thenAccept(result -> raidPools = result);
    }

    public static List<GroupedLootpool> getLootrunPools() {
        return lootrunPools;
    }

    public static List<GroupedLootpool> getRaidPools() {
        return raidPools;
    }
}
