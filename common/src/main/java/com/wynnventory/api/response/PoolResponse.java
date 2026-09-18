package com.wynnventory.api.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/** The {@code data} of GET /api/v2/{lootpools,raidpools}/current: one stored week, grouped by region. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PoolResponse(int year, int week, List<PoolGroup> groups) {
    /** What a 404 ("no pool stored for this week yet") maps to. */
    public static final PoolResponse EMPTY = new PoolResponse(0, 0, List.of());

    public PoolResponse {
        groups = groups == null ? List.of() : groups;
    }
}
