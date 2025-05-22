package com.wynnventory.util;

import com.wynnventory.api.ApiConfig;
import com.wynnventory.core.ModInfo;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class HttpUtil {
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public static void sendHttpPostRequest(URI uri, String payload) {
        String key = ApiConfig.getApiKey();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Authorization",  "Api-Key " + key)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        CompletableFuture<HttpResponse<String>> responseFuture = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        responseFuture.thenApply(HttpResponse::body)
                .exceptionally(e -> {
                    ModInfo.logError("Failed to send data: {}", e);
                    return null;
                });
    }

    public static HttpResponse<String> sendHttpGetRequest(URI uri) throws IOException, InterruptedException {
        String key = ApiConfig.getApiKey();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Authorization",  "Api-Key " + key)
                .header("Accept", "application/json")
                .GET()
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static String encodeName(String name) {
        return URLEncoder.encode(name, StandardCharsets.UTF_8).replace("+", "%20");
    }
}