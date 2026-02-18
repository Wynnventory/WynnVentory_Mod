package com.wynnventory.util;

import com.wynntils.core.components.Models;
import com.wynntils.core.text.StyledText;
import com.wynntils.models.gear.type.GearTier;
import com.wynntils.models.items.WynnItem;
import com.wynntils.models.items.WynnItemData;
import com.wynntils.models.items.items.game.AmplifierItem;
import com.wynntils.models.items.items.game.AspectItem;
import com.wynntils.models.items.items.game.DungeonKeyItem;
import com.wynntils.models.items.items.game.EmeraldItem;
import com.wynntils.models.items.items.game.EmeraldPouchItem;
import com.wynntils.models.items.items.game.GearItem;
import com.wynntils.models.items.items.game.HorseItem;
import com.wynntils.models.items.items.game.IngredientItem;
import com.wynntils.models.items.items.game.InsulatorItem;
import com.wynntils.models.items.items.game.MaterialItem;
import com.wynntils.models.items.items.game.PowderItem;
import com.wynntils.models.items.items.game.RuneItem;
import com.wynntils.models.items.items.game.SimulatorItem;
import com.wynntils.models.items.items.game.TomeItem;
import com.wynntils.models.trademarket.type.TradeMarketPriceInfo;
import com.wynntils.utils.mc.LoreUtils;
import com.wynnventory.core.WynnventoryMod;
import com.wynnventory.model.item.simple.SimpleGearItem;
import com.wynnventory.model.item.simple.SimpleItem;
import com.wynnventory.model.item.simple.SimpleTierItem;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.ItemStack;

public class ItemStackUtils {
    private static final Pattern PRICE_STR = Pattern.compile("§6󏿼󏿿󏿾 Price");
    private static final Pattern PRICE_PATTERN = Pattern.compile(
            "§6󏿼󐀆 (?:§f(?<amount>[\\d,]+) §7x )?§(?:(§f)|f§m|f)(?<price>[\\d,]+)§7(?:§m)?²(?:§b ✮ (?<silverbullPrice>[\\d,]+)§3²)?(?: .+)");

    private ItemStackUtils() {}

    public static SimpleItem toSimpleItem(ItemStack stack) {
        return toSimpleItem(ItemStackUtils.getWynnItem(stack));
    }

    public static SimpleItem toSimpleItem(WynnItem item) {
        return switch (item) {
            case AmplifierItem amplifierItem -> SimpleTierItem.from(amplifierItem);
            case AspectItem aspectItem -> SimpleItem.from(aspectItem);
            case DungeonKeyItem dungeonKeyItem -> SimpleItem.from(dungeonKeyItem);
            case EmeraldItem emeraldItem -> SimpleItem.from(emeraldItem);
            case EmeraldPouchItem emeraldPouchItem -> SimpleTierItem.from(emeraldPouchItem);
            case GearItem gearItem -> SimpleGearItem.from(gearItem);
            case HorseItem horseItem -> SimpleTierItem.from(horseItem);
            case IngredientItem ingredientItem -> SimpleTierItem.from(ingredientItem);
            case InsulatorItem insulatorItem -> SimpleItem.from(insulatorItem);
            case MaterialItem materialItem -> SimpleTierItem.from(materialItem);
            case PowderItem powderItem -> SimpleTierItem.from(powderItem);
            case RuneItem runeItem -> SimpleItem.from(runeItem);
            case SimulatorItem simulatorItem -> SimpleItem.from(simulatorItem);
            case TomeItem tomeItem -> SimpleItem.from(tomeItem);
            case null, default -> null;
        };
    }

    public static StyledText getWynntilsOriginalName(ItemStack stack) {
        try {
            Field originalNameField = ItemStack.class.getDeclaredField("wynntilsOriginalName");
            originalNameField.setAccessible(true);
            return (StyledText) originalNameField.get(stack);
        } catch (ReflectiveOperationException e) {
            WynnventoryMod.logError("Error retrieving original name", e);
            return null;
        }
    }

    public static String getWynntilsOriginalNameAsString(WynnItem item) {
        return Objects.requireNonNull(
                        ItemStackUtils.getWynntilsOriginalName(item.getData().get(WynnItemData.ITEMSTACK_KEY)))
                .getLastPart()
                .getComponent()
                .getString();
    }

    public static WynnItem getWynnItem(ItemStack stack) {
        Optional<WynnItem> optionalWynnItem = Models.Item.getWynnItem(stack);
        return optionalWynnItem.orElse(null);
    }

    public static String getMaterialName(MaterialItem item) {
        String source = item.getMaterialProfile().getSourceMaterial().name();
        String resource = item.getMaterialProfile().getResourceType().name();
        return StringUtils.toCamelCase(source + " " + resource, " ");
    }

    public static String getPowderName(PowderItem item) {
        return item.getPowderProfile().element().getName() + " Powder";
    }

    public static TradeMarketPriceInfo calculateItemPriceInfo(ItemStack stack) {
        List<StyledText> loreLines = LoreUtils.getLore(stack);
        if (loreLines.size() < 2) return TradeMarketPriceInfo.EMPTY;
        StyledText priceLine = loreLines.get(1);
        if (priceLine != null && priceLine.matches(PRICE_STR)) {
            StyledText priceValueLine = loreLines.get(2);
            Matcher matcher = priceValueLine.getMatcher(PRICE_PATTERN);
            if (!matcher.matches()) {
                WynnventoryMod.logWarn("Trade Market item had an unexpected price value line: " + priceValueLine);
                return TradeMarketPriceInfo.EMPTY;
            } else {
                int price = Integer.parseInt(matcher.group("price").replace(",", ""));
                String silverbullPriceStr = matcher.group("silverbullPrice");
                int silverbullPrice =
                        silverbullPriceStr == null ? price : Integer.parseInt(silverbullPriceStr.replace(",", ""));
                String amountStr = matcher.group("amount");
                int amount = amountStr == null ? 1 : Integer.parseInt(amountStr.replace(",", ""));
                return new TradeMarketPriceInfo(price, silverbullPrice, amount);
            }
        } else {
            WynnventoryMod.logWarn("Trade Market item had an unexpected price line: " + priceLine);
            return TradeMarketPriceInfo.EMPTY;
        }
    }

    public static String getAmplifierName(AmplifierItem item) {
        String name = getWynntilsOriginalNameAsString(item);
        String[] nameParts = name.split(" ");

        if (nameParts.length > 1) {
            return nameParts[0] + " " + nameParts[1];
        }

        return name;
    }

    public static String getHorseName(HorseItem item) {
        return StringUtils.toCamelCase(item.getTier().name()) + " Horse";
    }

    public static ChatFormatting getRarityChatFormattingByName(String rarity) {
        return Optional.ofNullable(GearTier.fromString(rarity))
                .map(GearTier::getChatFormatting)
                .orElse(ChatFormatting.WHITE);
    }
}
