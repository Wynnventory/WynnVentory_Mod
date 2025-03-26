package com.wynnventory.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.wynntils.models.character.type.ClassType;
import com.wynntils.models.gear.type.GearTier;
import com.wynntils.models.wynnitem.type.ItemMaterial;
import com.wynnventory.WynnventoryMod;
import com.wynnventory.model.item.info.AspectInfo;
import com.wynnventory.model.item.info.AspectTierInfo;
import com.wynnventory.util.HttpUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.Unbreakable;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.*;

public class WynncraftAPI {
    private static final String BASE_URL = "https://api.wynncraft.com/v3/";

    public Map<String, AspectInfo> fetchAllAspects() {
        Map<String, AspectInfo> aspects = new HashMap<>();

        try {
            List<ClassType> types = new ArrayList<>(List.of(ClassType.values()));
            types.remove(ClassType.NONE);

            for (ClassType type : types) {
                URI endpointUri = getEndpointURI("aspects/" + type.getName().toLowerCase());

                HttpResponse<String> response = HttpUtil.sendHttpGetRequest(endpointUri);

                if (response.statusCode() == 200) {
                    aspects.putAll(parseAspectResults(response.body()));
                } else {
                    WynnventoryMod.error(response.statusCode() + " - Failed to fetch " + type + " lootpools: " + response.body());
                }
            }
        } catch (Exception e) {
            WynnventoryMod.error("Failed to initiate aspect fetch {}", e);
        }

        return aspects;
    }

    private Map<String, AspectInfo> parseAspectResults(String response) {
        try {
            // Initialize ObjectMapper
            ObjectMapper mapper = new ObjectMapper();

            // Parse the JSON into a JsonNode tree
            JsonNode rootNode = mapper.readTree(response);

            // Create a map to hold the AspectInfo objects
            Map<String, AspectInfo> aspectInfoMap = new HashMap<>();

            // Iterate over each aspect in the root JSON object
            Iterator<Map.Entry<String, JsonNode>> aspects = rootNode.fields();
            while (aspects.hasNext()) {
                Map.Entry<String, JsonNode> aspectEntry = aspects.next();
                String aspectKey = aspectEntry.getKey();
                JsonNode aspectNode = aspectEntry.getValue();

                // Extract the "name" field
                String name = aspectNode.get("name").asText();

                // Extract and map "requiredClass" to ClassType
                String requiredClassStr = aspectNode.get("requiredClass").asText();
                ClassType classType = ClassType.fromName(requiredClassStr);

                // Extract and map "rarity" to GearTier
                String rarityStr = aspectNode.get("rarity").asText();
                GearTier gearTier = GearTier.fromString(rarityStr);

                // Extract "tiers" and parse AspectTierInfo objects
                JsonNode tiersNode = aspectNode.get("tiers");
                Map<Integer, AspectTierInfo> tiersMap = parseTiers(tiersNode);

                // Extract "icon" and parse ItemMaterial
                JsonNode iconNode = aspectNode.get("icon");
                ItemMaterial material = parseItemMaterial(iconNode);

                // Create AspectInfo object
                AspectInfo aspectInfo = new AspectInfo(
                        name,
                        classType,
                        gearTier,
                        tiersMap,
                        material
                );

                // Add the AspectInfo to the map
                aspectInfoMap.put(aspectKey, aspectInfo);
            }

            /*// Example usage: print the parsed aspects
            for (Map.Entry<String, AspectInfo> entry : aspectInfoMap.entrySet()) {
                printAspectInfo(entry.getValue());
            }*/

            return aspectInfoMap;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new HashMap<>();
    }

    private static Map<Integer, AspectTierInfo> parseTiers(JsonNode tiersNode) {
        Map<Integer, AspectTierInfo> tiersMap = new HashMap<>();

        Iterator<Map.Entry<String, JsonNode>> tiers = tiersNode.fields();
        while (tiers.hasNext()) {
            Map.Entry<String, JsonNode> tierEntry = tiers.next();
            int tierNumber = Integer.parseInt(tierEntry.getKey());
            JsonNode tierNode = tierEntry.getValue();

            // Extract "threshold"
            int threshold = tierNode.get("threshold").asInt();

            // Extract "description" (list of strings)
            List<String> description = new ArrayList<>();
            JsonNode descriptionNode = tierNode.get("description");
            if (descriptionNode.isArray()) {
                for (JsonNode desc : descriptionNode) {
                    description.add(desc.asText());
                }
            }

            // Create AspectTierInfo object
            AspectTierInfo tierInfo = new AspectTierInfo(threshold, description);

            // Add to the tiers map
            tiersMap.put(tierNumber, tierInfo);
        }

        return tiersMap;
    }

    private static ItemMaterial parseItemMaterial(JsonNode iconNode) {
        if (iconNode == null || iconNode.isNull()) {
            return null;
        }

        // Extract "value" and "format"
        JsonNode valueNode = iconNode.get("value");
        String format = iconNode.get("format").asText();

        // Extract fields from "value"
        String id = valueNode.get("id").asText();
        String name = valueNode.get("name").asText();
        int customModelData = valueNode.get("customModelData").asInt();

        // Placeholder code (replace with actual implementation)
        return new ItemMaterial(createItemStack(Items.POTION, customModelData));
    }

    private static URI getEndpointURI(String endpoint) {
        return createApiBaseUrl().resolve(endpoint);
    }

    private static URI createApiBaseUrl() {
        try {
            return new URI(BASE_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid URL format", e);
        }
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        return mapper;
    }

    private void printAspectInfo(AspectInfo aspectInfo) {
        System.out.println("Name: " + aspectInfo.name());
        System.out.println("Class Type: " + aspectInfo.classType());
        System.out.println("Gear Tier: " + aspectInfo.gearTier());
        System.out.println("Tiers:");
        for (Map.Entry<Integer, AspectTierInfo> tierEntry : aspectInfo.tiers().entrySet()) {
            System.out.println("  Tier: " + tierEntry.getKey());
            System.out.println("    Threshold: " + tierEntry.getValue().threshold());
            System.out.println("    Description: " + tierEntry.getValue().description());
        }
        System.out.println("Material: " + aspectInfo.material());
        System.out.println("-----------------------------------");
    }

    private static ItemStack createItemStack(Item item, int modelValue) {
        ItemStack itemStack = new ItemStack(item);

        // TODO: Pass model value to dummy item
        itemStack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>()));
        itemStack.set(DataComponents.UNBREAKABLE, new Unbreakable(false));
        return itemStack;
    }
}
