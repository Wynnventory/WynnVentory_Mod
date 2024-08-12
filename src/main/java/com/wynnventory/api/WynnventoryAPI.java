package com.wynnventory.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.wynntils.core.components.Models;
import com.wynntils.models.items.items.game.GearItem;
import com.wynntils.models.trademarket.type.TradeMarketPriceInfo;
import com.wynnventory.WynnventoryMod;
import com.wynnventory.model.item.TradeMarketItem;
import com.wynnventory.model.item.TradeMarketItemPriceInfo;
import com.wynnventory.util.TradeMarketPriceParser;
import net.minecraft.world.item.ItemStack;

import java.io.IOException;
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
    private static final URI API_BASE_URL = createApiBaseUrl();

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = createObjectMapper();

    private static URI createApiBaseUrl() {
        try {
            return new URI(BASE_URL).resolve("/" + API_IDENTIFIER + "/");
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid URL format", e);
        }
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        return mapper;
    }

    private static URI getEndpointURI(String endpoint) {
        return API_BASE_URL.resolve(endpoint);
    }

    public void sendTradeMarketResults(ItemStack item) {
        sendTradeMarketResults(List.of(item));
    }

    public void sendTradeMarketResults(List<ItemStack> items) {
        if (items.isEmpty()) return;

        List<TradeMarketItem> marketItems = createTradeMarketItems(items);

        if (marketItems.isEmpty()) return;

        sendHttpPostRequest(getEndpointURI("trademarket/items"), serializeMarketItems(marketItems));
    }

    public TradeMarketItemPriceInfo fetchItemPrices(ItemStack item) {
        return Models.Item.asWynnItem(item, GearItem.class)
                .map(gearItem -> fetchItemPrices(gearItem.getName()))
                .orElse(null);
    }

    public TradeMarketItemPriceInfo fetchItemPrices(String itemName) {
        try {
            String encodedItemName = URLEncoder.encode(itemName, StandardCharsets.UTF_8).replace("+", "%20");
            URI endpointURI = getEndpointURI("trademarket/item/" + encodedItemName + "/price");

            HttpResponse<String> response = sendHttpGetRequest(endpointURI);

            if (response.statusCode() == 200) {
                return parsePriceInfoResponse(response.body());
            } else if (response.statusCode() == 204) {
                return null;
            } else {
                WynnventoryMod.error("Failed to fetch item price from API: " + response.body());
                return null;
            }
        } catch (Exception e) {
            WynnventoryMod.error("Failed to initiate item price fetch {}", e);
            return null;
        }
    }

    private List<TradeMarketItem> createTradeMarketItems(List<ItemStack> items) {
        List<TradeMarketItem> marketItems = new ArrayList<>();

        for (ItemStack item : items) {
            Optional<GearItem> gearItemOptional = Models.Item.asWynnItem(item, GearItem.class);

            gearItemOptional.ifPresent(gearItem -> {
                TradeMarketPriceInfo priceInfo = TradeMarketPriceParser.calculateItemPriceInfo(item);
                if (priceInfo != TradeMarketPriceInfo.EMPTY) {
                    marketItems.add(new TradeMarketItem(gearItem, priceInfo.price(), priceInfo.amount()));
                }
            });
        }

        return marketItems;
    }

    private String serializeMarketItems(List<TradeMarketItem> marketItems) {
        try {
            return objectMapper.writeValueAsString(marketItems);
        } catch (JsonProcessingException e) {
            WynnventoryMod.LOGGER.error("Failed to serialize market items", e);
            return "{}";
        }
    }

    private TradeMarketItemPriceInfo parsePriceInfoResponse(String responseBody) {
        try {
            List<TradeMarketItemPriceInfo> priceInfoList = objectMapper.readValue(responseBody, new com.fasterxml.jackson.core.type.TypeReference<>() {});
            return priceInfoList.isEmpty() ? null : priceInfoList.getFirst();
        } catch (JsonProcessingException e) {
            WynnventoryMod.error("Failed to parse item price response {}", e);
            return null;
        }
    }

    private void sendHttpPostRequest(URI uri, String payload) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        CompletableFuture<HttpResponse<String>> responseFuture = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        responseFuture.thenApply(HttpResponse::body)
                        .exceptionally(e -> {
                            WynnventoryMod.error("Failed to send data: {}", e);
                            return null;
                        });
    }

    private HttpResponse<String> sendHttpGetRequest(URI uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Accept", "application/json")
                .GET()
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    }
}
