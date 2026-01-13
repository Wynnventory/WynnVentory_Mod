package com.wynnventory.util;

import com.wynntils.core.components.Models;
import com.wynntils.core.text.StyledText;
import com.wynntils.handlers.item.ItemAnnotation;
import com.wynntils.models.gear.type.GearTier;
import com.wynntils.models.items.WynnItem;
import com.wynntils.models.items.WynnItemData;
import com.wynntils.models.items.items.game.*;
import com.wynnventory.core.WynnventoryMod;
import com.wynnventory.model.item.simple.SimpleGearItem;
import com.wynnventory.model.item.simple.SimpleItem;
import com.wynnventory.model.item.simple.SimpleTierItem;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;

public class ItemStackUtils {

    public static SimpleItem toSimpleItem(ItemStack itemStack) {
        return toSimpleItem(Models.Item.getWynnItem(itemStack).get());
    }

    public static SimpleItem toSimpleItem(WynnItem item) {

        switch (item) {
            case SimulatorItem simulatorItem -> {
                return SimpleItem.fromSimulatorItem(simulatorItem);
            } case InsulatorItem insulatorItem -> {
                return SimpleItem.fromInsulatorItem(insulatorItem);
            } case RuneItem runeItem -> {
                return SimpleItem.fromRuneItem(runeItem);
            } case DungeonKeyItem dungeonKeyItem -> {
                return SimpleItem.fromDungeonKeyItem(dungeonKeyItem);
            } case GearItem gearItem -> {
                return new SimpleGearItem(gearItem);
            } case IngredientItem ingredientItem -> {
                return SimpleTierItem.fromIngredientItem(ingredientItem);
            } case MaterialItem materialItem -> {
                return SimpleTierItem.fromMaterialItem(materialItem);
            } case PowderItem powderItem -> {
                return SimpleTierItem.fromPowderItem(powderItem);
            } case AmplifierItem amplifierItem -> {
                return SimpleTierItem.fromAmplifierItem(amplifierItem);
            } case HorseItem horseItem -> {
                return SimpleTierItem.fromHorseItem(horseItem);
            } case EmeraldPouchItem emeraldPouchItem -> {
                return SimpleTierItem.fromEmeraldPouchItem(emeraldPouchItem);
            }

            default -> {
                WynnventoryMod.logWarn("Unknown item type: " + item.getClass().getSimpleName());
                return new SimpleItem();
            }
        }
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
        String name = ""; //getWynntilsOriginalNameAsString(item);
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
}