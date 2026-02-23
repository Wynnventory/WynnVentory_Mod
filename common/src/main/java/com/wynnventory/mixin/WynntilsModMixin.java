package com.wynnventory.mixin;

import com.wynntils.core.WynntilsMod;
import com.wynnventory.feature.FavouriteNotifyFeature;
import com.wynnventory.feature.tooltip.aspect.AspectTooltipFeature;
import com.wynnventory.feature.tooltip.price.PriceTooltipFeature;
import com.wynnventory.feature.updater.AutoUpdateFeature;
import java.io.File;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WynntilsMod.class)
public abstract class WynntilsModMixin {
    @Inject(method = "init", at = @At("RETURN"))
    private static void init(
            WynntilsMod.ModLoader loader,
            String modVersion,
            boolean isDevelopmentEnvironment,
            File modFile,
            CallbackInfo ci) {
        WynntilsMod.registerEventListener(new AspectTooltipFeature());
        WynntilsMod.registerEventListener(new AutoUpdateFeature());
        WynntilsMod.registerEventListener(new FavouriteNotifyFeature());
        WynntilsMod.registerEventListener(new PriceTooltipFeature());
    }
}
