package com.wynnventory.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.wynntils.core.components.Models;
import com.wynntils.models.items.WynnItem;
import com.wynntils.models.items.WynnItemData;
import com.wynntils.models.items.items.game.GearItem;
import com.wynntils.models.items.items.game.InsulatorItem;
import com.wynntils.models.items.items.game.SimulatorItem;
import com.wynntils.models.items.properties.GearTierItemProperty;
import com.wynntils.models.trademarket.type.TradeMarketPriceInfo;
import com.wynntils.utils.mc.McUtils;
import com.wynnventory.WynnventoryMod;
import com.wynnventory.model.item.LootpoolItem;
import com.wynnventory.model.item.TradeMarketItem;
import com.wynnventory.model.item.TradeMarketItemPriceInfo;
import com.wynnventory.util.HttpUtil;
import com.wynnventory.util.ItemStackUtils;
import com.wynnventory.util.RegionDetector;
import com.wynnventory.util.TradeMarketPriceParser;
import net.minecraft.world.item.ItemStack;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WynnventoryAPI {
    private static final String BASE_URL = "https://www.wynnventory.com";
    private static final String API_IDENTIFIER = "api";
    private static final URI API_BASE_URL = createApiBaseUrl();
    private static final ObjectMapper objectMapper = createObjectMapper();

    public void sendTradeMarketResults(List<ItemStack> items) {
        if (items.isEmpty()) return;

        List<TradeMarketItem> marketItems = createTradeMarketItems(items);

        if (marketItems.isEmpty()) return;

        URI endpointURI;
        if (WynnventoryMod.isDev()) {
            WynnventoryMod.info("Sending market data data to DEV endpoint.");
//            endpointURI = getEndpointURI("trademarket/items?env=dev2");
            endpointURI = getEndpointURI("https://wynn-ventory-dev-2a243523ab77.herokuapp.com/api/trademarket/items?env=dev2");
        } else {
            endpointURI = getEndpointURI("trademarket/items");
        }
        HttpUtil.sendHttpPostRequest(endpointURI, serializeItemData(marketItems));
    }

    public void sendLootpoolData(List<ItemStack> items) {
        if (items == null || items.isEmpty()) return;

        List<LootpoolItem> lootpoolItems = createLootpoolItems(items);
        String serializedData = serializeItemData(lootpoolItems);
        if (serializedData.equals("[]")) return;

        URI endpointURI;
        if (WynnventoryMod.isDev()) {
            WynnventoryMod.info("Sending lootpool data to DEV endpoint.");
//            endpointURI = getEndpointURI("lootpool/items?env=dev2");
            endpointURI = URI.create("https://wynn-ventory-dev-2a243523ab77.herokuapp.com/api/lootpool/items?env=dev2");
        } else {
            endpointURI = getEndpointURI("lootpool/items");
        }
        HttpUtil.sendHttpPostRequest(endpointURI, serializedData);
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

            HttpResponse<String> response = HttpUtil.sendHttpGetRequest(endpointURI);

            if (response.statusCode() == 200) {
                return parsePriceInfoResponse(response.body());
            } else if (response.statusCode() == 404) {
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

    private List<LootpoolItem> createLootpoolItems(List<ItemStack> items) {
        List<LootpoolItem> lootpoolItems = new ArrayList<>();
        String region = RegionDetector.getRegion(McUtils.player().getBlockX(), McUtils.player().getBlockZ());

        for (ItemStack item : items) {
            Optional<WynnItem> wynnItemOptional = Optional.ofNullable(ItemStackUtils.getWynnItem(item));

            wynnItemOptional.ifPresent(wynnItem -> {
                if (LootpoolItem.LOOT_CLASSES.contains(wynnItem.getClass())) {
                    String shiny = null;
                    String name = ItemStackUtils.getWynntilsOriginalName(wynnItem.getData().get(WynnItemData.ITEMSTACK_KEY)).getLastPart().getComponent().getString();
                    String rarity = null;
                    String type = null;

                    if (wynnItem instanceof GearItem gearItem) {
                        if (name.contains("Shiny")) {
                            shiny = "Shiny";
                        }
                        name = gearItem.getName();
                        rarity = gearItem.getGearTier().getName();
                        type = gearItem.getGearType().name();
                    }
                    if (wynnItem instanceof SimulatorItem || wynnItem instanceof InsulatorItem) {
                        rarity = ((GearTierItemProperty) wynnItem).getGearTier().getName();
                    }

                    LootpoolItem lootpoolItem = new LootpoolItem(
                            wynnItem.getClass().getSimpleName(),
                            region,
                            ((ItemStack) wynnItem.getData().get(WynnItemData.ITEMSTACK_KEY)).getCount(),
                            name,
                            rarity,
                            shiny,
                            type,
                            McUtils.playerName()
                    );

                    lootpoolItems.add(lootpoolItem);
                } else {
                    WynnventoryMod.error("Unknown class: " + wynnItem.getClass());
                }
            });
        }
        return lootpoolItems;
    }

    private String serializeItemData(List<?> items) {
        try {
            return objectMapper.writeValueAsString(items);
        } catch (JsonProcessingException e) {
            WynnventoryMod.LOGGER.error("Failed to serialize item data", e);
            return "[]";
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
}