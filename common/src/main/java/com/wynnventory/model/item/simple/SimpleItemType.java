package com.wynnventory.model.item.simple;

public enum SimpleItemType {
    // SimpleItems
    SIMULATOR("SimulatorItem", true),
    INSULATOR("InsulatorItem", true),
    RUNE("RuneItem", true),
    DUNGEON_KEY("DungeonKeyItem", true),
    EMERALD_ITEM("EmeraldItem", false),
    ASPECT("AspectItem", false),
    TOME("TomeItem", false),

    // SimpleTierItems
    INGREDIENT("IngredientItem", true),
    MATERIAL("MaterialItem", true),
    POWDER("PowderItem", true),
    AMPLIFIER("AmplifierItem", true),
    HORSE("HorseItem", true),
    EMERALD_POUCH("EmeraldPouchItem", true),

    // SimpleGearItems
    GEAR("GearItem", true);

    private final String type;
    private final boolean sellable;

    SimpleItemType(String type, boolean sellable) {
        this.type = type;
        this.sellable = sellable;
    }

    public String getType() {
        return type;
    }

    public boolean isSellable() {
        return sellable;
    }

    public static SimpleItemType fromType(String type) {
        for (SimpleItemType itemType : values()) {
            if (itemType.getType().equals(type)) return itemType;
        }

        return null;
    }
}
