package com.wynnventory.model.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.Duration;

public interface Expirable {
    Duration DATA_LIFESPAN = Duration.ofMinutes(5);

    @JsonIgnore
    boolean isExpired();
}
