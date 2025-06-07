package com.wynnventory.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wynnventory.core.ModInfo;
import com.wynnventory.model.item.Icon;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class IconManager {
    public static final String GEAR_URL = "https://raw.githubusercontent.com/Wynntils/Static-Storage/main/Reference/gear.json";
    public static final String MATERIALS_URL = "https://raw.githubusercontent.com/Wynntils/Static-Storage/main/Reference/materials.json";
    public static final String INGREDIENTS_URL = "https://raw.githubusercontent.com/Wynntils/Static-Storage/main/Reference/ingredients.json";
    public static final String ASPECTS_URL = "https://raw.githubusercontent.com/Wynntils/Static-Storage/refs/heads/main/Reference/aspects.json";
    public static final String TOMES_URL = "https://raw.githubusercontent.com/Wynntils/Static-Storage/refs/heads/main/Reference/tomes.json";

    private static final Gson GSON = new Gson();
    private static Map<String, JsonObject> allEntries;

    private IconManager() {
    }

    public static void fetchAll() {
        Map<String, JsonObject> gearMap = fetchJson(GEAR_URL);
        Map<String, JsonObject> materialsMap = fetchJson(MATERIALS_URL);
        Map<String, JsonObject> ingredientsMap = fetchJson(INGREDIENTS_URL);
        Map<String, JsonObject> aspectsMap = fetchJson(ASPECTS_URL);
        Map<String, JsonObject> tomesMap = fetchJson(TOMES_URL);

        allEntries = new HashMap<>(gearMap);
        allEntries.putAll(materialsMap);
        allEntries.putAll(ingredientsMap);
        allEntries.putAll(flattenAspects(aspectsMap));
        allEntries.putAll(tomesMap);
    }

    private static Map<String, JsonObject> fetchJson(String url) {
        try {
            HttpResponse<String> resp = HttpClient.newHttpClient()
                    .send(HttpRequest.newBuilder(URI.create(url)).GET().build(),
                            HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() != 200) {
                throw new IOException("Unexpected response code: " + resp.statusCode());
            }

            return parseAndStripUnicodeKeys(resp.body());
        } catch (InterruptedException | IOException e) {
            ModInfo.logError("Could not fetch JSON from " + url, e);
            return Map.of();
        }
    }

    public static Icon getIcon(String name, int tier) {
        return getIcon(name + " " + tier);
    }

    public static Icon getIcon(String name) {
        JsonObject entry = allEntries.get(name.replaceFirst("^Shiny ", ""));
        if (entry == null) {
            ModInfo.logDebug("No JSON entry for key: " + name);
            return null;
        }

        try {
            String entryType = entry.has("type")
                    ? entry.get("type").getAsString().toLowerCase()
                    : "";

            if ("armour".equals(entryType)) {
                String mat = entry.get("armourMaterial").getAsString();
                String arm = entry.get("armourType").getAsString();
                return new Icon("armour", mat + "_" + arm);
            }
        } catch (Exception e) {
            ModInfo.logError("Failed to extract icon for entry: " + entry, e);
            return null;
        }

        return extractIcon(entry);
    }

    private static Icon extractIcon(JsonObject entry) {
        if (!entry.has("icon")) {
            ModInfo.logError("Missing icon for entry: " + entry);
            return null;
        }

        JsonObject iconObj = entry.getAsJsonObject("icon");

        String format = iconObj.get("format").getAsString();
        if (entry.has("tiers")) {
            format = "aspect_attribute";
        }
        JsonElement valElem = iconObj.get("value");

        String value = valElem.isJsonPrimitive()
                ? valElem.getAsString()
                : valElem.getAsJsonObject()
                .get("name")
                .getAsString();

        return new Icon(format, value.replaceAll(":", "_"));
    }

    private static Map<String, JsonObject> flattenAspects(Map<String, JsonObject> aspectsMap) {
        Map<String, JsonObject> flattenedMap = new HashMap<>();
        for (Map.Entry<String, JsonObject> classEntry : aspectsMap.entrySet()) {
            JsonObject classAspects = classEntry.getValue();
            // skip empty or non‐object values
            if (classAspects == null) continue;

            for (Map.Entry<String, JsonElement> aspectEntry
                    : classAspects.entrySet()) {
                JsonElement val = aspectEntry.getValue();
                if (val != null && val.isJsonObject()) {
                    flattenedMap.put(
                            aspectEntry.getKey(),
                            val.getAsJsonObject()
                    );
                }
            }
        }

        return flattenedMap;
    }

    public static Map<String, JsonObject> parseAndStripUnicodeKeys(String jsonBody) {
        Type mapType = new TypeToken<Map<String, JsonObject>>() {}.getType();
        Map<String, JsonObject> original = GSON.fromJson(jsonBody, mapType);

        Map<String, JsonObject> cleaned = new HashMap<>();
        for (Map.Entry<String, JsonObject> entry : original.entrySet()) {
            String rawKey = entry.getKey();
            // "\\P{ASCII}" matches any character NOT in the ASCII range (0x00 – 0x7F).
            // Replacing all \P{ASCII} with "" leaves only ASCII characters behind.
            String strippedKey = rawKey.replaceAll("\\P{ASCII}", "");

            if (cleaned.containsKey(strippedKey)) {
                ModInfo.logWarn("Key collision after stripping Unicode: " + strippedKey);
                continue;
            }

            cleaned.put(strippedKey, entry.getValue());
        }

        return cleaned;
    }
}
