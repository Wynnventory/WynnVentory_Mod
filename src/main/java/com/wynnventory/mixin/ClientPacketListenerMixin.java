package com.wynnventory.mixin;

import com.wynntils.core.components.Models;
import com.wynntils.models.items.items.gui.GambitItem;
import com.wynntils.utils.mc.McUtils;
import com.wynnventory.accessor.ItemQueueAccessor;
import com.wynnventory.core.ModInfo;
import com.wynnventory.enums.Region;
import com.wynnventory.enums.RegionType;
import com.wynnventory.model.item.Lootpool;
import com.wynnventory.model.item.LootpoolItem;
import com.wynnventory.model.item.simplified.SimplifiedGambitItem;
import com.wynnventory.model.item.trademarket.TradeMarketItem;
import com.wynnventory.util.FavouriteNotifier;
import com.wynnventory.util.ModUpdater;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin extends ClientCommonPacketListenerImpl implements ItemQueueAccessor {
    private static final int CONTAINER_SLOTS = 54;
    private static int JOIN_COUNTER = 0;
    private static final String RAID_WINDOW_TITLE = "󏿡";

    @Unique
    private final List<TradeMarketItem> marketItemBuffer = new ArrayList<>();

    @Unique
    private final List<SimplifiedGambitItem> gambitItemBuffer = new ArrayList<>();

    @Unique
    private final Map<String, Lootpool> lootpoolBuffer = new ConcurrentHashMap<>();
    @Unique
    private final Map<String, Lootpool> raidpoolBuffer = new ConcurrentHashMap<>();

    protected ClientPacketListenerMixin(Minecraft minecraft, Connection connection, CommonListenerCookie commonListenerCookie) {
        super(minecraft, connection, commonListenerCookie);
    }

    @Inject(method = "handleLogin", at = @At("RETURN"))
    private void onPlayerJoin(ClientboundLoginPacket packet, CallbackInfo ci) {
        if (++JOIN_COUNTER == 2) {
            ModUpdater.checkForUpdates();
            FavouriteNotifier.checkFavourites();
        }
    }

/*    @Inject(method = "handleContainerSetSlot", at = @At("RETURN"))
    private void handleContainerSetSlot(ClientboundContainerSetSlotPacket packet, CallbackInfo ci) {
        Screen currentScreen = Minecraft.getInstance().screen;
        ItemStack item = packet.getItem();
        if (item.getItem() == Items.AIR || item.getItem() == Items.COMPASS || item.getItem() == Items.POTION) return;
        if (currentScreen == null || packet.getContainerId() <= 0) return;
        String screenTitle = currentScreen.getTitle().getString();

        if (screenTitle.equals(MARKET_TITLE)) {
            TradeMarketGearItem marketItem = TradeMarketGearItem.createTradeMarketItem(item);

            if(marketItem != null) {
                marketItemsBuffer.add(marketItem);
            }
        }
    }*/

    @Inject(method = "handleContainerContent", at = @At("RETURN"))
    private void handleContainerContent(ClientboundContainerSetContentPacket packet, CallbackInfo ci) {
        Screen currentScreen = Minecraft.getInstance().screen;
        if (!(currentScreen instanceof AbstractContainerScreen<?> containerScreen) || packet.getContainerId() <= 0)
            return;

        String title = containerScreen.getTitle().getString();
        List<ItemStack> packetItems = packet.getItems();

        Region region = Region.getRegionByInventoryTitle(title);
        if (region != null && region.getRegionType() != null) {
            handleRewardContainer(region, packetItems);
        } else if (RAID_WINDOW_TITLE.equalsIgnoreCase(title)) {
            handleRaidWindowContainer(packetItems);
        }
    }

    @Unique
    private void handleRewardContainer(Region region, List<ItemStack> items) {
        if (ModInfo.isDev()) {
            McUtils.sendMessageToClient(Component.literal(region.getRegionType() + " DETECTED. Region is " + region.getShortName()));
        }

        List<ItemStack> filtered = items.stream()
                .limit(CONTAINER_SLOTS)
                .filter(item -> !item.isEmpty() && item.getItem() != Items.COMPASS)
                .toList();

        addItemsToLootpoolQueue(region, filtered);
    }

    @Unique
    private void handleRaidWindowContainer(List<ItemStack> items) {
        items.stream()
                .limit(CONTAINER_SLOTS)
                .filter(item -> !item.isEmpty() && item.getItem() != Items.COMPASS)
                .map(Models.Item::getWynnItem)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(GambitItem.class::isInstance)
                .map(GambitItem.class::cast)
                .forEach(this::addItemToGambitQueue);
    }

    @Override
    public void addItemToTrademarketQueue(ItemStack item) {
        if (item.getItem() == Items.AIR || item.getItem() == Items.COMPASS || item.getItem() == Items.POTION) return;
        if (McUtils.inventory().items.contains(item)) return;

        TradeMarketItem tradeMarketItem = TradeMarketItem.from(item);
        if (tradeMarketItem != null && !marketItemBuffer.contains(tradeMarketItem)) {
            marketItemBuffer.add(tradeMarketItem);
        }
    }

    @Override
    public void addItemToGambitQueue(GambitItem gambitItem) {
        SimplifiedGambitItem item = new SimplifiedGambitItem(gambitItem);

        if (!gambitItemBuffer.contains(item)) {
            gambitItemBuffer.add(item);
        }
    }

    @Override
    public void addItemsToLootpoolQueue(Region region, List<ItemStack> items) {
        String shortName = region.getShortName();

        if (region.getRegionType() == RegionType.LOOTRUN) {
            lootpoolBuffer.computeIfAbsent(shortName,
                            k -> new Lootpool(region))
                    .addItems(LootpoolItem.createLootpoolItemsFromItemStack(items));
        } else if (region.getRegionType() == RegionType.RAID) {
            raidpoolBuffer.computeIfAbsent(shortName,
                            k -> new Lootpool(region))
                    .addItems(LootpoolItem.createLootpoolItemsFromItemStack(items));
        }
    }

    @Override
    public List<TradeMarketItem> getQueuedMarketItems() {
        return marketItemBuffer;
    }

    @Override
    public List<SimplifiedGambitItem> getQueuedGambitItems() {
        return gambitItemBuffer;
    }

    @Override
    public Map<String, Lootpool> getQueuedLootpools() {
        return lootpoolBuffer;
    }

    @Override
    public Map<String, Lootpool> getQueuedRaidpools() {
        return raidpoolBuffer;
    }
}