package com.wynnventory.util;

import com.wynntils.core.components.Services;
import com.wynntils.utils.mc.McUtils;
import com.wynnventory.config.ConfigManager;
import com.wynnventory.model.item.GroupedLootpool;
import com.wynnventory.model.item.LootpoolGroup;
import com.wynnventory.model.item.LootpoolItem;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class FavouriteNotifier {

//    private static final int MAX_TOASTS = ConfigManager.getInstance().getMaxFavouriteNotifierToasts();
    private static final int MAX_TOASTS = 5;

    private FavouriteNotifier() {}

    public static void checkFavourites() {
        Set<String> favourites = Services.Favorites.getFavoriteItems();
        if (favourites.isEmpty()) return;

        List<FavouriteMatch> matches = findMatches(favourites);
        if (matches.isEmpty()) return;

        showToasts(matches);
    }

    private static List<FavouriteMatch> findMatches(Set<String> favourites) {
        Set<String> seen = new HashSet<>();
        List<FavouriteMatch> result = new ArrayList<>();

        Stream<GroupedLootpool> allPools = Stream.concat(
                LootpoolManager.getLootrunPools().stream(),
                LootpoolManager.getRaidPools().stream()
        );

        allPools.forEach(pool -> {
            String region = pool.getRegion();

            for (LootpoolGroup group : pool.getGroupItems()) {
                for (LootpoolItem item : group.getLootItems()) {
                    String name = item.getName();
                    String uniqueKey = name + ":" + region;

                    if (favourites.contains(name) && seen.add(uniqueKey)) {
                        result.add(new FavouriteMatch(name, region));
                        if (result.size() >= MAX_TOASTS) return;
                    }
                }
            }
        });

        return result;
    }

    private static void showToasts(List<FavouriteMatch> matches) {
        int total = matches.size();
        int shown = Math.min(total, MAX_TOASTS - 1);

        for (int i = 0; i < shown; i++) {
            FavouriteMatch match = matches.get(i);
            showToast("Favourite Found", match.itemName() + " in " + match.region());
        }

        int remaining = total - shown;
        if (remaining > 0) {
            showToast("More Favourites", "…and " + remaining + " more.");
        }
    }

    private static void showToast(String title, String desc) {
        McUtils.mc().getToastManager().addToast(
                new SystemToast(
                        new SystemToast.SystemToastId(10000L),
                        Component.literal(title),
                        Component.literal(desc)
                )
        );
    }

    public record FavouriteMatch(String itemName, String region) {}
}