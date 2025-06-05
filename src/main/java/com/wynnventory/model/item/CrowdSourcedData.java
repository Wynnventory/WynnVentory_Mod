package com.wynnventory.model.item;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wynntils.utils.mc.McUtils;
import com.wynnventory.core.ModInfo;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public abstract class CrowdSourcedData {
    protected String playerName;
    protected String modVersion;

    @JsonProperty("timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected LocalDateTime collectionTime;

    protected CrowdSourcedData() {
        this.playerName = McUtils.playerName();
        this.modVersion = ModInfo.VERSION;
        this.collectionTime = LocalDateTime.now(ZoneOffset.UTC);
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
