package com.wynnventory.model.item.trademarket;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wynnventory.model.item.Expirable;
import java.time.Instant;
import org.junit.jupiter.api.Test;

public class TrademarketItemSnapshotTest {
    private static final Instant STALE =
            Instant.now().minus(Expirable.DATA_LIFESPAN).minusSeconds(1);

    @Test
    void freshSnapshotWithoutDataIsNotExpired() {
        // A "no data" answer must be retried after the normal lifespan, not cached for the whole session.
        assertFalse(new TrademarketItemSnapshot(null, null).isExpired());
    }

    @Test
    void staleSnapshotWithoutDataIsExpired() {
        assertTrue(new TrademarketItemSnapshot(null, null, STALE).isExpired());
    }

    @Test
    void staleSnapshotWithDataIsExpired() {
        assertTrue(new TrademarketItemSnapshot(new TrademarketItemSummary(), null, STALE).isExpired());
    }

    @Test
    void freshSnapshotWithDataIsNotExpired() {
        assertFalse(new TrademarketItemSnapshot(new TrademarketItemSummary(), null).isExpired());
    }
}
