package com.wynnventory.util;

import com.wynnventory.api.ApiConfig;
import com.wynnventory.core.WynnventoryMod;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class HttpUtils {
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    private HttpUtils() {}

    public static CompletableFuture<Void> sendPostRequest(URI uri, String jsonPayload) {
        HttpRequest request = baseRequest(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        return send(request)
                .thenAccept(resp -> {
                    int code = resp.statusCode();
                    if (code < 200 || code >= 300) {
                        WynnventoryMod.logError("Failed to POST to endpoint '{}'. Code '{}', Reason '{}'", uri, code, resp.body());
                    }
                });
    }

    public static CompletableFuture<HttpResponse<String>> sendGetRequest(URI uri) {
        HttpRequest request = baseRequest(uri)
                .header("Accept", "application/json")
                .GET()
                .build();

        return send(request)
                .whenComplete((resp, ex) -> {
                    int code = resp.statusCode();
                    if (code < 200 || code >= 300) {
                        WynnventoryMod.logError("Failed to GET from endpoint '{}'. Code '{}', Reason '{}'", uri, code, resp.body());
                    }
                });
    }

    private static HttpRequest.Builder baseRequest(URI uri) {
        String key = ApiConfig.getApiKey();
        return HttpRequest.newBuilder()
                .uri(uri)
                .header("Authorization", "Api-Key " + key);
    }

    private static CompletableFuture<HttpResponse<String>> send(HttpRequest request) {
        return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }
}