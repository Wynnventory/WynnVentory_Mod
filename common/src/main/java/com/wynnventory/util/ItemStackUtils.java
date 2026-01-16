package com.wynnventory.util;

import com.wynntils.core.components.Models;
import com.wynntils.core.text.StyledText;
import com.wynntils.models.gear.GearModel;
import com.wynntils.models.gear.type.GearTier;
import com.wynntils.models.items.WynnItem;
import com.wynntils.models.items.WynnItemData;
import com.wynntils.models.items.items.game.*;
import com.wynntils.models.stats.type.StatActualValue;
import com.wynntils.models.stats.type.StatPossibleValues;
import com.wynnventory.core.WynnventoryMod;
import com.wynnventory.model.item.ItemStat;
import com.wynnventory.model.item.simple.SimpleGearItem;
import com.wynnventory.model.item.simple.SimpleItem;
import com.wynnventory.model.item.simple.SimpleTierItem;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ItemStackUtils {

    private ItemStackUtils() { }

    public static SimpleItem toSimpleItem(ItemStack itemStack) {
        return toSimpleItem(Models.Item.getWynnItem(itemStack).get());
    }

    public static SimpleItem toSimpleItem(WynnItem item) {
        return switch (item) {
            case SimulatorItem simulatorItem    -> fromSimulatorItem(simulatorItem);
            case InsulatorItem insulatorItem    -> fromInsulatorItem(insulatorItem);
            case RuneItem runeItem              -> fromRuneItem(runeItem);
            case DungeonKeyItem dungeonKeyItem  -> fromDungeonKeyItem(dungeonKeyItem);
            case GearItem gearItem              -> fromGearItem(gearItem);
            case IngredientItem ingredientItem  -> fromIngredientItem(ingredientItem);
            case MaterialItem materialItem      -> fromMaterialItem(materialItem);
            case PowderItem powderItem          -> fromPowderItem(powderItem);
            case AmplifierItem amplifierItem    -> fromAmplifierItem(amplifierItem);
            case HorseItem horseItem            -> fromHorseItem(horseItem);
            case EmeraldPouchItem emeraldPouchItem -> fromEmeraldPouchItem(emeraldPouchItem);
            case EmeraldItem emeraldItem        -> fromEmeraldItem(emeraldItem);
            case TomeItem tomeItem              -> fromTomeItem(tomeItem);
            case AspectItem aspectItem          -> fromAspectItem(aspectItem);
            default -> {
                yield null;
            }
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

    private static SimpleItem fromSimulatorItem(SimulatorItem item) {
        return createSimpleItem(item, item.getGearTier().getName(), "SimulatorItem", "Simulator");
    }

    private static SimpleItem fromInsulatorItem(InsulatorItem item) {
        return createSimpleItem(item, item.getGearTier().getName(), "InsulatorItem", "Insulator");
    }

    private static SimpleItem fromRuneItem(RuneItem item) {
        return createSimpleItem(item, "RuneItem");
    }

    private static SimpleItem fromDungeonKeyItem(DungeonKeyItem item) {
        return createSimpleItem(item,"DungeonKeyItem");
    }

    private static SimpleItem createSimpleItem(WynnItem item, String itemType) {
        String name = ItemStackUtils.getWynntilsOriginalNameAsString(item);
        return createSimpleItem(item, "Common", itemType, StringUtils.toCamelCase(name));
    }

    private static SimpleItem createSimpleItem(WynnItem item, String rarity, String itemType, String type) {
        String name = ItemStackUtils.getWynntilsOriginalNameAsString(item);
        int amount = ((ItemStack) item.getData().get(WynnItemData.ITEMSTACK_KEY)).getCount();
        return new SimpleItem(name, rarity, itemType, type, IconManager.getIcon(name), amount);
    }

    private static SimpleTierItem fromIngredientItem(IngredientItem item) {
        return createTierItem(item, item.getName(), "Common", "IngredientItem", item.getIngredientInfo().professions().toString(), item.getQualityTier());
    }

    private static SimpleTierItem fromMaterialItem(MaterialItem materialItem) {
        return createTierItem(materialItem, ItemStackUtils.getMaterialName(materialItem), "Common", "MaterialItem", materialItem.getProfessionTypes().toString(), materialItem.getQualityTier());
    }

    private static SimpleTierItem fromPowderItem(PowderItem powderItem) {
        String type = powderItem.getPowderProfile().element().getName() + "Powder";
        return createTierItem(powderItem, ItemStackUtils.getPowderName(powderItem), "Common", "PowderItem", type, powderItem.getTier());
    }

    private static SimpleTierItem fromAmplifierItem(AmplifierItem amplifierItem) {
        return createTierItem(amplifierItem, ItemStackUtils.getAmplifierName(amplifierItem), amplifierItem.getGearTier().getName(), "AmplifierItem", amplifierItem.getTier());
    }

    private static SimpleTierItem fromHorseItem(HorseItem horseItem) {
        return createTierItem(horseItem, ItemStackUtils.getHorseName(horseItem), "Common", "HorseItem", horseItem.getTier().getNumeral());
    }

    private static SimpleTierItem fromEmeraldPouchItem(EmeraldPouchItem emeraldPouchItem) {
        return createTierItem(emeraldPouchItem, "Emerald Pouch", "Common", "EmeraldPouchItem", emeraldPouchItem.getTier());
    }

    private static SimpleItem fromEmeraldItem(EmeraldItem emeraldItem) {
        return createSimpleItem(emeraldItem, "Common", "EmeraldItem", emeraldItem.getUnit().name());
    }

    private static SimpleItem fromTomeItem(TomeItem tomeItem) {
        return createSimpleItem(tomeItem, tomeItem.getGearTier().getName(), "TomeItem", tomeItem.getItemInfo().type().name());
    }

    private static SimpleItem fromAspectItem(AspectItem aspectItem) {
        return createSimpleItem(aspectItem, aspectItem.getGearTier().getName(), "AspectItem", aspectItem.getRequiredClass().getName() + "Aspect");
    }

    private static SimpleTierItem createTierItem(WynnItem item, String name, String rarity, String itemType, int tier) {
        return createTierItem(item, name, rarity, itemType, StringUtils.toCamelCase(name), tier);
    }

    private static SimpleTierItem createTierItem(WynnItem item, String name, String rarity, String itemType, String type, int tier) {
        int amount = ((ItemStack) item.getData().get(WynnItemData.ITEMSTACK_KEY)).getCount();
        return new SimpleTierItem(name, rarity, itemType, type, IconManager.getIcon(name, tier), amount, tier);
    }

    private static SimpleGearItem fromGearItem(GearItem item) {
        String name = item.getName();
        ItemStack itemStack = item.getData().get(WynnItemData.ITEMSTACK_KEY);

        return new SimpleGearItem(
                name,
                item.getGearTier().getName(),
                "GearItem",
                item.getGearType().name(),
                IconManager.getIcon(name),
                itemStack.getCount(),
                item.isUnidentified(),
                item.getRerollCount(),
                new GearModel().parseInstance(item.getItemInfo(), itemStack).shinyStat(),
                item.getOverallPercentage(),
                getActualStats(item)
        );
    }

    private static List<ItemStat> getActualStats(GearItem item) {
        final List<StatActualValue> actualValues = item.getIdentifications();
        final List<StatPossibleValues> possibleValues = item.getPossibleValues();

        return actualValues.stream()
                .map(actual -> possibleValues.stream()
                        .filter(p -> p.statType().getKey().equals(actual.statType().getKey()))
                        .findFirst()
                        .map(possible -> new ItemStat(actual, possible))
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }
}