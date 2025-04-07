package com.wynnventory.model.item;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupedLootpool {

    @JsonProperty("group_items")
    @JsonAlias("region_items")
    private List<LootpoolGroup> groupItems;

    private String region;
    private String timestamp;
    private int week;
    private int year;

    // Getters and setters

    @JsonProperty("group_items")
    public List<LootpoolGroup> getGroupItems() {
        return groupItems;
    }

    public void setGroupItems(List<LootpoolGroup> groupItems) {
        this.groupItems = groupItems;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
