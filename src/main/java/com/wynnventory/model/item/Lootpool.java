package com.wynnventory.model.item;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Lootpool {
    private Set<LootpoolItem> lootpoolItems = new HashSet<>();
    private String region;
    private String playerName;
    private String modVersion;
    private LocalDateTime collectionTime;

    public Lootpool(String region, String playerName, String modVersion) {
        this.region = region;
        this.playerName = playerName;
        this.modVersion = modVersion;
        this.collectionTime = LocalDateTime.now(ZoneOffset.UTC);
    }

    public void addItem(LootpoolItem item) {
        lootpoolItems.add(item);
    }

    public void addItems(List<LootpoolItem> items) {
        lootpoolItems.addAll(items);
    }

    public void removeItem(LootpoolItem item) {
        lootpoolItems.remove(item);
    }

    public void removeItems(List<LootpoolItem> items) {
        items.forEach(lootpoolItems::remove);
    }

    public Set<LootpoolItem> getItems() {
        return lootpoolItems;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getModVersion() {
        return modVersion;
    }

    public void setModVersion(String modVersion) {
        this.modVersion = modVersion;
    }

    public String getCollectionTime() { return collectionTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); }

    public void setCollectionTime(LocalDateTime collectionTime) { this.collectionTime = collectionTime; }
}
