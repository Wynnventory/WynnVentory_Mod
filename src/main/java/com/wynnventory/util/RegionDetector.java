package com.wynnventory.util;

import com.wynntils.utils.mc.McUtils;
import com.wynnventory.WynnventoryMod;

public class RegionDetector {
    public static final String UNDEFINED_REGION = "undefined region";

    public enum Region {
        // LOOTPOOLS
        SKY("Sky Islands", 900, -4500, 1100, -4300),
        COTL("COTL", 400, -5500, 700, -4800),
        MOLTEN("Molten Heights", 1000, -5300, 1400, -4900),
        CORKUS("Corkus", -1700, -2800, -1400, -2500),
        SE("Silent Expanse", 900, -900, 1100, -700),

        // RAIDPOOLS
        TNA("TNA", 24400, -23900, 24600, -23700),
        TCC("TCC", 10800, 3800, 10850, 4000),
        NOL("NOL", 10900, 2800, 11100, 3000),
        NOTG("NOTG", 10250, 3050, 10400, 3200);


        private final String displayName;
        private final int minX;
        private final int minZ;
        private final int maxX;
        private final int maxZ;

        Region(String displayName, int minX, int minZ, int maxX, int maxZ) {
            this.displayName = displayName;
            this.minX = minX;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxZ = maxZ;
        }

        public boolean contains(int x, int z) {
            return minX <= x && x <= maxX && minZ <= z && z <= maxZ;
        }

        public String getName() {
            return this.displayName;
        }
    }

    public static String getRegion(int x, int z) {
        for (Region region : Region.values()) {
            if (region.contains(x, z)) {
                return region.getName();
            }
        }
        return UNDEFINED_REGION;
    }
}