package com.wynnventory.model.item.simple;

public enum SimpleItemType {
    // SimpleItems
    SIMULATOR("SimulatorItem", true, "augment"),
    INSULATOR("InsulatorItem", true, "augment"),
    RUNE("RuneItem", true, "rune"),
    DUNGEON_KEY("DungeonKeyItem", true, "dungeon"),
    EMERALD_ITEM("EmeraldItem", false, "emerald"),
    ASPECT("AspectItem", false, "aspect"),
    TOME("TomeItem", false, "tome"),

    // SimpleTierItems
    INGREDIENT("IngredientItem", true, "ingredient"),
    MATERIAL("MaterialItem", true, "material"),
    POWDER("PowderItem", true, "powder"),
    AMPLIFIER("AmplifierItem", true, "augment"),
    MOUNT("MountItem", true, "mount"),
    EMERALD_POUCH("EmeraldPouchItem", true, "pouch"),

    // SimpleGearItems
    GEAR("GearItem", true, "gear");

    private final String type;
    private final boolean sellable;
    private final String iconPrefix;

    SimpleItemType(String type, boolean sellable, String iconPrefix) {
        this.type = type;
        this.sellable = sellable;
        this.iconPrefix = iconPrefix;
    }

    public String getType() {
        return type;
    }

    public boolean isSellable() {
        return sellable;
    }

    public String getIconPrefix() {
        return this.iconPrefix;
    }

    public static SimpleItemType fromType(String type) {
        for (SimpleItemType itemType : values()) {
            if (itemType.getType().equals(type)) return itemType;
        }

        return null;
    }
}
