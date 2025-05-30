package com.wynnventory.enums;

public enum Region {
    CANYON_OF_THE_LOST("COTL", "Canyon of the Lost","󏿲󏽯", RegionType.LOOTRUN),
    CORKUS("Corkus", "Corkus","󏿲󏽯", RegionType.LOOTRUN),
    MOLTEN_HEIGHTS("Molten Heights", "Molten Heights","󏿲󏽯", RegionType.LOOTRUN),
    SKY_ISLANDS("Sky Islands", "Sky Islands","󏿲󏽯", RegionType.LOOTRUN),
    SILENT_EXPANSE("Silent Expanse", "Silent Expanse","󏿲󏽯", RegionType.LOOTRUN),

    NEST_OF_GROOTSLANGS("NOTG", "Nest of the Grootslangs","󏿪󏽯", RegionType.RAID),
    NEXUS_OF_LIGHT("NOL", "Orphion's Nexus of Light","󏿪󏽯", RegionType.RAID),
    CANYON_COLOSSUS("TCC", "The Canyon Colossus","󏿪󏽯", RegionType.RAID),
    NAMELESS_ANOMALY("TNA", "The Nameless Anomaly","󏿪󏽯", RegionType.RAID);

    private final String shortName;
    private final String name;
    private final String inventoryTitle;
    private final RegionType type;

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
