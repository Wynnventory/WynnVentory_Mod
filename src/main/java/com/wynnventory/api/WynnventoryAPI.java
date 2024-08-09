package com.wynnventory.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.wynntils.core.components.Models;
import com.wynntils.models.items.WynnItem;
import com.wynntils.models.items.items.game.GearItem;
import com.wynntils.models.trademarket.type.TradeMarketPriceInfo;
import com.wynnventory.WynnventoryMod;
import com.wynnventory.model.item.TradeMarketItem;
import com.wynnventory.model.item.TradeMarketItemPriceInfo;
import com.wynnventory.util.TradeMarketPriceParser;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Unique;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class WynnventoryAPI {
    private static final String BASE_URL = "https://www.wynnventory.com";
    private static final String API_IDENTIFIER = "api";
    private static final URI API_BASE_URL;

    private static final HttpClient httpClient = HttpClient.newHttpClient();


    static {
        try {
            API_BASE_URL = new URI(BASE_URL).resolve("/" + API_IDENTIFIER + "/");
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid URL format", e);
        }
    }

    private static URI getEndpointURI(String endpoint) {
        return API_BASE_URL.resolve(endpoint);
    }

    public void sendTradeMarketResults(ItemStack item) {
        sendTradeMarketResults(List.of(item));
    }

    @Unique
    public void sendTradeMarketResults(List<ItemStack> items) {
        if(items.isEmpty()) {
            return;
        }

        final List<TradeMarketItem> marketItems = new ArrayList<>();
        final URI endpointURI = getEndpointURI("trademarket/items");
        final ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new Jdk8Module());

        for(ItemStack item : items) {
            Optional<GearItem> gearItemOptional = Models.Item.asWynnItem(item, GearItem.class);

            if (gearItemOptional.isPresent()) {
                TradeMarketPriceInfo priceInfo = TradeMarketPriceParser.calculateItemPriceInfo(item);

                if (priceInfo != TradeMarketPriceInfo.EMPTY) {
                    marketItems.add(new TradeMarketItem(gearItemOptional.get(), priceInfo.price(), priceInfo.amount()));
                    WynnventoryMod.LOGGER.info("Received item {}", gearItemOptional.get().getName());
                }
            }
        }

        if(marketItems.isEmpty()) {
            return;
        }

        String payload;
        try {
            payload = mapper.writeValueAsString(marketItems);
        } catch (JsonProcessingException e) {
            WynnventoryMod.LOGGER.error("Failed to serialize market items", e);
            payload = "{}";
        }

        final HttpRequest request = HttpRequest.newBuilder()
                .uri(endpointURI)
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

    public TradeMarketItemPriceInfo fetchItemPriceForItem(ItemStack item) {
        Optional<GearItem> gearItemOptional = Models.Item.asWynnItem(item, GearItem.class);

        return gearItemOptional.map(gearItem -> fetchItemPriceForItem(gearItem.getName())).orElse(null);
    }

    public TradeMarketItemPriceInfo fetchItemPriceForItem(String itemName) {
        final String encodedItemName = URLEncoder.encode(itemName, StandardCharsets.UTF_8);
        final URI endpointURI = getEndpointURI("trademarket/item/" + encodedItemName + "/price");

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(endpointURI)
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Check if the response status code is 200 OK
            if (response.statusCode() == 200) {
                return new ObjectMapper().readValue(response.body(), new com.fasterxml.jackson.core.type.TypeReference<List<TradeMarketItemPriceInfo>>() {}).getFirst();
            } else {
                WynnventoryMod.error("Failed to deserialize item price!" + response.body());
                return null;
            }
        } catch (Exception e) {
            WynnventoryMod.error("Failed to fetch item price from API: " + e.getMessage());
        }

        return null;
    }
}
