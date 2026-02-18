package com.wynnventory.api.service;

import com.wynntils.models.gear.type.GearTier;
import com.wynnventory.api.WynnventoryApi;
import com.wynnventory.model.item.simple.SimpleGearItem;
import com.wynnventory.model.item.simple.SimpleItem;
import com.wynnventory.model.item.simple.SimpleTierItem;
import com.wynnventory.model.reward.RewardPool;
import com.wynnventory.model.reward.RewardPoolDocument;
import com.wynnventory.model.reward.RewardType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

public enum RewardService {
    INSTANCE;

    private final WynnventoryApi api = new WynnventoryApi();
    private final List<RewardPoolDocument> rewardData = new CopyOnWriteArrayList<>();
    private CompletableFuture<Void> refreshFuture = null;

    RewardService() {}

    public CompletableFuture<List<SimpleItem>> getItems(RewardPool pool) {
        return getAllPools().thenApply(pools -> {
            List<SimpleItem> items = new ArrayList<>(pools.stream()
                    .filter(doc ->
                            doc.getRewardPool() != null && doc.getRewardPool().equals(pool))
                    .flatMap(doc -> doc.getItems().stream())
                    .toList());

            if (pool.getType() == RewardType.RAID) sortForRaid(items);
            else {
                sortForLootrun(items);
            }

            return items;
        });
    }

    private void sortForRaid(List<SimpleItem> items) {
        items.sort(Comparator.comparing(this::getRarityRank, Comparator.reverseOrder())
                .thenComparing(SimpleItem::getItemType, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                .thenComparing(SimpleItem::getType, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                .thenComparing(SimpleItem::getName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                .thenComparing(i -> i instanceof SimpleTierItem sti ? sti.getTier() : 0));
    }

    private void sortForLootrun(List<SimpleItem> items) {
        items.sort(Comparator.comparing(
                        (SimpleItem i) -> i instanceof SimpleGearItem gear && gear.isShiny(), Comparator.reverseOrder())
                .thenComparing(this::getRarityRank, Comparator.reverseOrder())
                .thenComparing(SimpleItem::getName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                .thenComparing(SimpleItem::getItemType, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                .thenComparing(SimpleItem::getType, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));
    }

    public CompletableFuture<List<RewardPoolDocument>> getAllPools() {
        synchronized (this) {
            if (!rewardData.isEmpty()) {
                return CompletableFuture.completedFuture(Collections.unmodifiableList(rewardData));
            }

            if (refreshFuture != null) {
                return refreshFuture.thenApply(v -> Collections.unmodifiableList(rewardData));
            }

            refreshFuture = reloadAllPools();
            return refreshFuture.thenApply(v -> {
                synchronized (this) {
                    refreshFuture = null;
                }
                return Collections.unmodifiableList(rewardData);
            });
        }
    }

    public CompletableFuture<List<RewardPoolDocument>> getRaidPools() {
        return getAllPools().thenApply(pools -> pools.stream()
                .filter(doc ->
                        doc.getRewardPool() != null && doc.getRewardPool().getType() == RewardType.RAID)
                .toList());
    }

    public CompletableFuture<List<RewardPoolDocument>> getLootrunPools() {
        return getAllPools().thenApply(pools -> pools.stream()
                .filter(doc ->
                        doc.getRewardPool() != null && doc.getRewardPool().getType() == RewardType.LOOTRUN)
                .toList());
    }

    public CompletableFuture<Void> reloadAllPools() {
        return CompletableFuture.allOf(fetch(RewardType.LOOTRUN), fetch(RewardType.RAID));
    }

    private CompletableFuture<Void> fetch(RewardType type) {
        return api.fetchRewardPools(type).thenAccept(pools -> {
            if (pools != null && !pools.isEmpty()) {
                rewardData.removeIf(doc ->
                        doc.getRewardPool() != null && doc.getRewardPool().getType() == type);
                rewardData.addAll(pools);
            }
        });
    }

    private int getRarityRank(SimpleItem i) {
        return switch (i.getRarityEnum()) {
            case GearTier.MYTHIC -> 7;
            case GearTier.FABLED -> 6;
            case GearTier.LEGENDARY -> 5;
            case GearTier.RARE -> 4;
            case GearTier.UNIQUE -> 3;
            case GearTier.SET -> 2;
            case GearTier.NORMAL -> 1;
            default -> 0;
        };
    }
}
