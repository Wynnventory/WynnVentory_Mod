package com.wynnventory.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wynnventory.core.WynnventoryMod;
import com.wynnventory.model.item.simple.SimpleItem;
import com.wynnventory.model.reward.RewardPool;
import com.wynnventory.model.reward.RewardPoolDocument;
import com.wynnventory.util.HttpUtils;

import java.net.URI;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class WynnventoryApi  {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule());

    // TODO: sendTradeMarketResults

    // TODO: sendGambitItems

    public void sendRewardPoolData(Map<RewardPool, Set<SimpleItem>> drainedPools) {
        URI uri = Endpoint.LOOTPOOL_ITEMS.uri();

        for (Map.Entry<RewardPool, Set<SimpleItem>> entry : drainedPools.entrySet()) {
            RewardPool pool = entry.getKey();
            Set<SimpleItem> itemsSet = entry.getValue();
            if (pool == null || itemsSet == null || itemsSet.isEmpty()) continue;

            RewardPoolDocument doc = new RewardPoolDocument(new ArrayList<>(itemsSet), pool.getFullName(), pool.getType().name());
            WynnventoryMod.logDebug("Trying to send {} items for RewardPool {}", itemsSet.size(), pool.getShortName());

            HttpUtils.sendPostRequest(uri, serialize(doc));
        }
    }

    // TODO: sendRaidpoolData

    // TODO: fetchItemPrice (by name)

    // TODO: fetchItemPrice (by name and tier)

    // TODO: fetchLootpools (RAID or LOOTPOOL)

    // TODO: fetchLatestHistoricItemPrice (by name)

    // TODO: fetchLatestHistoricItemPrice (by name and tier)

    // TODO: parsePriceInfoResponse

    // TODO: parseLootpoolResponse

    private <T> T handleResponse(HttpResponse<String> resp, Function<String, T> on200) {
        if (resp.statusCode() == 200) {
            return on200.apply(resp.body());
        } else {
            WynnventoryMod.logError("API error ({}): {}", resp.statusCode(), resp.body());
        }

        return null;
    }

    private void post(URI uri, Object payload) {
        HttpUtils.sendPostRequest(uri, serialize(payload));
    }

    private String serialize(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            WynnventoryMod.logError("Serialization failed", e);
            return "[]";
        }
    }


}