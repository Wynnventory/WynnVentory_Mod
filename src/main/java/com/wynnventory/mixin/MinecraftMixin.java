package com.wynnventory.mixin;

import com.wynnventory.util.RaidDisplayQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Inject(method = "setScreen", at = @At("RETURN"))
    private void onSetScreen(Screen guiScreen, CallbackInfo ci) {
        RaidDisplayQueue.setShowRaidAspects(guiScreen != null && isRaidLobbyScreen(guiScreen));
    }

    @Unique
    private boolean isRaidLobbyScreen(Screen screen) {
        return screen.getTitle().getString().equals("󏿡");
    }
}