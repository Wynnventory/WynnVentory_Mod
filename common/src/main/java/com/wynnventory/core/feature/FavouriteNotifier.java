package com.wynnventory.core.feature;

import com.wynntils.core.components.Services;
import com.wynntils.utils.mc.McUtils;
import com.wynnventory.api.service.RewardService;
import com.wynnventory.core.config.ModConfig;
import com.wynnventory.model.item.simple.SimpleItem;
import com.wynnventory.model.reward.RewardPool;
import com.wynnventory.model.reward.RewardPoolDocument;
import com.wynnventory.util.ItemStackUtils;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;

public class FavouriteNotifier {
    private FavouriteNotifier() {}

    public static void checkFavourites() {
        if (!ModConfig.getInstance().getFavouriteNotifierSettings().isEnableNotifier()) return;

        Set<String> favourites = Services.Favorites.getFavoriteItems();
        if (favourites.isEmpty()) return;

        RewardService.INSTANCE.getAllPools().thenAccept(pools -> {
            List<FavouriteMatch> matches = findMatches(favourites, pools);
            if (!matches.isEmpty()) {
                showToasts(matches);
            }
        });
    }

    private static List<FavouriteMatch> findMatches(Set<String> favourites, List<RewardPoolDocument> pools) {
        boolean mythicsOnly =
                ModConfig.getInstance().getFavouriteNotifierSettings().isMythicsOnly();

        Set<FavouriteMatch> result = new HashSet<>();
        for (RewardPoolDocument document : pools) {
            for (SimpleItem item : document.getItems()) {
                if (!favourites.contains(item.getName())
                        || (mythicsOnly && !item.getRarity().equalsIgnoreCase("mythic"))) continue;

                result.add(new FavouriteMatch(
                        item.getName(),
                        document.getRewardPool(),
                        ItemStackUtils.getRarityChatFormattingByName(item.getRarity())));
            }
        }

        return result.stream().toList();
    }

    private static void showToasts(List<FavouriteMatch> matches) {
        int total = matches.size();
        int shown = Math.min(
                total, ModConfig.getInstance().getFavouriteNotifierSettings().getMaxToasts() - 1);

        for (int i = 0; i < shown; i++) {
            FavouriteMatch match = matches.get(i);
            showToast(
                    "feature.wynnventory.favotireNotifier.favouriteFound.title",
                    Component.literal(match.rarityColor() + match.itemName() + ChatFormatting.WHITE + " in "
                            + match.pool.getShortName()));
        }

        int remaining = total - shown;
        if (remaining > 0) {
            showToast(
                    "feature.wynnventory.favotireNotifier.moreFound.title", Component.literal(remaining + " more..."));
        }
    }

    private static void showToast(String title, Component desc) {
        McUtils.mc().execute(() -> McUtils.mc()
                .getToastManager()
                .addToast(new SystemToast(new SystemToast.SystemToastId(5000), Component.translatable(title), desc)));
    }

    public record FavouriteMatch(String itemName, RewardPool pool, ChatFormatting rarityColor) {}
}
