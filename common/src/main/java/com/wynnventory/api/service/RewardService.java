package com.wynnventory.api.service;

import com.wynnventory.api.WynnventoryApi;
import com.wynnventory.model.item.simple.SimpleItem;
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
        return getPools().thenApply(pools -> {
            List<SimpleItem> items = new ArrayList<>(pools.stream()
                    .filter(doc -> doc.getRewardPool() != null && doc.getRewardPool().equals(pool))
                    .flatMap(doc -> doc.getItems().stream())
                    .toList());

            items.sort(Comparator
                    .comparingInt(this::getRarityRank).reversed()
                    .thenComparing(SimpleItem::getType, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                    .thenComparing(SimpleItem::getName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
            );
            return items;
        });
    }

    public CompletableFuture<List<RewardPoolDocument>> getPools() {
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

    private CompletableFuture<Void> reloadAllPools() {
        return CompletableFuture.allOf(
                fetch(RewardType.LOOTRUN),
                fetch(RewardType.RAID)
        );
    }

    private CompletableFuture<Void> fetch(RewardType type) {
        return api.fetchRewardPools(type)
                .thenAccept(pools -> {
                    if (pools != null && !pools.isEmpty()) {
                        rewardData.removeIf(doc -> doc.getRewardPool() != null && doc.getRewardPool().getType() == type);
                        rewardData.addAll(pools);
                    }
                });
    }

    private int getRarityRank(SimpleItem i) {
        String r = i.getRarity();
        if (r == null) return 0;
        return switch (r.trim().toLowerCase()) {
            case "mythic" -> 7;
            case "fabled" -> 6;
            case "legendary" -> 5;
            case "rare" -> 4;
            case "unique" -> 3;
            case "set" -> 2;
            case "common" -> 1;
            default -> 0;
        };
    }
}
