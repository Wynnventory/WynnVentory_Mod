package com.wynnventory.mixin;

import com.wynnventory.core.input.KeyBindManager;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public class OptionsMixin {
    @Inject(method = "load()V", at = @At("HEAD"))
    private void load(CallbackInfo ci) {
        KeyBindManager.registerKeybinds((Options) (Object) this);
    }
}
