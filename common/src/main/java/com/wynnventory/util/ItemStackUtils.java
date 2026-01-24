package com.wynnventory.util;

import com.wynntils.core.components.Models;
import com.wynntils.core.text.StyledText;
import com.wynntils.models.gear.type.GearTier;
import com.wynntils.models.items.WynnItem;
import com.wynntils.models.items.WynnItemData;
import com.wynntils.models.items.items.game.*;
import com.wynntils.models.trademarket.type.TradeMarketPriceInfo;
import com.wynntils.utils.mc.LoreUtils;
import com.wynnventory.core.WynnventoryMod;
import com.wynnventory.model.item.simple.SimpleGearItem;
import com.wynnventory.model.item.simple.SimpleItem;
import com.wynnventory.model.item.simple.SimpleTierItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemStackUtils {
    private static final Pattern PRICE_STR = Pattern.compile("§6󏿼󏿿󏿾 Price");
    private static final Pattern PRICE_PATTERN = Pattern.compile(
            "§6󏿼󐀆 (?:§f(?<amount>[\\d,]+) §7x )?§(?:(§f)|f§m|f)(?<price>[\\d,]+)§7(?:§m)?²(?:§b ✮ (?<silverbullPrice>[\\d,]+)§3²)?(?: .+)");

    private ItemStackUtils() { }

    public static SimpleItem toSimpleItem(ItemStack stack) {
        return Models.Item.getWynnItem(stack).map(ItemStackUtils::toSimpleItem).orElse(null);
    }

    public static SimpleItem toSimpleItem(WynnItem item) {
        return switch (item) {
            case AmplifierItem amplifierItem        -> SimpleTierItem.from(amplifierItem);
            case AspectItem aspectItem              -> SimpleItem.from(aspectItem);
            case DungeonKeyItem dungeonKeyItem      -> SimpleItem.from(dungeonKeyItem);
            case EmeraldItem emeraldItem            -> SimpleItem.from(emeraldItem);
            case EmeraldPouchItem emeraldPouchItem  -> SimpleTierItem.from(emeraldPouchItem);
            case GearItem gearItem                  -> SimpleGearItem.from(gearItem);
            case HorseItem horseItem                -> SimpleTierItem.from(horseItem);
            case IngredientItem ingredientItem      -> SimpleTierItem.from(ingredientItem);
            case InsulatorItem insulatorItem        -> SimpleItem.from(insulatorItem);
            case MaterialItem materialItem          -> SimpleTierItem.from(materialItem);
            case PowderItem powderItem              -> SimpleTierItem.from(powderItem);
            case RuneItem runeItem                  -> SimpleItem.from(runeItem);
            case SimulatorItem simulatorItem        -> SimpleItem.from(simulatorItem);
            case TomeItem tomeItem                  -> SimpleTierItem.from(tomeItem);
            case null, default -> null;
        };
    }

    public static StyledText getWynntilsOriginalName(ItemStack itemStack) {
        try {
            Field originalNameField = ItemStack.class.getDeclaredField("wynntilsOriginalName");
            originalNameField.setAccessible(true);
            return (StyledText) originalNameField.get(itemStack);
        } catch (ReflectiveOperationException e) {
            WynnventoryMod.logError("Error retrieving original name", e);
            return null;
        }
    }

    public static Component getWynntilsOriginalNameAsComponent(WynnItem item) {
        return item.getData().get(WynnItemData.ITEMSTACK_KEY);

    }

    public static String getWynntilsOriginalNameAsString(WynnItem item) {
        return Objects.requireNonNull(ItemStackUtils.getWynntilsOriginalName(item.getData().get(WynnItemData.ITEMSTACK_KEY))).getLastPart().getComponent().getString();
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

    public static TradeMarketPriceInfo calculateItemPriceInfo(ItemStack itemStack) {
        List<StyledText> loreLines = LoreUtils.getLore(itemStack);
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
                int silverbullPrice = silverbullPriceStr == null ? price : Integer.parseInt(silverbullPriceStr.replace(",", ""));
                String amountStr = matcher.group("amount");
                int amount = amountStr == null ? 1 : Integer.parseInt(amountStr.replace(",", ""));
                return new TradeMarketPriceInfo(price, silverbullPrice, amount);
            }
        } else {
            WynnventoryMod.logWarn("Trade Market item had an unexpected price line: " + priceLine);
            return TradeMarketPriceInfo.EMPTY;
        }
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
        return StringUtils.toCamelCase(item.getTier().name()) + " Horse";
    }
}