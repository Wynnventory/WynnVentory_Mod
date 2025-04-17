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

public class ItemStackUtils {
    private static final String TITLE_TEXT = "Trade Market Price Info";
    private static final long EXPIRE_MINS = 2;

    private static final TradeMarketItemPriceInfo FETCHING = new TradeMarketItemPriceInfo();
    private static final TradeMarketItemPriceInfo UNTRADABLE = new TradeMarketItemPriceInfo();

    private static final Map<String, TradeMarketItemPriceHolder> fetchedPrices = new HashMap<>();
    private static final Map<String, TradeMarketItemPriceHolder> fetchedHistoricPrices = new HashMap<>();

    private static final ExecutorService executorService = Executors.newCachedThreadPool();
    private static final WynnventoryAPI API = new WynnventoryAPI();

    public static StyledText getWynntilsOriginalName(ItemStack itemStack) {
        try {
            Field wynntilsOriginalName = ItemStack.class.getDeclaredField("wynntilsOriginalName");
            wynntilsOriginalName.setAccessible(true);

            return (StyledText) wynntilsOriginalName.get(itemStack);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            ModInfo.logError("Error trying to get wynntilsOriginalName.", e);
            return null;
        }
    }

    public static List<Component> getTooltips(ItemStack itemStack) {
        Optional<WynnItem> maybeWynnItem = Models.Item.getWynnItem(itemStack);
        List<Component> tooltipComponents = new ArrayList<>();

        if(maybeWynnItem.isPresent()) {

            WynnItem wynnItem = maybeWynnItem.get();
            switch (wynnItem) {
                case GearItem gearItem -> processItemTooltip(gearItem, tooltipComponents);
                case GearBoxItem gearBoxItem -> processBoxedTooltip(gearBoxItem, tooltipComponents);
                case IngredientItem ingredientItem -> processIngredientTooltip(ingredientItem, tooltipComponents);
                case MaterialItem materialItem -> processMaterialTooltip(materialItem, tooltipComponents);
                default -> {
                    return tooltipComponents;
                }
            }
        }

        tooltipComponents.addFirst(Component.literal(TITLE_TEXT).withStyle(ChatFormatting.GOLD));

        return tooltipComponents;
    }

    private static void processItemTooltip(GearItem gearItem, List<Component> tooltipComponents) {
        fetchPricesForGear(gearItem.getItemInfo());
        tooltipComponents.addAll(getTooltipsForGear(gearItem.getName(), gearItem.getGearTier().getChatFormatting()));
        cleanExpiredPrices(gearItem.getName());
    }

    private static void processBoxedTooltip(GearBoxItem gearBoxItem, List<Component> tooltipComponents) {
        List<GearInfo> possibleGears = Models.Gear.getPossibleGears(gearBoxItem);

        if(possibleGears.isEmpty()) return;

        ChatFormatting color = possibleGears.getFirst().tier().getChatFormatting();

        List<TradeMarketItemPriceHolder> priceHolders = new ArrayList<>();
        for (GearInfo gear : possibleGears) {
            fetchPricesForGear(gear);
            priceHolders.add(fetchedPrices.get(gear.name()));
        }

        PriceTooltipHelper.sortTradeMarketPriceHolders(priceHolders);
        for (TradeMarketItemPriceHolder holder : priceHolders) {
            tooltipComponents.addAll(getTooltipsForGear(holder.getItemName(), color));
            tooltipComponents.add(Component.literal("")); // Spacer
            cleanExpiredPrices(holder.getItemName());
        }
    }

    private static void processIngredientTooltip(IngredientItem ingredientItem, List<Component> tooltipComponents) {
        fetchPricesForIngredients(ingredientItem);
        tooltipComponents.addAll(getTooltipsForIngredient(ingredientItem.getName()));
        cleanExpiredPrices(ingredientItem.getName());
    }

    private static void processMaterialTooltip(MaterialItem materialItem, List<Component> tooltipComponents) {
        String itemName = getMaterialName(materialItem);

        fetchPricesForMaterial(materialItem);
        tooltipComponents.addAll(getTooltipsForMaterial(getMaterialName(materialItem), materialItem.getQualityTier()));
        cleanExpiredPrices(itemName);
    }

    private static void cleanExpiredPrices(String gearName) {
        TradeMarketItemPriceHolder priceHolder = fetchedPrices.get(gearName);
        if (priceHolder != null && priceHolder.isPriceExpired(EXPIRE_MINS)) {
            fetchedPrices.remove(gearName);
        }
        TradeMarketItemPriceHolder historicHolder = fetchedHistoricPrices.get(gearName);
        if (historicHolder != null && historicHolder.isPriceExpired(EXPIRE_MINS)) {
            fetchedHistoricPrices.remove(gearName);
        }
    }

    private static void fetchPricesForGear(GearInfo gearInfo) {
        String gearName = gearInfo.name();
        if (!fetchedPrices.containsKey(gearName)) {
            TradeMarketItemPriceHolder priceHolder = new TradeMarketItemPriceHolder(FETCHING, gearName);
            fetchedPrices.put(gearName, priceHolder);

            if (gearInfo.metaInfo().restrictions() == GearRestrictions.UNTRADABLE) {
                priceHolder.setPriceInfo(UNTRADABLE);
            } else {
                CompletableFuture.supplyAsync(() -> API.fetchItemPrice(gearName), executorService)
                        .thenAccept(priceHolder::setPriceInfo);
            }
        }

        if (!fetchedHistoricPrices.containsKey(gearName)) {
            TradeMarketItemPriceHolder historicHolder = new TradeMarketItemPriceHolder(FETCHING, gearName);
            fetchedHistoricPrices.put(gearName, historicHolder);

            if (gearInfo.metaInfo().restrictions() == GearRestrictions.UNTRADABLE) {
                historicHolder.setPriceInfo(UNTRADABLE);
            } else {
                CompletableFuture.supplyAsync(() -> API.fetchLatestHistoricGearPrice(gearName), executorService)
                        .thenAccept(historicHolder::setPriceInfo);
            }
        }
    }

