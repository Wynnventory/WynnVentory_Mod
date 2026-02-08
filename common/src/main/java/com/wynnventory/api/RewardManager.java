package com.wynnventory.api;

import com.wynnventory.model.item.simple.SimpleItem;
import com.wynnventory.model.reward.RewardPool;
import com.wynnventory.model.reward.RewardPoolDocument;
import com.wynnventory.model.reward.RewardType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class RewardManager {
    private static final WynnventoryApi API = new WynnventoryApi();

    private static final List<RewardPoolDocument> rewardData = new ArrayList<>();

    private RewardManager() {}

    public static void reloadAllPools() {
        refresh(RewardType.LOOTRUN);
        refresh(RewardType.RAID);
    }

    public static void refresh(RewardType type) {
        API.fetchRewardPools(type)
                .thenAccept(documents -> {
                    rewardData.clear();
                    rewardData.addAll(documents);
                });
    }

    public static List<SimpleItem> getItems(RewardPool pool) {
        List<SimpleItem> items = new ArrayList<>(rewardData.stream()
                .filter(doc -> doc.getRewardPool().equals(pool))
                .flatMap(doc -> doc.getItems().stream())
                .toList());

        items.sort(Comparator
                .comparingInt(RewardManager::getRarityRank).reversed()
                .thenComparing(SimpleItem::getType, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(SimpleItem::getName, String.CASE_INSENSITIVE_ORDER)
        );
        return items;
    }

    private static int getRarityRank(SimpleItem i) {
        String r = i.getRarity();
        if (r == null) return 0;
        return switch (r.trim().toLowerCase()) {
            case "mythic"    -> 7;
            case "fabled"    -> 6;
            case "legendary" -> 5;
            case "rare"      -> 4;
            case "unique"    -> 3;
            case "set"       -> 2;
            case "common"    -> 1;
            default          -> 0;
        };
    }
}
