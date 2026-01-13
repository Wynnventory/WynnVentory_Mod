package com.wynnventory.mixin;

import com.wynntils.core.components.Models;
import com.wynntils.models.items.WynnItem;
import com.wynntils.models.items.items.game.GearItem;
import com.wynntils.utils.mc.McUtils;
import com.wynnventory.core.WynnventoryMod;
import com.wynnventory.event.LootrunPreviewOpenedEvent;
import com.wynnventory.model.reward.RewardScreen;
import com.wynnventory.util.ItemStackUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

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

    @Inject(
            method =
                    "handleContainerSetSlot(Lnet/minecraft/network/protocol/game/ClientboundContainerSetSlotPacket;)V",
            at = @At("RETURN"))
    private void handleContainerSetSlotPost(ClientboundContainerSetSlotPacket clientboundContainerSetSlotPacket, CallbackInfo ci) {
        ItemStack itemStack = clientboundContainerSetSlotPacket.getItem();

        Optional<WynnItem> maybeItem = Models.Item.getWynnItem(itemStack);
        if (maybeItem.isEmpty()) return;
        switch (maybeItem.get()) {
            case GearItem gearItem ->
                    McUtils.sendMessageToClient(Component.literal("Item name is: " + gearItem.getName()));
            default -> McUtils.sendMessageToClient(Component.literal("Not GearItem"));
        }
    }
}
