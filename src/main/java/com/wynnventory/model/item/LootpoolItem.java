package com.wynnventory.model.item;

import com.wynntils.models.items.WynnItem;
import com.wynntils.models.items.items.game.*;

import java.util.Arrays;
import java.util.List;

public class LootpoolItem {
    private String type;
    private String region;
    private int amount;
    private String name;
    private String rarity;
    private String shiny;
    public static final List<Class<? extends WynnItem>> LOOT_CLASSES = Arrays.asList(
            GearItem.class,
            InsulatorItem.class,
            SimulatorItem.class,
            EmeraldItem.class,
            MiscItem.class,
            RuneItem.class,
            DungeonKeyItem.class
    );

    // Constructor
    public LootpoolItem(String type, String region, int amount, String name, String rarity, String shiny) {
        this.type = type;
        this.region = region;
        this.amount = amount;
        this.name = name;
        this.rarity = rarity;
        this.shiny = shiny;
    }

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}