package com.wynnventory.fabric;

import net.fabricmc.api.ModInitializer;

import com.wynnventory.core.WynnventoryMod;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.io.File;

public final class WynnventoryModFabric implements ModInitializer {
    private static final ModContainer INSTANCE = FabricLoader.getInstance().getModContainer("wynnventory").orElseThrow(() -> new IllegalStateException("Wynnventory mod container not found"));

    @Override
    public void onInitialize() {
        String version  = INSTANCE.getMetadata().getVersion().getFriendlyString();
        File modFile    = INSTANCE.getOrigin().getPaths().getFirst().toFile();

        WynnventoryMod.init(WynnventoryMod.ModLoader.FABRIC, version, modFile);
    }
}
