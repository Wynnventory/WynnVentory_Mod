package com.wynnventory.mixin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wynntils.core.WynntilsMod;
import com.wynntils.core.components.Models;
import com.wynntils.core.events.MixinHelper;
import com.wynntils.mc.event.ContainerSetContentEvent;
import com.wynntils.models.items.items.game.GearItem;
import com.wynntils.models.trademarket.TradeMarketModel;
import com.wynntils.models.trademarket.type.TradeMarketPriceInfo;
import com.wynntils.utils.mc.McUtils;
import com.wynnventory.WynnventoryMod;
import com.wynnventory.model.Item.TradeMarketItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
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

    @Inject(
            method =
                    "handleContainerContent(Lnet/minecraft/network/protocol/game/ClientboundContainerSetContentPacket;)V",
            at = @At("HEAD"),
            cancellable = true)
    private void handleContainerContentPre(ClientboundContainerSetContentPacket packet, CallbackInfo ci) {
        WynnventoryMod.LOGGER.info("INJECTOR INJECTED THE INJECTION");
        /*ContainerSetContentEvent event = new ContainerSetContentEvent.Pre(
                packet.getItems(), packet.getCarriedItem(), packet.getContainerId(), packet.getStateId());
        MixinHelper.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }*/

        final List<TradeMarketItem> tradeMarketItems = new ArrayList<>();
        final ObjectMapper mapper = new ObjectMapper();
        final TradeMarketModel tm = new TradeMarketModel();

        for(ItemStack item : packet.getItems()) {
            TradeMarketPriceInfo priceInfo = tm.calculateItemPriceInfo(item);
            Optional<GearItem> gearItemOptional = Models.Item.asWynnItem(item, GearItem.class);

            gearItemOptional.ifPresent(gearItem -> tradeMarketItems.add(new TradeMarketItem(gearItem, priceInfo.price(), priceInfo.amount())));
        }

        try {
            sendResults(mapper.writeValueAsString(tradeMarketItems));
        } catch (JsonProcessingException e) {
            WynntilsMod.error("Failed to send data to remote endpoint due to: " + e.getMessage());
        }

/*        if (!packet.getItems().equals(event.getItems())) {
            if (packet.getContainerId() == 0) {
                McUtils.player()
                        .inventoryMenu
                        .initializeContents(packet.getStateId(), packet.getItems(), packet.getCarriedItem());
            } else if (packet.getContainerId() == McUtils.containerMenu().containerId) {
                McUtils.player()
                        .containerMenu
                        .initializeContents(packet.getStateId(), packet.getItems(), packet.getCarriedItem());
            }

            ci.cancel();
        }*/
    }

    private void sendResults(String payload) {
        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.wynnventory.com/api/trademarket/items"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        CompletableFuture<HttpResponse<String>> responseFuture = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        responseFuture.thenApply(HttpResponse::body)
                .thenAccept(responseBody -> WynntilsMod.info("Response body: " + responseBody))
                .exceptionally(e -> {
                    WynntilsMod.error("Failed to send data: " + e.getMessage());
                    return null;
                });
    }
}
