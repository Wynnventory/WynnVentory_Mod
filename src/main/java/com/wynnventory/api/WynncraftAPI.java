package com.wynnventory.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.wynntils.models.character.type.ClassType;
import com.wynnventory.WynnventoryMod;
import com.wynnventory.model.item.info.AspectInfo;
import com.wynnventory.model.item.info.AspectTierInfo;
import com.wynnventory.util.HttpUtil;
import org.objectweb.asm.TypeReference;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WynncraftAPI {
    private static final String BASE_URL = "https://api.wynncraft.com/v3";

    public Map<String, AspectInfo> fetchAllAspects() {
        try {
            List<ClassType> types = new ArrayList<>(List.of(ClassType.values()));
            types.remove(ClassType.NONE);

            for (ClassType type : types) {
                URI endpointUri = getEndpointURI("/aspects/" + type.getName().toLowerCase());

                System.out.println(endpointUri);

                HttpResponse<String> response = HttpUtil.sendHttpGetRequest(endpointUri);

                if (response.statusCode() == 200) {
                    return parseAspectResults(response.body());
                } else {
                    WynnventoryMod.error(response.statusCode() + " - Failed to fetch " + type + " lootpools: " + response.body());
                    return null;
                }
            }
        } catch (Exception e) {
            WynnventoryMod.error("Failed to initiate aspect fetch {}", e);
        }

        return new HashMap<>();
    }

    private Map<String, AspectInfo> parseAspectResults(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            // Map the root JSON to a Map<String, AspectInfo>
            Map<String, AspectInfo> aspectInfoMap = mapper.readValue(response, new com.fasterxml.jackson.core.type.TypeReference<>() {});

            // Access a specific AspectInfo
            AspectInfo aspect = aspectInfoMap.get("Aspect of Empowering Fantasy");

            // Example: Print the name
            System.out.println("Aspect Name: " + aspect.name());

            // Example: Iterate over tiers
            for (Map.Entry<Integer, AspectTierInfo> entry : aspect.tiers().entrySet()) {
                System.out.println("Tier: " + entry.getKey());
                System.out.println("Threshold: " + entry.getValue().threshHold());
                System.out.println("Description: " + entry.getValue().description());
            }

            // Access enums
            System.out.println("Class Type: " + aspect.classType());
            System.out.println("Gear Tier: " + aspect.gearTier());

            // Access material
            System.out.println("Material: " + aspect.material());

            return aspectInfoMap;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new HashMap<>();
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
}
