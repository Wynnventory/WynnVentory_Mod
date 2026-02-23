package com.wynnventory.mixin;

import com.wynnventory.feature.input.KeybindFeature;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public class OptionsMixin {
    @Inject(method = "load()V", at = @At("HEAD"))
    private void load(CallbackInfo ci) {
        KeybindFeature.registerKeybinds((Options) (Object) this);
    }
}
