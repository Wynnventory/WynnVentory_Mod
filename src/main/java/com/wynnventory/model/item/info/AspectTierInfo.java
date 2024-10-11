package com.wynnventory.model.item.info;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record AspectTierInfo (
    @JsonProperty("threshold")
    int threshHold,

    @JsonProperty("description")
    String description
    ) {

    @JsonCreator
    public AspectTierInfo(int threshHold, String description) {
        this.threshHold = threshHold;
        this.description = description;
    }
}
