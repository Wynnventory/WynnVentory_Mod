package com.wynnventory.util;

import com.wynntils.core.components.Models;
import com.wynntils.core.text.StyledText;
import com.wynntils.models.gear.type.GearInfo;
import com.wynntils.models.gear.type.GearRestrictions;
import com.wynntils.models.gear.type.GearTier;
import com.wynntils.models.gear.type.GearType;
import com.wynntils.models.ingredients.type.IngredientTierFormatting;
import com.wynntils.models.items.WynnItem;
import com.wynntils.models.items.WynnItemData;
import com.wynntils.models.items.items.game.*;
import com.wynntils.utils.MathUtils;
import com.wynnventory.api.WynnventoryAPI;
import com.wynnventory.core.ModInfo;
import com.wynnventory.model.item.trademarket.TradeMarketItemPriceHolder;
import com.wynnventory.model.item.trademarket.TradeMarketItemPriceInfo;
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

    public static String getWynntilsOriginalNameAsString(WynnItem item) {
        return Objects.requireNonNull(ItemStackUtils.getWynntilsOriginalName(item.getData().get(WynnItemData.ITEMSTACK_KEY))).getLastPart().getComponent().getString();
    }

    public static List<Component> getTooltips(ItemStack itemStack) {
        Optional<WynnItem> maybeItem = Models.Item.getWynnItem(itemStack);
        List<Component> tooltipLines = new ArrayList<>();

        if (maybeItem.isEmpty()) return tooltipLines;
        switch (maybeItem.get()) {
            case GearItem gearItem ->
                    processSimple(gearItem.getName(), gearItem.getGearTier().getChatFormatting(), tooltipLines, gearItem.getItemInfo().metaInfo().restrictions() == GearRestrictions.UNTRADABLE);
            case GearBoxItem gearBoxItem when !gearBoxItem.getGearType().equals(GearType.MASTERY_TOME) ->
                    processGearBox(gearBoxItem, tooltipLines);
            case IngredientItem ingredientItem ->
                    processCrafting(ingredientItem.getName(), ingredientItem.getQualityTier(), ChatFormatting.GRAY, tooltipLines);
            case MaterialItem materialItem ->
                    processCrafting(getMaterialName(materialItem), materialItem.getQualityTier(), ChatFormatting.WHITE, tooltipLines);
            case PowderItem powderItem ->
                    processTiered(getPowderName(powderItem), powderItem.getTier(), powderItem.getPowderProfile().element().getLightColor(), tooltipLines);
            case AmplifierItem amplifierItem ->
                    processTiered(getAmplifierName(amplifierItem), amplifierItem.getTier(), amplifierItem.getGearTier().getChatFormatting(), tooltipLines);
            case HorseItem horseItem ->
                    processTiered(getHorseName(horseItem), horseItem.getTier().getNumeral(), GearTier.NORMAL.getChatFormatting(), tooltipLines);
            case EmeraldPouchItem emeraldPouchItem ->
                    processTiered(emeraldPouchItem.getName(), emeraldPouchItem.getName() + " " + emeraldPouchItem.getTier(), emeraldPouchItem.getTier(), GearTier.NORMAL.getChatFormatting(), tooltipLines);
            case InsulatorItem insulatorItem ->
                processSimple(ItemStackUtils.getWynntilsOriginalNameAsString(insulatorItem), insulatorItem.getGearTier().getChatFormatting(), tooltipLines);
            case SimulatorItem simulatorItem ->
                processSimple(ItemStackUtils.getWynntilsOriginalNameAsString(simulatorItem), simulatorItem.getGearTier().getChatFormatting(), tooltipLines);
            case RuneItem runeItem ->
                    processSimple(ItemStackUtils.getWynntilsOriginalNameAsString(runeItem), GearTier.NORMAL.getChatFormatting(), tooltipLines);
            case DungeonKeyItem dungeonKeyItem ->
                    processSimple(ItemStackUtils.getWynntilsOriginalNameAsString(dungeonKeyItem), GearTier.NORMAL.getChatFormatting(), tooltipLines);
            default -> {
                return tooltipLines;
            }
        }

        return tooltipLines;
    }

    private static void processSimple(String itemName, ChatFormatting color, List<Component> tooltipLines) {
        processSimple(itemName, color, tooltipLines, false);
    }

    private static void processSimple(String itemName, ChatFormatting color, List<Component> tooltipLines, boolean untradable) {
        tooltipLines.addFirst(Component.literal(TITLE_TEXT).withStyle(ChatFormatting.GOLD));
        fetchPrices(itemName,
                () -> wynnventoryAPI.fetchItemPrice(itemName),
                () -> untradable ? UNTRADABLE : wynnventoryAPI.fetchLatestHistoricItemPrice(itemName)
        );
        tooltipLines.addAll(createTooltip(itemName, color, untradable));
        removeExpiredPrices(itemName);
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
            tooltipLines.addAll(createTooltip(itemKey, color, false));
            tooltipLines.add(Component.literal("")); // spacer
            removeExpiredPrices(itemKey);
        }
    }

    private static void processCrafting(String displayName, int tier, ChatFormatting color, List<Component> tooltipLines) {
        tooltipLines.addFirst(Component.literal(TITLE_TEXT).withStyle(ChatFormatting.GOLD));

        String itemKey = displayName + "_" + tier;
        fetchPrices(itemKey,
                () -> wynnventoryAPI.fetchItemPrice(displayName, tier),
                () -> wynnventoryAPI.fetchLatestHistoricItemPrice(displayName, tier));

        String name = displayName;
        if (tier > 0) {
            name = displayName + " " + IngredientTierFormatting.fromTierNum(tier).getTierString();
        }

        tooltipLines.addAll(createTooltip(name, itemKey, color, false));
        removeExpiredPrices(itemKey);
    }

    private static void processTiered(String displayName, int tier, ChatFormatting color, List<Component> tooltipLines) {
        processTiered(displayName + " " + MathUtils.toRoman(tier), displayName + " " + tier, tier, color, tooltipLines);
    }

    private static void processTiered(String displayName, String key, int tier, ChatFormatting color, List<Component> tooltipLines) {
        tooltipLines.addFirst(Component.literal(TITLE_TEXT).withStyle(ChatFormatting.GOLD));

        fetchPrices(key,
                () -> wynnventoryAPI.fetchItemPrice(displayName, tier),
                () -> wynnventoryAPI.fetchLatestHistoricItemPrice(displayName, tier));

        tooltipLines.addAll(createTooltip(displayName, key, color, false));
        removeExpiredPrices(key);
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

    private static List<Component> createTooltip(String itemKey, ChatFormatting color, boolean untradable) {
        return createTooltip(itemKey, itemKey, color, untradable);
    }

    private static List<Component> createTooltip(String displayName, String itemKey, ChatFormatting color, boolean untradable) {
        PriceHolderPair holders = priceCache.get(itemKey);
        TradeMarketItemPriceInfo currentInfo = holders.currentHolder.getPriceInfo();

        if (untradable || currentInfo == UNTRADABLE) {
            return Collections.singletonList(Component.literal("Item is untradable.").withStyle(ChatFormatting.RED));
        }
        if (currentInfo == FETCHING) {
            return Collections.singletonList(Component.literal("Retrieving price information...").withStyle(ChatFormatting.WHITE));
        }

        TradeMarketItemPriceInfo historicInfo = holders.historicHolder.getPriceInfo();
        return PriceTooltipHelper.createPriceTooltip(currentInfo, historicInfo, displayName, color);
    }

    private static void removeExpiredPrices(String itemKey) {
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

    public static String getMaterialName(MaterialItem item) {
        String source = item.getMaterialProfile().getSourceMaterial().name();
        String resource = item.getMaterialProfile().getResourceType().name();
        return StringUtils.toCamelCase(source + " " + resource, " ");
    }

    public static String getPowderName(PowderItem item) {
        return item.getPowderProfile().element().getName() + " Powder";
    }

    public static String getPowderType(PowderItem item) {
        return StringUtils.toCamelCase(getPowderName(item));
    }

    public static String getAmplifierName(AmplifierItem item) {
        String name = getWynntilsOriginalNameAsString(item);
        String[] nameParts = name.split(" ");

        if (nameParts.length > 1) {
            return nameParts[0] + " " + nameParts[1];
        }

        return name;
    }

    public static String getAmplifierType(AmplifierItem item) {
        return StringUtils.toCamelCase(getAmplifierName(item));
    }

    public static String getHorseName(HorseItem item) {
        // manually building the name as item.getName() would return the nickname if present
        return StringUtils.toCamelCase(item.getTier().name()) + " Horse";
    }

    private record PriceHolderPair(String itemKey, TradeMarketItemPriceHolder currentHolder,
                                   TradeMarketItemPriceHolder historicHolder) {

    }
}