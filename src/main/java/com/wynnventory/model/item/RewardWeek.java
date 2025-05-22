package com.wynnventory.model.item;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RewardWeek {
    private List<Lootpool> regions = new ArrayList<>();
    private int week;
    private int year;

    public List<Lootpool> getRegions() { return regions; }
    public void setRegions(List<Lootpool> regions) { this.regions = regions; }

    public int getWeek() { return week; }
    public void setWeek(int week) { this.week = week; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
}
