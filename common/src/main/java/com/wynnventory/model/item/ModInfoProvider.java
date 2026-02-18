package com.wynnventory.model.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.wynntils.utils.mc.McUtils;
import com.wynnventory.core.WynnventoryMod;

public abstract class ModInfoProvider extends TimestampedObject {
    @JsonProperty(value = "playerName", access = Access.READ_ONLY)
    protected String playerName;

    @JsonProperty(value = "modVersion", access = Access.READ_ONLY)
    protected String modVersion;

    protected ModInfoProvider() {
        super();

        if (McUtils.player() != null) {
            this.playerName = McUtils.playerName();
        } else {
            this.playerName = null;
        }

        this.modVersion = WynnventoryMod.getVersion();
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
}
