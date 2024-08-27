package com.wynnventory.model.item;

import com.wynntils.models.items.WynnItem;
import com.wynntils.models.items.WynnItemData;
import com.wynntils.models.items.items.game.*;
import com.wynntils.models.items.properties.GearTierItemProperty;
import com.wynntils.utils.mc.McUtils;
import com.wynnventory.WynnventoryMod;
import com.wynnventory.util.ItemStackUtils;
import com.wynnventory.util.RegionDetector;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class LootpoolItem {
    private String itemType;
    private String region;
    private int amount;
    private String name;
    private String rarity;
    private String shiny;
    private String type;
    private String playerName;
    private String modVersion;
    public static final List<Class<? extends WynnItem>> LOOT_CLASSES = Arrays.asList(
            GearItem.class,
            InsulatorItem.class,
            SimulatorItem.class,
            EmeraldItem.class,
            MiscItem.class,
            RuneItem.class,
            DungeonKeyItem.class
    );

    public LootpoolItem(String itemType, String region, int amount, String name, String rarity, String shiny, String type, String playerName) {
        this.itemType = itemType;
        this.region = region;
        this.amount = amount;
        this.name = name;
        this.rarity = rarity;
        this.shiny = shiny;
        this.type = type;
        this.playerName = playerName;
        this.modVersion = WynnventoryMod.WYNNVENTORY_VERSION;
    }

    public static List<LootpoolItem> createLootpoolItems(List<ItemStack> items) {
        List<LootpoolItem> lootpoolItems = new ArrayList<>();
        String region = RegionDetector.getRegion(McUtils.player().getBlockX(), McUtils.player().getBlockZ());

        for (ItemStack item : items) {
            Optional<WynnItem> wynnItemOptional = Optional.ofNullable(ItemStackUtils.getWynnItem(item));

            wynnItemOptional.ifPresent(wynnItem -> {
                if (LootpoolItem.LOOT_CLASSES.contains(wynnItem.getClass())) {
                    String shiny = null;
                    String name = ItemStackUtils.getWynntilsOriginalName(wynnItem.getData().get(WynnItemData.ITEMSTACK_KEY)).getLastPart().getComponent().getString();
                    String rarity = null;
                    String type = null;

                    if (wynnItem instanceof GearItem gearItem) {
                        if (name.contains("Shiny")) {
                            shiny = "Shiny";
                        }
                        name = gearItem.getName();
                        rarity = gearItem.getGearTier().getName();
                        type = gearItem.getGearType().name();
                    }
                    if (wynnItem instanceof SimulatorItem || wynnItem instanceof InsulatorItem) {
                        rarity = ((GearTierItemProperty) wynnItem).getGearTier().getName();
                    }

                    LootpoolItem lootpoolItem = new LootpoolItem(
                            wynnItem.getClass().getSimpleName(),
                            region,
                            ((ItemStack) wynnItem.getData().get(WynnItemData.ITEMSTACK_KEY)).getCount(),
                            name,
                            rarity,
                            shiny,
                            type,
                            McUtils.playerName()
                    );

                    lootpoolItems.add(lootpoolItem);
                } else {
                    WynnventoryMod.error("Unknown class: " + wynnItem.getClass());
                }
            });
        }
        return lootpoolItems;
    }

    public static Optional<LootpoolItem> createLootpoolItem(ItemStack item) {
        List<LootpoolItem> items = createLootpoolItems(List.of(item));
        return items.isEmpty() ? Optional.empty() : Optional.of(items.getFirst());
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public String getShiny() {
        return shiny;
    }

    public void setShiny(String shiny) {
        this.shiny = shiny;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getModVersion() { return modVersion; }
}