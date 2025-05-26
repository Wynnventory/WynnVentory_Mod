package com.wynnventory.util;

import com.wynnventory.api.WynnventoryAPI;
import com.wynnventory.enums.RegionType;
import com.wynnventory.model.item.Lootpool;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LootpoolManager {
    private static final WynnventoryAPI API = new WynnventoryAPI();
    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    private static List<Lootpool> lootrunPools = List.of();
    private static List<Lootpool> raidPools = List.of();

    private LootpoolManager() {}

    public static void reloadAllPools() {
        CompletableFuture.supplyAsync(() -> API.getLootpools(RegionType.LOOTRUN), EXECUTOR)
                .thenAccept(result -> lootrunPools = result);
        CompletableFuture.supplyAsync(() -> API.getLootpools(RegionType.RAID), EXECUTOR)
                .thenAccept(result -> raidPools = result);
    }

    public static List<Lootpool> getLootrunPools() {
        return lootrunPools;
    }

    public static List<Lootpool> getRaidPools() {
        return raidPools;
    }
}
