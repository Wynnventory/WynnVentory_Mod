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
    void testEveryTypeRoundTripsThroughFromType() {
        for (SimpleItemType type : SimpleItemType.values()) {
            assertEquals(type, SimpleItemType.fromType(type.getType()));
        }
        assertNull(SimpleItemType.fromType("UnknownItem"));
    }
}
