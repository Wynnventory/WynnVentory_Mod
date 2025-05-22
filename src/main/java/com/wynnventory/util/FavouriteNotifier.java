package com.wynnventory.util;

import com.wynntils.core.components.Services;
import com.wynntils.utils.mc.McUtils;
import com.wynnventory.config.ConfigManager;
import com.wynnventory.core.ModInfo;
import com.wynnventory.model.item.Lootpool;
import com.wynnventory.model.item.LootpoolItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class FavouriteNotifier {

    private static final int MAX_TOASTS = ConfigManager.getInstance().getFavouriteNotifierSettings().getMaxToasts();

    private FavouriteNotifier() {
    }

    public static void checkFavourites() {
        if (ConfigManager.getInstance().getFavouriteNotifierSettings().isEnableNotifier()) {
            Set<String> favourites = Services.Favorites.getFavoriteItems();
            if (favourites.isEmpty()) return;

            List<FavouriteMatch> matches = findMatches(favourites);
            if (matches.isEmpty()) return;

            showToasts(matches);
        } else {
            ModInfo.logInfo("Favourite Notifier is disabled. No toasts will be displayed");
        }
    }

    private static List<FavouriteMatch> findMatches(Set<String> favourites) {
        boolean mythicsOnly = ConfigManager.getInstance().getFavouriteNotifierSettings().isMythicsOnly();

        Set<String> seen = new HashSet<>();
        List<FavouriteMatch> result = new ArrayList<>();

        List<Lootpool> allPools = Stream.concat(
                LootpoolManager.getLootrunPools().stream(),
                LootpoolManager.getRaidPools().stream()
        ).toList();

        // Process all pools and collect all matches without early termination
        for (Lootpool pool : allPools) {
            String region = pool.getRegion();
            List<LootpoolItem> itemsToCheck = mythicsOnly ? pool.getMythics() : pool.getItems().stream().toList();

            for (LootpoolItem item : itemsToCheck) {
                String name = item.getName();
                String uniqueKey = name + ":" + region;

                if (favourites.contains(name) && seen.add(uniqueKey)) {
                    result.add(new FavouriteMatch(name, region, item.getRarityColor()));
                }
            }
        }

        return result;
    }


    private static void showToasts(List<FavouriteMatch> matches) {
        int total = matches.size();
        int shown = Math.min(total, MAX_TOASTS - 1);

        for (int i = 0; i < shown; i++) {
            FavouriteMatch match = matches.get(i);
            showToast("Favourite Found", Component.literal(match.rarityColor() + match.itemName() + ChatFormatting.WHITE + " in " + match.region()));
        }

        int remaining = total - shown;
        if (remaining > 0) {
            showToast("More Favourites", Component.literal(remaining + " more..."));
        }
    }

    private static void showToast(String title, Component desc) {
        McUtils.mc().getToastManager().addToast(
                new SystemToast(
                        new SystemToast.SystemToastId(10000L),
                        Component.literal(title),
                        desc
                )
        );
    }

    public record FavouriteMatch(String itemName, String region, ChatFormatting rarityColor) {
    }
}
