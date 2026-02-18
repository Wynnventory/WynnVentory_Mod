package com.wynnventory.util;

import com.wynnventory.api.ApiConfig;
import com.wynnventory.core.WynnventoryMod;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class HttpUtils {
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    private HttpUtils() {}

    public static void sendPostRequest(URI uri, String jsonPayload) {
        WynnventoryMod.logDebug("Sending data to {} endpoint: {}", WynnventoryMod.isBeta() ? "DEV" : "PROD", uri);
        HttpRequest request;
        try {
            request = baseRequest(uri)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();
        } catch (Exception e) {
            WynnventoryMod.logError("Failed to create POST request for endpoint '{}': {}", uri, e.getMessage());
            return;
        }

        send(request).thenAccept(resp -> {
            int code = resp.statusCode();
            if (code < 200 || code >= 300) {
                WynnventoryMod.logError(
                        "Failed to POST to endpoint '{}'. Code '{}', Reason '{}'", uri, code, resp.body());
            }
        });
    }

    public static CompletableFuture<HttpResponse<String>> sendGetRequest(URI uri) {
        WynnventoryMod.logDebug("Fetching data from {} endpoint: {}", WynnventoryMod.isBeta() ? "DEV" : "PROD", uri);

        HttpRequest request;
        try {
            request =
                    baseRequest(uri).header("Accept", "application/json").GET().build();
        } catch (Exception e) {
            WynnventoryMod.logError("Failed to create GET request for endpoint '{}': {}", uri, e.getMessage());
            return CompletableFuture.completedFuture(null);
        }

        return send(request).whenComplete((resp, ex) -> {
            int code = resp.statusCode();
            if (code < 200 || code >= 300) {
                WynnventoryMod.logError(
                        "Failed to GET from endpoint '{}'. Code '{}', Reason '{}'", uri, code, resp.body());
            }
        });
    }

    public static String encode(String name) {
        return URLEncoder.encode(name, StandardCharsets.UTF_8).replace("+", "%20");
    }

    public static URI withQueryParams(URI baseUri, Map<String, ?> params) {
        if (params == null || params.isEmpty()) {
            return baseUri;
        }

        String query = params.entrySet().stream()
                .filter(e -> e.getValue() != null)
                .map(e -> encode(e.getKey()) + "=" + encode(String.valueOf(e.getValue())))
                .collect(Collectors.joining("&"));

        if (query.isEmpty()) {
            return baseUri;
        }

        String base = baseUri.toString();
        return URI.create(base + (base.contains("?") ? "&" : "?") + query);
    }

    private static HttpRequest.Builder baseRequest(URI uri) {
        String key = ApiConfig.getApiKey();
        return HttpRequest.newBuilder().uri(uri).timeout(TIMEOUT).header("Authorization", "Api-Key " + key);
    }

    private static CompletableFuture<HttpResponse<String>> send(HttpRequest request) {
        return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }
}
