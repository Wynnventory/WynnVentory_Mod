package com.wynnventory.model.item.simple;

public enum SimpleItemType {
    // SimpleItems
    SIMULATOR("SimulatorItem"),
    INSULATOR("InsulatorItem"),
    RUNE("RuneItem"),
    DUNGEON_KEY( "DungeonKeyItem"),
    EMERALD_ITEM("EmeraldItem"),
    ASPECT("AspectItem"),
    TOME("TomeItem"),

    // SimpleTierItems
    INGREDIENT("IngredientItem"),
    MATERIAL("MaterialItem"),
    POWDER("PowderItem"),
    AMPLIFIER("AmplifierItem"),
    HORSE("HorseItem"),
    EMERALD_POUCH("EmeraldPouchItem"),

    // SimpleGearItems
    GEAR("GearItem");

    private final String type;
    SimpleItemType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static SimpleItemType fromType(String type) {
        for (SimpleItemType itemType : values()) {
            if (itemType.getType().equals(type)) return itemType;
        }

        return null;
    }
}
