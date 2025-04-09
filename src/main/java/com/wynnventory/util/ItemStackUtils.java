package com.wynnventory.util;

import com.wynntils.core.components.Models;
import com.wynntils.core.text.StyledText;
import com.wynntils.models.gear.type.GearInfo;
import com.wynntils.models.gear.type.GearRestrictions;
import com.wynntils.models.gear.type.GearTier;
import com.wynntils.models.items.WynnItem;
import com.wynntils.models.items.WynnItemData;
import com.wynntils.models.items.items.game.GearBoxItem;
import com.wynntils.models.items.items.game.GearItem;
import com.wynnventory.WynnventoryMod;
import com.wynnventory.api.WynnventoryAPI;
import com.wynnventory.core.ModInfo;
import com.wynnventory.model.item.TradeMarketItemPriceHolder;
import com.wynnventory.model.item.TradeMarketItemPriceInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentMap;
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

    public static WynnItem getWynntilsAnnotation(ItemStack itemStack) {
        try {
            Field wynntilsAnnotation = ItemStack.class.getDeclaredField("wynntilsAnnotation");
            wynntilsAnnotation.setAccessible(true);

            return (WynnItem) wynntilsAnnotation.get(itemStack);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            ModInfo.logError("Error trying to get wynntilsAnnotation.", e);
            return null;
        }
    }

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

    public static WynnItem getWynnItem(ItemStack itemStack) {
        WynnItem wynnItem = getWynntilsAnnotation(itemStack);
        assert wynnItem != null;
        ItemStack item = wynnItem.getData().get(WynnItemData.ITEMSTACK_KEY);
        return ItemStackUtils.getWynntilsAnnotation(item);
    }

    public static List<Component> getTooltips(ItemStack itemStack) {
        Optional<WynnItem> maybeWynnItem = Models.Item.getWynnItem(itemStack);
        List<Component> tooltipComponents = new ArrayList<>();

        if(maybeWynnItem.isPresent()) {

            WynnItem wynnItem = maybeWynnItem.get();
            switch (wynnItem) {
                case GearItem gearItem -> {
                    processGearTooltip(gearItem.getItemInfo(), tooltipComponents);
                }
                case GearBoxItem gearBoxItem -> {
                    processGearBoxTooltip(gearBoxItem, tooltipComponents);
                } default -> {
                    return tooltipComponents;
                }
            }
        }

        tooltipComponents.addFirst(Component.literal(TITLE_TEXT).withStyle(ChatFormatting.GOLD));

        return tooltipComponents;
    }

    private static void processGearTooltip(GearInfo gearInfo, List<Component> tooltipComponents) {
        fetchPricesForGear(gearInfo);
        tooltipComponents.addAll(getTooltipsForGear(gearInfo));
        cleanExpiredPrices(gearInfo.name());
    }

    private static void processGearBoxTooltip(GearBoxItem gearBoxItem, List<Component> tooltipComponents) {
        List<GearInfo> possibleGears = Models.Gear.getPossibleGears(gearBoxItem);
        List<TradeMarketItemPriceHolder> priceHolders = new ArrayList<>();
        for (GearInfo gear : possibleGears) {
            fetchPricesForGear(gear);
            priceHolders.add(fetchedPrices.get(gear.name()));
        }

        PriceTooltipHelper.sortTradeMarketPriceHolders(priceHolders);
        for (TradeMarketItemPriceHolder holder : priceHolders) {
            GearInfo gearInfo = holder.getInfo();
            tooltipComponents.addAll(getTooltipsForGear(gearInfo));
            tooltipComponents.add(Component.literal("")); // Spacer
            cleanExpiredPrices(gearInfo.name());
        }
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
            TradeMarketItemPriceHolder priceHolder = new TradeMarketItemPriceHolder(FETCHING, gearInfo);
            fetchedPrices.put(gearName, priceHolder);

            if (gearInfo.metaInfo().restrictions() == GearRestrictions.UNTRADABLE) {
                priceHolder.setPriceInfo(UNTRADABLE);
            } else {
                CompletableFuture.supplyAsync(() -> API.fetchItemPrices(gearName), executorService)
                        .thenAccept(priceHolder::setPriceInfo);
            }
        }

        if (!fetchedHistoricPrices.containsKey(gearName)) {
            TradeMarketItemPriceHolder historicHolder = new TradeMarketItemPriceHolder(FETCHING, gearInfo);
            fetchedHistoricPrices.put(gearName, historicHolder);

            if (gearInfo.metaInfo().restrictions() == GearRestrictions.UNTRADABLE) {
                historicHolder.setPriceInfo(UNTRADABLE);
            } else {
                CompletableFuture.supplyAsync(() -> API.fetchLatestHistoricItemPrice(gearName), executorService)
                        .thenAccept(historicHolder::setPriceInfo);
            }
        }
    }

    private static List<Component> getTooltipsForGear(GearInfo gearInfo) {
        TradeMarketItemPriceInfo priceInfo = fetchedPrices.get(gearInfo.name()).getPriceInfo();
        if (priceInfo == FETCHING) {
            return Collections.singletonList(Component.literal("Retrieving price information...").withStyle(ChatFormatting.WHITE));
        } else if (priceInfo == UNTRADABLE) {
            return Collections.singletonList(Component.literal("Item is untradable.").withStyle(ChatFormatting.RED));
        } else {
            TradeMarketItemPriceInfo historicInfo = fetchedHistoricPrices.get(gearInfo.name()).getPriceInfo();
            return PriceTooltipHelper.createPriceTooltip(gearInfo, priceInfo, historicInfo);
        }
    }

    public static ChatFormatting getRarityColor(String rarity) {
        return switch (GearTier.fromString(rarity)) {
            case GearTier.MYTHIC -> ChatFormatting.DARK_PURPLE;
            case GearTier.FABLED -> ChatFormatting.RED;
            case GearTier.LEGENDARY -> ChatFormatting.AQUA;
            case GearTier.RARE -> ChatFormatting.LIGHT_PURPLE;
            case GearTier.UNIQUE -> ChatFormatting.YELLOW;
            case GearTier.SET -> ChatFormatting.GREEN;
            default -> ChatFormatting.WHITE; // including Common
        };
    }
}