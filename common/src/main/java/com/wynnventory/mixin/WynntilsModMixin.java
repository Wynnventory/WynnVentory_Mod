package com.wynnventory.mixin;

import com.wynntils.core.WynntilsMod;
import com.wynnventory.feature.FeatureManager;
import java.io.File;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WynntilsMod.class)
public abstract class WynntilsModMixin {
    private WynntilsModMixin() {}

    @Inject(method = "init", at = @At("RETURN"))
    private static void init(
            WynntilsMod.ModLoader loader,
            String modVersion,
            boolean isDevelopmentEnvironment,
            File modFile,
            CallbackInfo ci) {
        WynntilsMod.registerEventListener(FeatureManager.INSTANCE);
    }
}
