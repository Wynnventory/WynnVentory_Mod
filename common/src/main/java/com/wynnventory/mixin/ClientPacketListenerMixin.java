package com.wynnventory.mixin;

import com.wynnventory.core.WynnventoryMod;
import com.wynnventory.event.LootrunPreviewOpenedEvent;
import com.wynnventory.model.reward.RewardScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin extends ClientCommonPacketListenerImpl {

    protected ClientPacketListenerMixin(
            Minecraft minecraft, Connection connection, CommonListenerCookie commonListenerCookie) {
        super(minecraft, connection, commonListenerCookie);
    }

    @Inject(
            method =
                    "handleContainerContent(Lnet/minecraft/network/protocol/game/ClientboundContainerSetContentPacket;)V",
            at = @At("RETURN"))
    private void handleContainerContentPost(ClientboundContainerSetContentPacket packet, CallbackInfo ci) {
//        if (!isRenderThread()) return;
        Screen screen = Minecraft.getInstance().screen;
        if (screen == null) return;

        if (RewardScreen.isLootrunTitle(screen.getTitle().getString())) {
            WynnventoryMod.postEvent(new LootrunPreviewOpenedEvent(packet.items(), packet.carriedItem(), packet.containerId(), packet.stateId(), screen.getTitle().getString()));
        }
        // TODO else if isRaidTitle
        }
}
