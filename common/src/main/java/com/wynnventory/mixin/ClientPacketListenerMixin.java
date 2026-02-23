package com.wynnventory.mixin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.RootCommandNode;
import com.wynntils.utils.mc.McUtils;
import com.wynnventory.core.WynnventoryMod;
import com.wynnventory.events.CommandAddedEvent;
import com.wynnventory.events.CommandSentEvent;
import com.wynnventory.events.RaidLobbyPopulatedEvent;
import com.wynnventory.events.RewardPreviewOpenedEvent;
import com.wynnventory.model.container.Container;
import com.wynnventory.model.container.RaidLobbyContainer;
import com.wynnventory.model.reward.RewardPool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.world.flag.FeatureFlagSet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin extends ClientCommonPacketListenerImpl {
    @Shadow
    private CommandDispatcher<SharedSuggestionProvider> commands;

    @Shadow
    private RegistryAccess.Frozen registryAccess;

    @Shadow
    @Final
    private FeatureFlagSet enabledFeatures;

    protected ClientPacketListenerMixin(
            Minecraft minecraft, Connection connection, CommonListenerCookie commonListenerCookie) {
        super(minecraft, connection, commonListenerCookie);
    }

    @Unique
    private static boolean isRenderThread() {
        return McUtils.mc().isSameThread();
    }

    @Inject(
            method =
                    "handleContainerContent(Lnet/minecraft/network/protocol/game/ClientboundContainerSetContentPacket;)V",
            at = @At("RETURN"))
    private void handleContainerContentPost(ClientboundContainerSetContentPacket packet, CallbackInfo ci) {
        if (!isRenderThread()) return;

        Container container = Container.current();
        if (container == null) return;
        if (!container.matchesContainer(packet.getContainerId())) return;

        String title = container.title();
        if (RewardPool.isLootrunTitle(title))
            WynnventoryMod.postEvent(
                    new RewardPreviewOpenedEvent.Lootrun(packet.getItems(), packet.getContainerId(), title));
        if (RewardPool.isRaidTitle(title))
            WynnventoryMod.postEvent(
                    new RewardPreviewOpenedEvent.Raid(packet.getItems(), packet.getContainerId(), title));
        if (RaidLobbyContainer.matchesTitle(title))
            WynnventoryMod.postEvent(new RaidLobbyPopulatedEvent(packet.getItems(), packet.getContainerId(), title));
    }

    @Inject(method = "sendCommand(Ljava/lang/String;)V", at = @At("HEAD"), cancellable = true)
    private void sendCommand(String string, CallbackInfo ci) {
        CommandSentEvent event = new CommandSentEvent(string);
        WynnventoryMod.postEvent(event);

        if (event.isCanceled()) ci.cancel();
    }

    @Inject(
            method = "handleCommands(Lnet/minecraft/network/protocol/game/ClientboundCommandsPacket;)V",
            at = @At("RETURN"))
    private void handleCommands(ClientboundCommandsPacket packet, CallbackInfo ci) {
        if (!isRenderThread()) return;

        RootCommandNode<SharedSuggestionProvider> root = this.commands.getRoot();

        WynnventoryMod.postEvent(new CommandAddedEvent(root));
    }
}
