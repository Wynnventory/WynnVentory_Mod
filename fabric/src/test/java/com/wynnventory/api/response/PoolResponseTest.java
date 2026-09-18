package com.wynnventory.api.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wynntils.models.gear.type.GearTier;
import com.wynnventory.model.item.simple.SimpleGearItem;
import com.wynnventory.model.item.simple.SimpleItem;
import com.wynnventory.model.item.simple.SimpleItemType;
import com.wynnventory.model.item.simple.SimpleTierItem;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class PoolResponseTest {
    private static final ObjectMapper MAPPER =
            new ObjectMapper().registerModule(new Jdk8Module()).registerModule(new JavaTimeModule());

    // The shiny_stat payload is whatever the mod submitted: Jackson's rendering of Wynntils' ShinyStat record.
    private static final String SHINY_STAT = "{\"statType\":{\"id\":1,\"key\":\"raidsCompleted\","
            + "\"displayName\":\"Raids Completed\",\"statUnit\":\"RAW\"},\"value\":5,\"shinyRerolls\":0}";

    private static final String POOL = "{"
            + "\"year\":2026,\"week\":38,"
            + "\"groups\":[{"
            + "\"name\":\"Silent Expanse\",\"type\":\"Lootrun\",\"timestamp\":\"2026-09-18T18:00:00Z\","
            + "\"items\":["
            + "{\"name\":\"Divzer\",\"amount\":1,\"rarity\":\"mythic\",\"item_type\":\"gear\",\"subtype\":\"bow\","
            + "\"tier\":null,\"shiny\":true,\"shiny_stat\":" + SHINY_STAT + ","
            + "\"icon\":{\"format\":\"attribute\",\"value\":\"bow\"}},"
            + "{\"name\":\"Water Powder\",\"amount\":3,\"rarity\":\"normal\",\"item_type\":\"powder\","
            + "\"subtype\":\"waterpowder\",\"tier\":4,\"shiny\":false,\"shiny_stat\":null,\"icon\":null},"
            + "{\"name\":\"Aspect of the Berserker\",\"amount\":1,\"rarity\":\"fabled\",\"item_type\":\"aspect\","
            + "\"subtype\":\"warrioraspect\",\"tier\":null,\"shiny\":false,\"shiny_stat\":null,\"icon\":null,"
            + "\"added_later\":1}"
            + "]}]}";

    private static PoolItem item(int index) throws Exception {
        return MAPPER.readValue(POOL, PoolResponse.class)
                .groups()
                .get(0)
                .items()
                .get(index);
    }

    @Test
    void parsesGroupsAndItems() throws Exception {
        PoolResponse pool = MAPPER.readValue(POOL, PoolResponse.class);
        assertEquals(2026, pool.year());
        assertEquals(38, pool.week());
        assertEquals(1, pool.groups().size());

        PoolGroup group = pool.groups().get(0);
        assertEquals("Silent Expanse", group.name());
        assertEquals("Lootrun", group.type());
        assertEquals(Instant.parse("2026-09-18T18:00:00Z"), group.timestamp());
        assertEquals(3, group.items().size());
    }

    @Test
    void gearWithShinyStatBecomesAShinyGearItem() throws Exception {
        SimpleItem mapped = item(0).toSimpleItem();
        SimpleGearItem gear = assertInstanceOf(SimpleGearItem.class, mapped);
        assertEquals("Divzer", gear.getName());
        assertEquals(GearTier.MYTHIC, gear.getRarityEnum());
        assertEquals(SimpleItemType.GEAR, gear.getItemTypeEnum());
        assertEquals("bow", gear.getType());
        assertEquals(1, gear.getAmount());
        assertTrue(gear.isShiny());
        assertEquals(
                "raidsCompleted", gear.getShinyStat().orElseThrow().statType().key());
        assertEquals("bow", gear.getIcon().getValue());
    }

    @Test
    void tieredTypesBecomeTierItems() throws Exception {
        SimpleItem mapped = item(1).toSimpleItem();
        SimpleTierItem powder = assertInstanceOf(SimpleTierItem.class, mapped);
        assertEquals(4, powder.getTier());
        assertEquals(3, powder.getAmount());
        assertEquals(SimpleItemType.POWDER, powder.getItemTypeEnum());
        assertEquals(GearTier.NORMAL, powder.getRarityEnum());
        assertNull(powder.getIcon());
    }

    @Test
    void otherTypesBecomePlainItemsAndUnknownFieldsAreIgnored() throws Exception {
        SimpleItem mapped = item(2).toSimpleItem();
        assertEquals(SimpleItem.class, mapped.getClass());
        assertEquals(SimpleItemType.ASPECT, mapped.getItemTypeEnum());
        assertEquals(GearTier.FABLED, mapped.getRarityEnum());
        assertEquals("warrioraspect", mapped.getType());
    }

    @Test
    void unknownItemTypeMapsToNull() {
        PoolItem item = new PoolItem("Mystery", 1, "rare", "weapon", "bow", null, false, Optional.empty(), null);
        assertNull(item.toSimpleItem());
    }

    @Test
    void missingRarityFallsBackToNormal() {
        PoolItem item = new PoolItem("Tome", 1, null, "tome", null, null, false, Optional.empty(), null);
        assertEquals(GearTier.NORMAL, item.toSimpleItem().getRarityEnum());
    }

    @Test
    void missingCollectionsDefaultToEmpty() throws Exception {
        assertEquals(
                List.of(),
                MAPPER.readValue("{\"year\":2026,\"week\":1}", PoolResponse.class)
                        .groups());
        assertEquals(
                List.of(),
                MAPPER.readValue("{\"name\":\"Corkus\"}", PoolGroup.class).items());

        PoolItem item = MAPPER.readValue("{\"name\":\"X\",\"item_type\":\"tome\"}", PoolItem.class);
        assertEquals(Optional.empty(), item.shinyStat());
        assertEquals(0, item.amount());
        assertEquals(List.of(), PoolResponse.EMPTY.groups());
    }

    @Test
    void shinyStatIsNeverANullOptional() throws Exception {
        // Jackson's Jdk8Module hands the record Optional.empty() for both an absent and a null
        // shiny_stat, so the record needs no null guard (which Sonar S2789 forbids on Optionals).
        ObjectMapper lenient = MAPPER.copy().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        for (ObjectMapper mapper : List.of(MAPPER, lenient)) {
            PoolItem absent = mapper.readValue("{\"name\":\"X\",\"item_type\":\"gear\"}", PoolItem.class);
            assertEquals(Optional.empty(), absent.shinyStat());
            assertFalse(((SimpleGearItem) absent.toSimpleItem()).isShiny());

            PoolItem explicitNull =
                    mapper.readValue("{\"name\":\"X\",\"item_type\":\"gear\",\"shiny_stat\":null}", PoolItem.class);
            assertEquals(Optional.empty(), explicitNull.shinyStat());
            assertFalse(((SimpleGearItem) explicitNull.toSimpleItem()).isShiny());
        }
    }
}
