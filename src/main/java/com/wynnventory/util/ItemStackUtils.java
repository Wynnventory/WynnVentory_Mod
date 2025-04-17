package com.wynnventory.util;

import com.wynntils.core.components.Models;
import com.wynntils.core.text.StyledText;
import com.wynntils.models.gear.type.GearInfo;
import com.wynntils.models.gear.type.GearRestrictions;
import com.wynntils.models.gear.type.GearTier;
import com.wynntils.models.items.WynnItem;
import com.wynntils.models.items.items.game.*;
import com.wynnventory.api.WynnventoryAPI;
import com.wynnventory.core.ModInfo;
import com.wynnventory.model.item.TradeMarketItemPriceHolder;
import com.wynnventory.model.item.TradeMarketItemPriceInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class ItemStackUtils {
    private static final String TITLE_TEXT = "Trade Market Price Info";
    private static final long EXPIRE_MINS = 2;

    private static final TradeMarketItemPriceInfo FETCHING = new TradeMarketItemPriceInfo();
    private static final TradeMarketItemPriceInfo UNTRADABLE = new TradeMarketItemPriceInfo();

    // Unified cache mapping item keys to current & historic price holders
    private static final Map<String, PriceHolderPair> priceCache = new HashMap<>();

    private static final ExecutorService executorService = Executors.newCachedThreadPool();
    private static final WynnventoryAPI wynnventoryAPI = new WynnventoryAPI();

    public static StyledText getWynntilsOriginalName(ItemStack itemStack) {
        try {
            Field originalNameField = ItemStack.class.getDeclaredField("wynntilsOriginalName");
            originalNameField.setAccessible(true);
            return (StyledText) originalNameField.get(itemStack);
        } catch (ReflectiveOperationException e) {
            ModInfo.logError("Error retrieving original name", e);
            return null;
        }
    }

    public static List<Component> getTooltips(ItemStack itemStack) {
        Optional<WynnItem> maybeItem = Models.Item.getWynnItem(itemStack);
        List<Component> tooltipLines = new ArrayList<>();

        if (maybeItem.isPresent()) {
            WynnItem wynnItem = maybeItem.get();
            switch (wynnItem) {
                case GearItem gearItem -> processGear(gearItem.getItemInfo(), gearItem.getName(), gearItem.getGearTier().getChatFormatting(), tooltipLines);
                case GearBoxItem gearBoxItem -> processGearBox(gearBoxItem, tooltipLines);
                case IngredientItem ingredientItem -> processSimple(ingredientItem.getName(), ingredientItem.getName(), -1, ChatFormatting.GRAY, tooltipLines);
                case MaterialItem materialItem -> {
                    String materialKey = getMaterialKey(materialItem);
                    processSimple(getMaterialName(materialItem), materialKey, materialItem.getQualityTier(), ChatFormatting.WHITE, tooltipLines);
                }
                default -> {}
            }
        }

        return tooltipLines;
    }

    private static void processGear(GearInfo gearInfo, String itemName, ChatFormatting color, List<Component> tooltipLines) {
        tooltipLines.addFirst(Component.literal(TITLE_TEXT).withStyle(ChatFormatting.GOLD));
        fetchPrices(itemName,
                () -> wynnventoryAPI.fetchItemPrice(itemName),
                () -> gearInfo.metaInfo().restrictions() == GearRestrictions.UNTRADABLE ? UNTRADABLE : wynnventoryAPI.fetchLatestHistoricItemPrice(itemName)
        );
        tooltipLines.addAll(createTooltip(itemName, color, gearInfo.metaInfo().restrictions()));
        evictExpiredPrices(itemName);
    }

    private static void processGearBox(GearBoxItem gearBoxItem, List<Component> tooltipLines) {
        tooltipLines.addFirst(Component.literal(TITLE_TEXT).withStyle(ChatFormatting.GOLD));

        List<GearInfo> possibleGears = Models.Gear.getPossibleGears(gearBoxItem);
        if (possibleGears.isEmpty()) {
            return;
        }

        ChatFormatting color = possibleGears.getFirst().tier().getChatFormatting();

        List<TradeMarketItemPriceHolder> priceHolders = new ArrayList<>();
        for (GearInfo gearInfo : possibleGears) {
            String gearName = gearInfo.name();
            fetchPrices(gearName,
                    () -> wynnventoryAPI.fetchItemPrice(gearName),
                    () -> wynnventoryAPI.fetchLatestHistoricItemPrice(gearName)
            );
            PriceHolderPair pair = priceCache.get(gearName);
            priceHolders.add(pair.currentHolder);
        }

        // Use original sorting logic
        PriceTooltipHelper.sortTradeMarketPriceHolders(priceHolders);
        for (TradeMarketItemPriceHolder holder : priceHolders) {
            String itemKey = holder.getItemName();
            tooltipLines.addAll(createTooltip(itemKey, color, null));
            tooltipLines.add(Component.literal("")); // spacer
            evictExpiredPrices(itemKey);
        }
    }

    private static void processSimple(String displayName, String itemKey, int tier, ChatFormatting color, List<Component> tooltipLines) {
        tooltipLines.addFirst(Component.literal(TITLE_TEXT).withStyle(ChatFormatting.GOLD));

        fetchPrices(itemKey,
                () -> wynnventoryAPI.fetchItemPrice(displayName, tier),
                () -> wynnventoryAPI.fetchLatestHistoricItemPrice(displayName, tier));

        tooltipLines.addAll(createTooltip(displayName, itemKey, color, null));
        evictExpiredPrices(itemKey);
    }

    private static void fetchPrices(String itemKey, Supplier<TradeMarketItemPriceInfo> currentPriceSupplier, Supplier<TradeMarketItemPriceInfo> historicPriceSupplier) {
        priceCache.computeIfAbsent(itemKey, key -> {
            TradeMarketItemPriceHolder currentHolder = new TradeMarketItemPriceHolder(FETCHING, key);
            TradeMarketItemPriceHolder historicHolder = new TradeMarketItemPriceHolder(FETCHING, key);

            CompletableFuture.supplyAsync(currentPriceSupplier, executorService).thenAccept(currentHolder::setPriceInfo);
            CompletableFuture.supplyAsync(historicPriceSupplier, executorService).thenAccept(historicHolder::setPriceInfo);

            return new PriceHolderPair(key, currentHolder, historicHolder);
        });
    }

    private static List<Component> createTooltip(String itemKey, ChatFormatting color, GearRestrictions restrictions) {
        return createTooltip(itemKey, itemKey, color, restrictions);
    }

    private static List<Component> createTooltip(String displayName, String itemKey, ChatFormatting color, GearRestrictions restrictions) {
        PriceHolderPair holders = priceCache.get(itemKey);
        TradeMarketItemPriceInfo currentInfo = holders.currentHolder.getPriceInfo();

        if (restrictions == GearRestrictions.UNTRADABLE || currentInfo == UNTRADABLE) {
            return Collections.singletonList(Component.literal("Item is untradable.").withStyle(ChatFormatting.RED));
        }
        if (currentInfo == FETCHING) {
            return Collections.singletonList(Component.literal("Retrieving price information...").withStyle(ChatFormatting.WHITE));
        }

        TradeMarketItemPriceInfo historicInfo = holders.historicHolder.getPriceInfo();
        return PriceTooltipHelper.createPriceTooltip(currentInfo, historicInfo, displayName, color);
    }

    private static void evictExpiredPrices(String itemKey) {
        PriceHolderPair holders = priceCache.get(itemKey);
        if (holders.currentHolder.isPriceExpired(EXPIRE_MINS)) {
            priceCache.remove(itemKey);
        }
    }

    public static ChatFormatting getRarityColor(String rarity) {
        return Optional.ofNullable(GearTier.fromString(rarity))
                .map(GearTier::getChatFormatting)
                .orElse(ChatFormatting.WHITE);
    }

    // Holds both current and historic price holders for an item
    private static class PriceHolderPair {
        final String itemKey;
        final TradeMarketItemPriceHolder currentHolder;
        final TradeMarketItemPriceHolder historicHolder;

        PriceHolderPair(String itemKey, TradeMarketItemPriceHolder currentHolder, TradeMarketItemPriceHolder historicHolder) {
            this.itemKey = itemKey;
            this.currentHolder = currentHolder;
            this.historicHolder = historicHolder;
        }
    }

    private static String getMaterialName(MaterialItem item) {
        String source = item.getMaterialProfile().getSourceMaterial().name();
        String resource = item.getMaterialProfile().getResourceType().name();
        return StringUtils.toCamelCase(source + " " + resource);
    }

    private static String getMaterialKey(MaterialItem item) {
        return getMaterialName(item) + item.getQualityTier();
    }
}