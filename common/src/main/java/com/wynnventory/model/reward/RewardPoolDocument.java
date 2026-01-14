package com.wynnventory.model.reward;

import com.wynnventory.data.ModInfoProvider;
import com.wynnventory.model.item.simple.SimpleItem;

import java.util.List;

public final class RewardPoolDocument extends ModInfoProvider {

    private final List<SimpleItem> items;
    private final String region;
    private final String type;

    public RewardPoolDocument(List<SimpleItem> items, String region, String type) {
        super();
        this.items = items;
        this.region = region;
        this.type = type;
    }

    public List<SimpleItem> getItems() { return items; }
    public String getRegion() { return region; }
    public String getType() { return type; }
}