    public static void fetchPricesForMaterial(MaterialItem item) {
        String name = getMaterialName(item);
        int tier = item.getQualityTier();
        String materialKey = name + tier;

        fetchedPrices.computeIfAbsent(materialKey, key -> {
            TradeMarketItemPriceHolder holder = new TradeMarketItemPriceHolder(FETCHING, key);

            CompletableFuture
                    .supplyAsync(() -> API.fetchItemPrice(name, tier), executorService)
                    .thenAccept(holder::setPriceInfo);

            return holder;
        });

        fetchedHistoricPrices.computeIfAbsent(materialKey, key -> {
            // create the holder in FETCHING state
            TradeMarketItemPriceHolder holder = new TradeMarketItemPriceHolder(FETCHING, key);

            // asynchronously load the real price
            CompletableFuture
                    .supplyAsync(() -> API.fetchItemPrice(name, tier), executorService)
                    .thenAccept(holder::setPriceInfo);

            return holder;
        });
    }

    public static void fetchPricesForIngredients(IngredientItem item) {
        String name = item.getName();

        fetchedPrices.computeIfAbsent(name, key -> {
            TradeMarketItemPriceHolder holder = new TradeMarketItemPriceHolder(FETCHING, key);

            CompletableFuture
                    .supplyAsync(() -> API.fetchItemPrice(name, item.getQualityTier()), executorService)
                    .thenAccept(holder::setPriceInfo);

            return holder;
        });

        fetchedHistoricPrices.computeIfAbsent(name, key -> {
            // create the holder in FETCHING state
            TradeMarketItemPriceHolder holder = new TradeMarketItemPriceHolder(FETCHING, key);

            // asynchronously load the real price
            CompletableFuture
                    .supplyAsync(() -> API.fetchItemPrice(name, item.getQualityTier()), executorService)
                    .thenAccept(holder::setPriceInfo);

            return holder;
        });
    }

    private static List<Component> getTooltipsForGear(String itemName, ChatFormatting color) {
        TradeMarketItemPriceInfo priceInfo = fetchedPrices.get(itemName).getPriceInfo();
        if (priceInfo == FETCHING) {
            return Collections.singletonList(Component.literal("Retrieving price information...").withStyle(ChatFormatting.WHITE));
        } else if (priceInfo == UNTRADABLE) {
            return Collections.singletonList(Component.literal("Item is untradable.").withStyle(ChatFormatting.RED));
        } else {
            TradeMarketItemPriceInfo historicInfo = fetchedHistoricPrices.get(itemName).getPriceInfo();
            return PriceTooltipHelper.createPriceTooltip(priceInfo, historicInfo, itemName, color);
        }
    }

    private static List<Component> getTooltipsForMaterial(String itemName, int tier) {
        TradeMarketItemPriceInfo priceInfo = fetchedPrices.get(itemName+tier).getPriceInfo();
        if (priceInfo == FETCHING) {
            return Collections.singletonList(Component.literal("Retrieving price information...").withStyle(ChatFormatting.WHITE));
        } else {
            TradeMarketItemPriceInfo historicInfo = fetchedHistoricPrices.get(itemName+tier).getPriceInfo();
            return PriceTooltipHelper.createPriceTooltip(priceInfo, historicInfo, itemName, ChatFormatting.WHITE);
        }
    }

    private static List<Component> getTooltipsForIngredient(String itemName) {
        TradeMarketItemPriceInfo priceInfo = fetchedPrices.get(itemName).getPriceInfo();
        if (priceInfo == FETCHING) {
            return Collections.singletonList(Component.literal("Retrieving price information...").withStyle(ChatFormatting.WHITE));
        } else {
            TradeMarketItemPriceInfo historicInfo = fetchedHistoricPrices.get(itemName).getPriceInfo();
            return PriceTooltipHelper.createPriceTooltip(priceInfo, historicInfo, itemName, ChatFormatting.GRAY);
        }
    }

    public static ChatFormatting getRarityColor(String rarity) {
        return switch (GearTier.fromString(rarity)) {
            case GearTier.MYTHIC -> GearTier.MYTHIC.getChatFormatting();
            case GearTier.FABLED -> GearTier.FABLED.getChatFormatting();
            case GearTier.LEGENDARY -> GearTier.LEGENDARY.getChatFormatting();
            case GearTier.RARE -> GearTier.RARE.getChatFormatting();
            case GearTier.UNIQUE -> GearTier.UNIQUE.getChatFormatting();
            case GearTier.SET -> GearTier.SET.getChatFormatting();
            default -> ChatFormatting.WHITE; // including Common
        };
    }

    private static String getMaterialName(MaterialItem item) {
        String sourceMaterialName = item.getMaterialProfile().getSourceMaterial().name();
        String resourceTypeName = item.getMaterialProfile().getResourceType().name();
        return StringUtils.toCamelCase(sourceMaterialName + " " + resourceTypeName);
    }
}