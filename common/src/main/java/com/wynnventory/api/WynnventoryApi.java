package com.wynnventory.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wynnventory.core.WynnventoryMod;
import com.wynnventory.util.HttpUtil;

import java.net.URI;
import java.net.http.HttpResponse;
import java.util.function.Function;

public class WynnventoryApi  {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    // TODO: sendTradeMarketResults

    // TODO: sendGambitItems

    // TODO: sendLootpoolData

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
        HttpUtil.sendHttpPostRequest(uri, serialize(payload));
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