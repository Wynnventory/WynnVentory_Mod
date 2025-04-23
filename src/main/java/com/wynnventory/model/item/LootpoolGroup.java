package com.wynnventory.model.item;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LootpoolGroup {

    private String group;

    @JsonProperty("loot_items")
    private List<LootpoolItem> lootItems;

    // Getters and setters

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @JsonProperty("loot_items")
    public List<LootpoolItem> getLootItems() {
        return lootItems;
    }

    public void setLootItems(List<LootpoolItem> lootItems) {
        this.lootItems = lootItems;
    }
}
