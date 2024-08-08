package com.wynnventory.mixin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.wynntils.core.WynntilsMod;
import com.wynntils.core.components.Models;
import com.wynntils.core.events.MixinHelper;
import com.wynntils.mc.event.ContainerSetSlotEvent;
import com.wynntils.models.items.items.game.GearItem;
import com.wynntils.models.trademarket.type.TradeMarketPriceInfo;
import com.wynntils.utils.mc.McUtils;
import com.wynnventory.WynnventoryMod;
import com.wynnventory.model.item.TradeMarketItem;
import com.wynnventory.util.TradeMarketPriceParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Mixin(ClientPacketListener.class)
public abstract class TradeMarketScannerMixin extends ClientCommonPacketListenerImpl {

    protected TradeMarketScannerMixin(Minecraft minecraft, Connection connection, CommonListenerCookie commonListenerCookie) {
        super(minecraft, connection, commonListenerCookie);
    }

//    @Unique
//    private static boolean isRenderThread() {
//        return McUtils.mc().isSameThread();
//    }

    @Inject(
            method = "handleContainerSetSlot(Lnet/minecraft/network/protocol/game/ClientboundContainerSetSlotPacket;)V",
            at = @At("HEAD")
    )
    private void handleContainerSetSlotPre(ClientboundContainerSetSlotPacket packet, CallbackInfo ci) {
//        if (!isRenderThread()) return;
        WynnventoryMod.LOGGER.info("Entered handleContainerSetSlotPre");
        WynnventoryMod.LOGGER.error("Entered handleContainerSetSlotPre");
        System.out.println("Entered handleContainerSetSlotPre");
        ContainerSetSlotEvent event = new ContainerSetSlotEvent.Pre(
                packet.getContainerId(), packet.getStateId(), packet.getSlot(), packet.getItem());
        MixinHelper.post(event);
        ItemStack item = packet.getItem();
        Optional<GearItem> gearItemOptional = Models.Item.asWynnItem(item, GearItem.class);
        List<TradeMarketItem> marketItems = new ArrayList<>();

        if(gearItemOptional.isPresent()) {
            TradeMarketPriceInfo priceInfo = TradeMarketPriceParser.calculateItemPriceInfo(item);

            if(priceInfo != TradeMarketPriceInfo.EMPTY) {
                marketItems.add(new TradeMarketItem(gearItemOptional.get(), priceInfo.price(), priceInfo.amount()));
                WynnventoryMod.LOGGER.info("Received item {}", gearItemOptional.get().getName());
            }
        }

        final ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());

        try {
            WynnventoryMod.LOGGER.info("marketItems Size: {}", marketItems.size());
            WynnventoryMod.LOGGER.error("marketItems Size: {}", marketItems.size());
            System.out.println("marketItems Size: "+ marketItems.size());
            if(!marketItems.isEmpty()) {
                sendResults(mapper.writeValueAsString(marketItems));
            }
        } catch (JsonProcessingException e) {
            WynntilsMod.error("Failed to send data to remote endpoint due to: " + e.getMessage());
        }
    }

    @Unique
    private void sendResults(String payload) {
        WynnventoryMod.LOGGER.info("Sending data...");
        WynnventoryMod.LOGGER.error("Sending data...");
        System.out.println("Sending data...");
        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.wynnventory.com/api/trademarket/items"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        CompletableFuture<HttpResponse<String>> responseFuture = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        responseFuture.thenApply(HttpResponse::body)
                .thenAccept(responseBody -> WynnventoryMod.LOGGER.info("Response body: {}", responseBody))
                .exceptionally(e -> {
                    WynnventoryMod.LOGGER.error("Failed to send data: {}", e.getMessage());
                    return null;
                });
    }
}
