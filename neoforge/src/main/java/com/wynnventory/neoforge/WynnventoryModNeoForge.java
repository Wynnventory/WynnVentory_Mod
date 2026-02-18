package com.wynnventory.neoforge;

import com.wynnventory.core.WynnventoryMod;
import java.io.File;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforgespi.language.IModInfo;

@Mod(WynnventoryMod.MOD_ID)
public final class WynnventoryModNeoForge {
    private static final IModInfo MOD_CONTEXT =
            ModLoadingContext.get().getActiveContainer().getModInfo();

    public WynnventoryModNeoForge() {
        String version = MOD_CONTEXT.getVersion().toString();
        File modFile = MOD_CONTEXT.getOwningFile().getFile().getFilePath().toFile();

        WynnventoryMod.init(WynnventoryMod.ModLoader.FORGE, version, modFile);
    }
}
