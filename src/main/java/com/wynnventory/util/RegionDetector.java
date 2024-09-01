package com.wynnventory.util;

public class RegionDetector {

    public enum Region {
        SKY("Sky Islands", 1026, -4421, 1040, -4409),
        COTL("COTL", 583, -5028, 590, -5015),
        MOLTEN("Molten Heights", 1265, -5131, 1278, -5123),
        CORKUS("Corkus", -1556, -2678, -1546, -2668),
        SE("Silent Expanse", 992, -792, 1005, -782);

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
        return "undefined region";
    }
}