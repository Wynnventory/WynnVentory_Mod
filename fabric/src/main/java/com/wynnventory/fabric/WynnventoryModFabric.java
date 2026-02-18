package com.wynnventory.fabric;

import com.wynnventory.core.WynnventoryMod;
import java.io.File;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public final class WynnventoryModFabric implements ClientModInitializer {
    private static final ModContainer INSTANCE = FabricLoader.getInstance()
            .getModContainer("wynnventory")
            .orElseThrow(() -> new IllegalStateException("Wynnventory mod container not found"));

    @Override
    public void onInitializeClient() {
        String version = INSTANCE.getMetadata().getVersion().getFriendlyString();
        File modFile = INSTANCE.getOrigin().getPaths().getFirst().toFile();

        WynnventoryMod.init(WynnventoryMod.ModLoader.FABRIC, version, modFile);
    }
}
