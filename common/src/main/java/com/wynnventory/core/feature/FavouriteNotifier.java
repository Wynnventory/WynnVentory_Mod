package com.wynnventory.core.feature;

import com.wynntils.core.components.Services;
import com.wynntils.utils.mc.McUtils;
import com.wynnventory.api.RewardManager;
import com.wynnventory.core.config.ModConfig;
import com.wynnventory.model.item.simple.SimpleItem;
import com.wynnventory.model.reward.RewardPool;
import com.wynnventory.model.reward.RewardPoolDocument;
import com.wynnventory.util.ItemStackUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FavouriteNotifier {
    private FavouriteNotifier() {}

    public static void checkFavourites() {
        if(!ModConfig.getInstance().getFavouriteNotifierSettings().isEnableNotifier()) return;

        Set<String> favourites = Services.Favorites.getFavoriteItems();
        if (favourites.isEmpty()) return;

        List<FavouriteMatch> matches = findMatches(favourites);
        if (matches.isEmpty()) return;

        showToasts(matches);
    }

    private static List<FavouriteMatch> findMatches(Set<String> favourites) {
        boolean mythicsOnly = ModConfig.getInstance().getFavouriteNotifierSettings().isMythicsOnly();

        List<FavouriteMatch> result = new ArrayList<>();
        for (RewardPoolDocument document : RewardManager.getPools()) {
            List<SimpleItem> itemsToCheck = mythicsOnly ? document.getItems().stream().filter(simpleItem -> simpleItem.getRarity().equalsIgnoreCase("mythic")).toList() : document.getItems();

            for (SimpleItem item : itemsToCheck) {
                if (favourites.contains(item.getName())) {
                    result.add(new FavouriteMatch(item.getName(), document.getRewardPool(), ItemStackUtils.getRarityChatFormattingByName(item.getRarity())));
                }
            }
        }

        return result;
    }


    private static void showToasts(List<FavouriteMatch> matches) {
        int total = matches.size();
        int shown = Math.min(total, ModConfig.getInstance().getFavouriteNotifierSettings().getMaxToasts() - 1);

        for (int i = 0; i < shown; i++) {
            FavouriteMatch match = matches.get(i);
            showToast("feature.wynnventory.favotireNotifier.favouriteFound.title", Component.literal(match.rarityColor() + match.itemName() + ChatFormatting.WHITE + " in " + match.pool.getShortName()));
        }

        int remaining = total - shown;
        if (remaining > 0) {
            showToast("feature.wynnventory.favotireNotifier.moreFound.title", Component.literal(remaining + " more..."));
        }
    }

    private static void showToast(String title, Component desc) {
        McUtils.mc().getToastManager().addToast(
                new SystemToast(
                        new SystemToast.SystemToastId(10000L),
                        Component.translatable(title),
                        desc
                )
        );
    }

    public record FavouriteMatch(String itemName, RewardPool pool, ChatFormatting rarityColor) {}
}
