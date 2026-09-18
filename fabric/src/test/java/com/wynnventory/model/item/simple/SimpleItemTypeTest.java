package com.wynnventory.model.item.simple;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class SimpleItemTypeTest {
    @Test
    void testWardTypeResolvesFromWynntilsClassName() {
        assertEquals(SimpleItemType.WARD, SimpleItemType.fromType("WardItem"));
    }

    @Test
    void testWardIsSellableAndUsesWardIconPrefix() {
        assertTrue(SimpleItemType.WARD.isSellable(), "Wards are tradeable on the Trade Market.");
        assertEquals("ward", SimpleItemType.WARD.getIconPrefix());
    }

    @Test
    void testCharmTypeResolvesFromWynntilsClassName() {
        assertEquals(SimpleItemType.CHARM, SimpleItemType.fromType("CharmItem"));
        assertTrue(SimpleItemType.CHARM.isSellable(), "Charms are tradeable on the Trade Market.");
        assertEquals("charm", SimpleItemType.CHARM.getIconPrefix());
    }

    @Test
    void testGatheringToolTypeResolvesFromWynntilsClassName() {
        assertEquals(SimpleItemType.GATHERING_TOOL, SimpleItemType.fromType("GatheringToolItem"));
        assertTrue(SimpleItemType.GATHERING_TOOL.isSellable(), "Gathering tools are tradeable on the Trade Market.");
        assertEquals("tool", SimpleItemType.GATHERING_TOOL.getIconPrefix());
    }

    @Test
    void testEveryTypeRoundTripsThroughFromType() {
        for (SimpleItemType type : SimpleItemType.values()) {
            assertEquals(type, SimpleItemType.fromType(type.getType()));
        }
        assertNull(SimpleItemType.fromType("UnknownItem"));
    }

    @Test
    void testEveryTypeRoundTripsThroughFromApiLabel() {
        for (SimpleItemType type : SimpleItemType.values()) {
            assertEquals(type, SimpleItemType.fromApiLabel(type.getApiLabel()));
        }
        assertNull(SimpleItemType.fromApiLabel("unknown"));
        assertNull(SimpleItemType.fromApiLabel(null));
    }

    @Test
    void testApiLabelsFollowTheV2Vocabulary() {
        // v2 strips the "Item" suffix and snake_cases the rest (see WynnVentory_Web serializers/common.py)
        assertEquals("gear", SimpleItemType.GEAR.getApiLabel());
        assertEquals("dungeon_key", SimpleItemType.DUNGEON_KEY.getApiLabel());
        assertEquals("emerald_pouch", SimpleItemType.EMERALD_POUCH.getApiLabel());
        assertEquals("gathering_tool", SimpleItemType.GATHERING_TOOL.getApiLabel());
        assertEquals("emerald", SimpleItemType.EMERALD_ITEM.getApiLabel());
        assertEquals(SimpleItemType.ASPECT, SimpleItemType.fromApiLabel("aspect"));
        assertEquals(SimpleItemType.CHARM, SimpleItemType.fromApiLabel("charm"));
    }
}
