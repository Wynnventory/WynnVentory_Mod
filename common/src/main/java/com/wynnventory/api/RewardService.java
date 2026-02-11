package com.wynnventory.api;

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

    RewardService() {}

    public CompletableFuture<List<SimpleItem>> getItems(RewardPool pool) {
        return getPools().thenApply(pools -> {
            List<SimpleItem> items = new ArrayList<>(pools.stream()
                    .filter(doc -> doc.getRewardPool().equals(pool))
                    .flatMap(doc -> doc.getItems().stream())
                    .toList());

            items.sort(Comparator
                    .comparingInt(this::getRarityRank).reversed()
                    .thenComparing(SimpleItem::getType, String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(SimpleItem::getName, String.CASE_INSENSITIVE_ORDER)
            );
            return items;
        });
    }

    public CompletableFuture<List<RewardPoolDocument>> getPools() {
        if (!rewardData.isEmpty()) {
            return CompletableFuture.completedFuture(Collections.unmodifiableList(rewardData));
        }

        return reloadAllPools().thenApply(v -> Collections.unmodifiableList(rewardData));
    }

    private CompletableFuture<Void> reloadAllPools() {
        rewardData.clear();

        return CompletableFuture.allOf(
                refresh(RewardType.LOOTRUN),
                refresh(RewardType.RAID)
        );
    }

    private CompletableFuture<Void> refresh(RewardType type) {
        return api.fetchRewardPools(type)
                .thenAccept(pools -> {
                    if (pools != null) {
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
