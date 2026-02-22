package com.wynnventory.core.feature.updater;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wynnventory.core.WynnventoryMod;
import net.minecraft.SharedConstants;

public class UpdateRequest {
    private String[] loaders = new String[] {WynnventoryMod.getLoader().name().toLowerCase()};

    @JsonProperty("game_versions")
    private String[] gameVersions =
            new String[] {SharedConstants.getCurrentVersion().name()};

    public UpdateRequest() {}

    public String[] getLoaders() {
        return loaders;
    }

    public String[] getGameVersions() {
        return gameVersions;
    }
}
