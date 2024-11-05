package com.wynnventory.model;

public enum Region {
    CANYON_OF_THE_LOST("COTL", "Canyon of the Lost","󏿲", RegionType.LOOTRUN),
    CORKUS("Corkus", "Corkus","󏿲󏽯", RegionType.LOOTRUN),
    MOLTEN_HEIGHTS("Molten Heights", "Molten Heights","󏿲󏽯", RegionType.LOOTRUN),
    SKY_ISLANDS("Sky Islands", "Sky Islands","󏿲󏽯", RegionType.LOOTRUN),
    SILENT_EXPANSE("Silent Expanse", "Silent Expanse","󏿲󏽯", RegionType.LOOTRUN);

    private String shortName;
    private String name;
    private String inventoryTitle;
    private RegionType type;

    Region(String shortName, String name, String inventoryTitle, RegionType type) {
        this.shortName = shortName;
        this.name = name;
        this.inventoryTitle = inventoryTitle;
        this.type = type;
    }

    public static Region getRegionByShortName(String shortName) {
        for (Region region : Region.values()) {
            if (region.getShortName().equals(shortName)) {
                return region;
            }
        }

        return null;
    }

    public static Region getRegionByName(String name) {
        for (Region region : Region.values()) {
            if (region.getName().equals(name)) {
                return region;
            }
        }

        return null;
    }

    public static Region getRegionByInventoryTitle(String inventoryTitle) {
        for (Region region : Region.values()) {
            if (region.getInventoryTitle().equals(inventoryTitle)) {
                return region;
            }
        }

        return null;
    }

    public String getShortName() {
        return shortName;
    }

    public String getName() {
        return name;
    }

    public String getInventoryTitle() {
        return inventoryTitle;
    }

    public RegionType getRegionType() {
        return type;
    }
}
