package com.wynnventory.model.item.trademarket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wynnventory.model.item.simple.SimpleItemType;
import org.junit.jupiter.api.Test;

public class TrademarketItemSummaryTest {
    private static final ObjectMapper MAPPER =
            new ObjectMapper().registerModule(new Jdk8Module()).registerModule(new JavaTimeModule());

    // The data of GET /api/v2/market/items/{name}/price (PriceStats in openapi_v2.yaml).
    private static final String PRICE_DATA = "{"
            + "\"name\":\"Divzer\",\"tier\":null,\"shiny\":false,\"item_type\":\"gear\","
            + "\"icon\":{\"format\":\"attribute\",\"value\":\"bow\"},"
            + "\"lowest_price\":12000,\"highest_price\":20000,\"average_price\":15500.5,"
            + "\"average_mid_80_percent_price\":15000.0,\"p50_price\":15200.0,\"average_p50_ema_price\":15100.0,"
            + "\"unidentified_lowest_price\":9000,\"unidentified_average_price\":9500.0,"
            + "\"total_count\":12,\"unidentified_count\":2,\"timestamp\":\"2026-09-18T12:00:00Z\"}";

    @Test
    void parsesAV2PriceStatsObject() throws Exception {
        TrademarketItemSummary summary = MAPPER.readValue(PRICE_DATA, TrademarketItemSummary.class);
        assertEquals("Divzer", summary.getName());
        assertFalse(summary.isEmpty());
        assertEquals(SimpleItemType.GEAR, summary.getItem().getItemTypeEnum());
        assertEquals("GearItem", summary.getItemType());
        assertEquals(12000, summary.getLowestPrice());
        assertEquals(20000, summary.getHighestPrice());
        assertEquals(15500.5, summary.getAveragePrice());
        assertEquals(15000.0, summary.getAverageMid80PercentPrice());
        assertEquals(15200.0, summary.getMedian());
        assertEquals(15100.0, summary.getMovingMedian());
        assertEquals(9000, summary.getUnidentifiedLowestPrice());
        assertEquals(9500.0, summary.getUnidentifiedAveragePrice());
        assertNull(summary.getUnidentifiedHighestPrice());
        assertFalse(summary.isShiny());
        assertNull(summary.getTier());
        assertEquals("bow", summary.getIcon().getValue());
    }

    @Test
    void aggregatedHistoryStatsParseWithoutPerSnapshotFields() throws Exception {
        // The data of GET /api/v2/market/items/{name}/history/latest (AggregatedPriceStats): no shiny,
        // timestamp, item_type or icon.
        String data = "{\"name\":\"Divzer\",\"tier\":null,\"document_count\":7,\"average_price\":15000.0,"
                + "\"average_p50_ema_price\":14900.0,\"total_count\":80,\"unidentified_count\":3}";
        TrademarketItemSummary summary = MAPPER.readValue(data, TrademarketItemSummary.class);
        assertEquals("Divzer", summary.getName());
        assertEquals(15000.0, summary.getAveragePrice());
        assertEquals(14900.0, summary.getMovingMedian());
        assertNull(summary.getLowestPrice());
        assertNull(summary.getItem().getItemTypeEnum());
    }

    @Test
    void unknownItemTypeLabelLeavesTheTypeUnset() throws Exception {
        TrademarketItemSummary summary =
                MAPPER.readValue("{\"name\":\"X\",\"item_type\":\"weapon\"}", TrademarketItemSummary.class);
        assertNull(summary.getItem().getItemTypeEnum());
    }

    @Test
    void typelessPriceStatisticsExposeANullItemTypeWithoutThrowing() throws Exception {
        // Aggregated history stats carry no item_type; getItemType() must not NPE on the unset enum.
        String data = "{\"name\":\"Divzer\",\"tier\":null,\"document_count\":7,\"average_price\":15000.0,"
                + "\"average_p50_ema_price\":14900.0,\"total_count\":80,\"unidentified_count\":3}";
        TrademarketItemSummary summary = MAPPER.readValue(data, TrademarketItemSummary.class);
        assertNull(summary.getItemType());
    }
}
