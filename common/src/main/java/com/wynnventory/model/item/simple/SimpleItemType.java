package com.wynnventory.model.item.simple;

public enum SimpleItemType {
    // SimpleItems
    SIMULATOR("SimulatorItem", "simulator", true, "augment"),
    INSULATOR("InsulatorItem", "insulator", true, "augment"),
    RUNE("RuneItem", "rune", true, "rune"),
    DUNGEON_KEY("DungeonKeyItem", "dungeon_key", true, "dungeon"),
    EMERALD_ITEM("EmeraldItem", "emerald", false, "emerald"),
    ASPECT("AspectItem", "aspect", false, "aspect"),
    TOME("TomeItem", "tome", false, "tome"),
    WARD("WardItem", "ward", true, "ward"),
    GATHERING_TOOL("GatheringToolItem", "gathering_tool", true, "tool"),

    // SimpleTierItems
    INGREDIENT("IngredientItem", "ingredient", true, "ingredient"),
    MATERIAL("MaterialItem", "material", true, "material"),
    POWDER("PowderItem", "powder", true, "powder"),
    AMPLIFIER("AmplifierItem", "amplifier", true, "augment"),
    MOUNT("MountItem", "mount", true, "mount"),
    EMERALD_POUCH("EmeraldPouchItem", "emerald_pouch", true, "pouch"),

    // SimpleGearItems
    GEAR("GearItem", "gear", true, "gear"),
    CHARM("CharmItem", "charm", true, "charm");

    /** Storage vocabulary: what the mod submits and the v1 surface stores. */
    private final String type;
    /** v2 vocabulary: the {@code item_type} label the /api/v2 surface emits. */
    private final String apiLabel;

    private final boolean sellable;
    private final String iconPrefix;

    SimpleItemType(String type, String apiLabel, boolean sellable, String iconPrefix) {
        this.type = type;
        this.apiLabel = apiLabel;
        this.sellable = sellable;
        this.iconPrefix = iconPrefix;
    }

    public String getType() {
        return type;
    }

    public String getApiLabel() {
        return apiLabel;
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

    public static SimpleItemType fromApiLabel(String label) {
        for (SimpleItemType itemType : values()) {
            if (itemType.getApiLabel().equals(label)) return itemType;
        }

        return null;
    }
}
