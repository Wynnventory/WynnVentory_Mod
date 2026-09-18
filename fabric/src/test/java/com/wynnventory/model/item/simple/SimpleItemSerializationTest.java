package com.wynnventory.model.item.simple;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wynntils.models.gear.type.GearTier;
import com.wynntils.models.stats.type.ShinyStat;
import com.wynntils.models.stats.type.ShinyStatType;
import com.wynntils.models.stats.type.StatUnit;
import com.wynnventory.model.item.Icon;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * Pins the wire shape of the v1 submission payload (what {@code SimpleItem} and its subclasses
 * serialize to when POSTed). The mod's Jackson deserialization annotations and {@code setItemType}
 * signature can drift silently since nothing else serializes these classes in tests.
 */
public class SimpleItemSerializationTest {
    private static final ObjectMapper MAPPER =
            new ObjectMapper().registerModule(new Jdk8Module()).registerModule(new JavaTimeModule());

    @Test
    void tierItemSerializesStorageTypeAndTierFields() throws Exception {
        SimpleTierItem item = new SimpleTierItem(
                "Water Powder",
                GearTier.NORMAL,
                SimpleItemType.POWDER,
                "WaterPowder",
                new Icon("attribute", "waterSmall"),
                3,
                4);

        JsonNode node = MAPPER.readTree(MAPPER.writeValueAsString(item));

        assertEquals("PowderItem", node.get("itemType").asText());
        assertEquals("WaterPowder", node.get("type").asText());
        assertEquals(4, node.get("tier").asInt());
        assertEquals(3, node.get("amount").asInt());
        assertEquals("Common", node.get("rarity").asText());
        assertEquals("Water Powder", node.get("name").asText());
        assertEquals("attribute", node.get("icon").get("format").asText());
        assertEquals("waterSmall", node.get("icon").get("value").asText());
    }

    @Test
    void gearItemSerializesShinyStatAndRarityName() throws Exception {
        SimpleGearItem item = new SimpleGearItem(
                "Divzer",
                GearTier.MYTHIC,
                SimpleItemType.GEAR,
                "BOW",
                null,
                1,
                false,
                0,
                Optional.of(
                        new ShinyStat(new ShinyStatType(1, "raidsCompleted", "Raids Completed", StatUnit.RAW), 5L, 0)),
                0f,
                List.of());

        JsonNode node = MAPPER.readTree(MAPPER.writeValueAsString(item));

        assertEquals("GearItem", node.get("itemType").asText());
        assertEquals("BOW", node.get("type").asText());
        assertEquals("Mythic", node.get("rarity").asText());
        assertTrue(node.get("shiny").asBoolean());
        assertEquals(
                "raidsCompleted",
                node.get("shinyStat").get("statType").get("key").asText());
        assertEquals(5, node.get("shinyStat").get("value").asLong());
    }

    @Test
    void plainItemSerializesStorageTypeAndOmitsTier() throws Exception {
        SimpleItem item = new SimpleItem(
                "Aspect of the Berserker", GearTier.FABLED, SimpleItemType.ASPECT, "WarriorAspect", null, 1);

        JsonNode node = MAPPER.readTree(MAPPER.writeValueAsString(item));

        assertEquals("AspectItem", node.get("itemType").asText());
        assertFalse(node.has("tier"));
    }
}
