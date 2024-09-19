package com.wynnventory.mixin;

import com.wynntils.core.components.Models;
import com.wynntils.models.items.WynnItem;
import com.wynntils.utils.mc.McUtils;
import com.wynnventory.WynnventoryMod;
import com.wynnventory.accessor.ItemQueueAccessor;
import com.wynnventory.model.item.Lootpool;
import com.wynnventory.model.item.LootpoolItem;
import com.wynnventory.model.item.TradeMarketItem;
import com.wynnventory.util.ModUpdater;
import com.wynnventory.util.RegionDetector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin extends ClientCommonPacketListenerImpl implements ItemQueueAccessor {
    private static final String MARKET_TITLE = "󏿨";
    private static final String LOOTPOOL_TITLE = "󏿲";
    private static final String RAIDPOOL_TITLE = "󏿪";

    private static boolean IS_FIRST_WORLD_JOIN = true;

    @Unique
    private final List<TradeMarketItem> marketItemsBuffer = new ArrayList<>();
    @Unique
    private final Map<String, Lootpool> lootpoolBuffer = new HashMap<>();
    @Unique
    private final Map<String, Lootpool> raidpoolBuffer = new HashMap<>();

    protected ClientPacketListenerMixin(Minecraft minecraft, Connection connection, CommonListenerCookie commonListenerCookie) {
        super(minecraft, connection, commonListenerCookie);
    }

    @Inject(method = "handleLogin", at = @At("RETURN"))
    private void onPlayerJoin(ClientboundLoginPacket packet, CallbackInfo ci) {
        if(IS_FIRST_WORLD_JOIN) {
           IS_FIRST_WORLD_JOIN = false;
        } else {
            ModUpdater.checkForUpdates();
        }
    }

    @Inject(method = "handleContainerSetSlot", at = @At("RETURN"))
    private void handleContainerSetSlot(ClientboundContainerSetSlotPacket packet, CallbackInfo ci) {
        Screen currentScreen = Minecraft.getInstance().screen;
        ItemStack item = packet.getItem();
        if (item.getItem() == Items.AIR || item.getItem() == Items.COMPASS || item.getItem() == Items.POTION) return;
        if (currentScreen == null || packet.getContainerId() <= 0) return;
        String screenTitle = currentScreen.getTitle().getString();

        if (screenTitle.equals(MARKET_TITLE)) {
            TradeMarketItem marketItem = TradeMarketItem.createTradeMarketItem(item);

            if(marketItem != null) {
                marketItemsBuffer.add(marketItem);
            }
        }
    }

    @Inject(method = "handleContainerContent", at = @At("RETURN"))
    private void handleContainerContent(ClientboundContainerSetContentPacket packet, CallbackInfo ci) {
        Screen currentScreen = Minecraft.getInstance().screen;
        if (currentScreen == null || packet.getContainerId() <= 0) return;

        if (currentScreen instanceof AbstractContainerScreen<?> containerScreen) {
            String title = containerScreen.getTitle().getString();
            if (title.equals(LOOTPOOL_TITLE)) {
                String region = RegionDetector.getRegion(McUtils.player().getBlockX(), McUtils.player().getBlockZ());

                if(WynnventoryMod.isDev()) {
                    McUtils.sendMessageToClient(Component.literal("LOOTPOOL DETECTED. Region is " + region));
                }

                if(region.equals(RegionDetector.UNDEFINED_REGION)) {
                    return;
                }

                addItemsToQueue(lootpoolBuffer, region, packet.getItems());
            } else if (title.equals(RAIDPOOL_TITLE)) {
                String region = RegionDetector.getRegion(McUtils.player().getBlockX(), McUtils.player().getBlockZ());

                if(WynnventoryMod.isDev()) {
                    McUtils.sendMessageToClient(Component.literal("RAIDPOOL DETECTED. Region is " + region));
                }

                if(region.equals(RegionDetector.UNDEFINED_REGION)) {
                    return;
                }

                addItemsToQueue(raidpoolBuffer, region, packet.getItems());
            }
        }
    }

    private void addItemsToQueue(Map<String, Lootpool> queue, String region, List<ItemStack> items) {
        if(!queue.containsKey(region)) {
            queue.put(region, new Lootpool(region, McUtils.playerName(), WynnventoryMod.WYNNVENTORY_VERSION));
        }

        List<LootpoolItem> lootpoolItems = LootpoolItem.createLootpoolItemsFromItemStack(items.stream()
                .filter(item -> !McUtils.player().getInventory().items.contains(item)).toList());

        queue.get(region).addItems(lootpoolItems);
    }

    @Override
    public List<TradeMarketItem> getQueuedMarketItems() {
        return marketItemsBuffer;
    }

    @Override
    public List<Lootpool> getQueuedLootpools() {
        return lootpoolBuffer.values().stream().toList();
    }

    @Override
    public List<Lootpool> getQueuedRaidpools() {
        return raidpoolBuffer.values().stream().toList();
    }
}