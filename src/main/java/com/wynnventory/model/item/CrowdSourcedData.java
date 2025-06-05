package com.wynnventory.model.item;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.wynntils.utils.mc.McUtils;
import com.wynnventory.core.ModInfo;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public abstract class CrowdSourcedData {
    @JsonProperty(value = "playerName", access = Access.READ_ONLY)
    protected String playerName;

    @JsonProperty(value = "modVersion", access = Access.READ_ONLY)
    protected String modVersion;

    @JsonProperty(value = "timestamp", access = Access.READ_ONLY)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected LocalDateTime collectionTime;

    protected CrowdSourcedData() {
        if (McUtils.player() != null) {
            this.playerName = McUtils.playerName();
        } else {
            this.playerName = null;
        }

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
