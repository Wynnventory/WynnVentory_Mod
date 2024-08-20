package com.wynnventory.mixin;

import com.wynntils.models.items.WynnItem;
import com.wynntils.models.items.WynnItemData;
import com.wynntils.models.items.items.game.*;
import com.wynnventory.WynnventoryMod;
import com.wynnventory.api.WynnventoryAPI;
import com.wynnventory.util.ItemStackUtils;
import com.wynnventory.util.ModUpdater;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin extends ClientCommonPacketListenerImpl {
    private static final WynnventoryAPI API = new WynnventoryAPI();
    private static final String LOOTPOOL_TITLE = "󏿲";

    protected ClientPacketListenerMixin(Minecraft minecraft, Connection connection, CommonListenerCookie commonListenerCookie) {
        super(minecraft, connection, commonListenerCookie);
    }

    @Inject(method = "handleLogin", at = @At("RETURN"))
    private void onPlayerJoin(ClientboundLoginPacket packet, CallbackInfo ci) {
        ModUpdater.checkForUpdates();
    }

    @Inject(
            method = "handleContainerSetSlot(Lnet/minecraft/network/protocol/game/ClientboundContainerSetSlotPacket;)V",
            at = @At("RETURN")
    )
    private void handleContainerSetSlotPre(ClientboundContainerSetSlotPacket packet, CallbackInfo ci) {
        Screen currentScreen = Minecraft.getInstance().screen;

        if (currentScreen instanceof AbstractContainerScreen<?> containerScreen) {
            if (containerScreen.getTitle().getString().equals(LOOTPOOL_TITLE)) {
                handleLootpoolData(containerScreen);
            } else {
                handleTrademarketData(packet);
            }
        }
    }

    @Unique
    private void handleTrademarketData(ClientboundContainerSetSlotPacket packet) {
        Item item = packet.getItem().getItem();
        if (packet.getContainerId() <= 0) return;
        if (item == Items.AIR || item == Items.COMPASS) return;
        API.sendTradeMarketResults(packet.getItem());
    }

    @Unique
    private void handleLootpoolData(AbstractContainerScreen<?> containerScreen) {
        AbstractContainerMenu container = containerScreen.getMenu();

        if (container instanceof ChestMenu chestContainer) {
            Container containerInstance = chestContainer.getContainer();
            if (containerInstance instanceof SimpleContainer simpleContainer) {
                List<ItemStack> items = simpleContainer.getItems();

                for (ItemStack item : items) {
                    if (item.getItem() != Items.AIR && item.getItem() != Items.POTION) {
                        WynnItem wynnItem = ItemStackUtils.getWynnItem(item);
                        switch (wynnItem) {
                            case GearItem gearItem -> WynnventoryMod.warn("GearItem: " + gearItem.getName());
                            case InsulatorItem insulatorItem ->
                                    WynnventoryMod.warn("InsulatorItem: " + ItemStackUtils.getWynntilsOriginalName(wynnItem.getData().get(WynnItemData.ITEMSTACK_KEY)));
                            case SimulatorItem simulatorItem ->
                                    WynnventoryMod.warn("SimulatorItem: " + ItemStackUtils.getWynntilsOriginalName(wynnItem.getData().get(WynnItemData.ITEMSTACK_KEY)));
                            case EmeraldItem emeraldItem ->
                                    WynnventoryMod.warn("EmeraldItem: " + ItemStackUtils.getWynntilsOriginalName(wynnItem.getData().get(WynnItemData.ITEMSTACK_KEY)));
                            case MiscItem miscItem ->
                                    WynnventoryMod.warn("MiscItem: " + ItemStackUtils.getWynntilsOriginalName(wynnItem.getData().get(WynnItemData.ITEMSTACK_KEY)));
                            case RuneItem runeItem ->
                                    WynnventoryMod.warn("RuneItem: " + ItemStackUtils.getWynntilsOriginalName(wynnItem.getData().get(WynnItemData.ITEMSTACK_KEY)));
                            case DungeonKeyItem dungeonKeyItem ->
                                    WynnventoryMod.warn("DungeonKeyItem: " + ItemStackUtils.getWynntilsOriginalName(wynnItem.getData().get(WynnItemData.ITEMSTACK_KEY)));
                            case null, default -> WynnventoryMod.error("Unknown class: " + wynnItem.getClass());
                        }
                    }
                }
            }
        }
    }
}