package com.wynnventory.feature.updater;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wynnventory.core.WynnventoryMod;
import net.minecraft.SharedConstants;

public class UpdateRequest {
    private final String[] loaders =
            new String[] {WynnventoryMod.getLoader().name().toLowerCase()};

    @JsonProperty("game_versions")
    private final String[] gameVersions =
            new String[] {SharedConstants.getCurrentVersion().getName()};

    public String[] getLoaders() {
        return loaders;
    }

    public String[] getGameVersions() {
        return gameVersions;
    }
}
