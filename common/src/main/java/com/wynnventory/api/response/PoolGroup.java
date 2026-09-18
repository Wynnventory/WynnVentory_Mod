package com.wynnventory.api.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;
import java.util.List;

/** One v2 pool group; for the raw weekly pools the name is the region / raid full name. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PoolGroup(String name, String type, Instant timestamp, List<PoolItem> items) {
    public PoolGroup {
        items = items == null ? List.of() : items;
    }
}
