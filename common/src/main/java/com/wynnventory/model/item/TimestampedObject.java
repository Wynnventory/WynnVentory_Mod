package com.wynnventory.model.item;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.time.Instant;

public abstract class TimestampedObject implements Expirable {
    @JsonProperty(value = "timestamp", access = Access.READ_ONLY)
    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "UTC")
    protected Instant timestamp;

    protected TimestampedObject() {
        this.timestamp = Instant.now();
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Instant getTimestamp() {
        return this.timestamp;
    }

    public boolean isExpired() {
        return timestamp.isBefore(Instant.now().minus(DATA_LIFESPAN));
    }
}
