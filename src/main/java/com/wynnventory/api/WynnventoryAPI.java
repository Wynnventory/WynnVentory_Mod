package com.wynnventory.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wynnventory.core.ModInfo;
import com.wynnventory.enums.PoolType;
import com.wynnventory.model.item.GroupedLootpool;
import com.wynnventory.model.item.Lootpool;
import com.wynnventory.model.item.TradeMarketItem;
import com.wynnventory.model.item.TradeMarketItemPriceInfo;
import com.wynnventory.util.HttpUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class WynnventoryAPI {
    private static final ObjectMapper MAPPER = createObjectMapper();

    public void sendTradeMarketResults(List<TradeMarketItem> items) {
        if (items.isEmpty()) return;
        ModInfo.logInfo("Sending market data to {} endpoint.", ModInfo.isDev() ? "DEV" : "PROD");
        URI uri = Endpoint.TRADE_MARKET_ITEMS.uri();
        post(uri, items);
        ModInfo.logInfo("Submitted {} market items to API: {}", items.size(), uri);
    }

    public void sendLootpoolData(List<Lootpool> pools) {
        if (pools.isEmpty()) return;
        ModInfo.logInfo("Sending lootpool data to {} endpoint.", ModInfo.isDev() ? "DEV" : "PROD");
        URI uri = Endpoint.LOOTPOOL_ITEMS.uri();
        for (Lootpool p : pools) {
            post(uri, p);
            ModInfo.logInfo("Submitted {} lootpool items to API: {}", p.getItems().size(), uri);
        }
    }

    public void sendRaidpoolData(List<Lootpool> pools) {
        if (pools.isEmpty()) return;
        ModInfo.logInfo("Sending raidpool data to {} endpoint.", ModInfo.isDev() ? "DEV" : "PROD");
        URI uri = Endpoint.RAIDPOOL_ITEMS.uri();
        for (Lootpool p : pools) {
            post(uri, p);
            ModInfo.logInfo("Submitted {} raidpool items to API: {}", p.getItems().size(), uri);
        }
    }

    public TradeMarketItemPriceInfo fetchItemPrice(String itemName) {
        return fetchItemPrice(itemName, -1);
    }

    public TradeMarketItemPriceInfo fetchItemPrice(String name, int tier) {
        try {
            URI uri = Endpoint.TRADE_MARKET_PRICE
                    .uri(HttpUtil.encodeName(name), tier);
            ModInfo.logInfo("Fetching market data from {} endpoint.", ModInfo.isDev() ? "DEV" : "PROD");
            HttpResponse<String> resp = HttpUtil.sendHttpGetRequest(uri);
            return handleResponse(resp, this::parsePriceInfoResponse);
        } catch (Exception e) {
            ModInfo.logError("Failed to fetch item price", e);
            return null;
        }
    }

    public List<GroupedLootpool> getLootpools(PoolType type) {
        try {
            String path = type.getName() + "/items";
            URI uri = ApiConfig.baseUri().resolve(path);
            ModInfo.logInfo("Fetching {} lootpools from {} endpoint.", type, ModInfo.isDev() ? "DEV" : "PROD");
            HttpResponse<String> resp = HttpUtil.sendHttpGetRequest(uri);
            return handleResponse(resp, this::parseLootpoolResponse, ArrayList::new);
        } catch (Exception e) {
            ModInfo.logError("Failed to fetch lootpools", e);
            return new ArrayList<>();
        }
    }

    public TradeMarketItemPriceInfo fetchLatestHistoricItemPrice(String itemName) {
        return fetchLatestHistoricItemPrice(itemName, -1);
    }

    public TradeMarketItemPriceInfo fetchLatestHistoricItemPrice(String name, int tier) {
        try {
            URI uri = Endpoint.TRADE_MARKET_HISTORY_LATEST
                    .uri(HttpUtil.encodeName(name), tier);
            ModInfo.logInfo("Fetching history from {} endpoint.", ModInfo.isDev() ? "DEV" : "PROD");
            HttpResponse<String> resp = HttpUtil.sendHttpGetRequest(uri);
            return handleResponse(resp, this::parseHistoricPriceInfo);
        } catch (Exception e) {
            ModInfo.logError("Failed to fetch historic price", e);
            return null;
        }
    }

    private <T> T handleResponse(HttpResponse<String> resp,
                                 Function<String, T> on200) {
        return handleResponse(resp, on200, () -> null);
    }

    private <T> T handleResponse(HttpResponse<String> resp, Function<String, T> on200, Supplier<T> on404) {
        if (resp.statusCode() == 200)      return on200.apply(resp.body());
        else if (resp.statusCode() == 404) return on404.get();
        else {
            ModInfo.logError("API error ({}): {}", resp.statusCode(), resp.body());
            return on404.get();
        }
    }

    private void post(URI uri, Object payload) {
        HttpUtil.sendHttpPostRequest(uri, serialize(payload));
    }

    private String serialize(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            ModInfo.logError("Serialization failed", e);
            return "[]";
        }
    }

    private TradeMarketItemPriceInfo parsePriceInfoResponse(String responseBody) {
        try {
            List<TradeMarketItemPriceInfo> priceInfoList = MAPPER.readValue(responseBody, new TypeReference<>() {
            });
            return priceInfoList.isEmpty() ? null : priceInfoList.getFirst();
        } catch (JsonProcessingException e) {
            ModInfo.logError("Failed to parse item price response {}", e);
        }

        return null;
    }

    private List<GroupedLootpool> parseLootpoolResponse(String responseBody) {
        try {
            return MAPPER.readValue(responseBody, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            ModInfo.logError("Failed to parse item price response {}", e);
        }

        return new ArrayList<>();
    }

    private TradeMarketItemPriceInfo parseHistoricPriceInfo(String responseBody) {
        try {
            return MAPPER.readValue(responseBody, TradeMarketItemPriceInfo.class);
        } catch (JsonProcessingException e) {
            ModInfo.logError("Failed to parse historic item price response {}", e);
            return null;
        }
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new Jdk8Module());
        return mapper;
    }
}