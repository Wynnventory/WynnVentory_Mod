package com.wynnventory.mixin;

import com.wynntils.core.WynntilsMod;
import com.wynntils.utils.mc.McUtils;
import com.wynnventory.core.WynnventoryMod;
import com.wynnventory.event.RaidWindowOpenedEvent;
import com.wynnventory.event.RewardPreviewOpenedEvent;
import com.wynnventory.handler.TooltipRenderHandler;
import com.wynnventory.model.container.RaidWindowContainer;
import com.wynnventory.model.reward.RewardPool;
import com.wynnventory.util.ContainerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(WynntilsMod.class)
public abstract class WynntilsModMixin {


    @Inject(
            method =
                    "init",
            at = @At("RETURN"))
    private static void init(WynntilsMod.ModLoader loader, String modVersion, boolean isDevelopmentEnvironment, File modFile, CallbackInfo ci) {
        WynntilsMod.registerEventListener(new TooltipRenderHandler());
    }

}
