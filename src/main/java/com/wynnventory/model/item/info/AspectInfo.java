package com.wynnventory.model.item.info;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wynntils.models.character.type.ClassType;
import com.wynntils.models.gear.type.GearTier;
import com.wynntils.models.wynnitem.type.ItemMaterial;
import java.util.HashMap;
import java.util.Map;

public record AspectInfo(
        @JsonProperty("name")
        String name,

        @JsonProperty("requiredClass")
        ClassType classType,

        @JsonProperty("rarity")
        GearTier gearTier,

        @JsonProperty("tiers")
        Map<Integer, AspectTierInfo> tiers,

        @JsonProperty("icon")
        ItemMaterial material
) {
    @JsonCreator
    public AspectInfo(String name, String requiredClass, String rarity, Map<String, AspectTierInfo> tiers, ItemMaterial material) {
        this(name, ClassType.fromName(requiredClass), GearTier.fromString(rarity), convertTierKeys(tiers), material);
    }

    private static Map<Integer, AspectTierInfo> convertTierKeys(Map<String, AspectTierInfo> tiers) {
        Map<Integer, AspectTierInfo> tierMap = new HashMap<>();
        for (Map.Entry<String, AspectTierInfo> entry : tiers.entrySet()) {
            int tierNumber = Integer.parseInt(entry.getKey());
            tierMap.put(tierNumber, entry.getValue());
        }

        return tierMap;
    }
}
