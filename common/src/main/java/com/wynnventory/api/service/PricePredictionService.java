package com.wynnventory.api.service;

import com.wynnventory.api.WynnventoryApi;
import com.wynnventory.model.item.Expirable;
import com.wynnventory.model.item.simple.SimpleGearItem;
import com.wynnventory.model.item.trademarket.prediction.PricePredictionRequest;
import com.wynnventory.model.item.trademarket.prediction.PricePredictionResponse;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public enum PricePredictionService {
    INSTANCE;

    private final WynnventoryApi api = new WynnventoryApi();
    private final ConcurrentHashMap<Integer, CachedPrediction> predictions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, CompletableFuture<PricePredictionResponse>> inFlight =
            new ConcurrentHashMap<>();

    PricePredictionService() {}

    public PricePredictionResponse getPrediction(SimpleGearItem gearItem) {
        if (gearItem == null || gearItem.isUnidentified()) return null;

        return getPrediction(PricePredictionRequest.from(gearItem));
    }

    public PricePredictionResponse getPrediction(PricePredictionRequest request) {
        if (request == null || request.getName() == null || request.getName().isBlank()) return null;

        int key = generateHash(request);

        CachedPrediction cached = predictions.get(key);
        if (cached != null && !cached.isExpired()) {
            return cached.response();
        } else if (cached != null) {
            predictions.remove(key, cached);
        }

        inFlight.computeIfAbsent(key, k -> fetchPrediction(request).whenComplete((prediction, ex) -> {
            predictions.put(k, new CachedPrediction(prediction));
            inFlight.remove(k);
        }));

        return null;
    }

    private CompletableFuture<PricePredictionResponse> fetchPrediction(PricePredictionRequest request) {
        return api.fetchPricePrediction(request);
    }

    public int generateHash(PricePredictionRequest request) {
        return Objects.hash(request.getName(), request.getStatRolls(), request.getRerollCount(), request.isShiny());
    }

    private record CachedPrediction(PricePredictionResponse response, Instant createdAt) {
        private CachedPrediction(PricePredictionResponse response) {
            this(response, Instant.now());
        }

        private boolean isExpired() {
            return createdAt.isBefore(Instant.now().minus(Expirable.DATA_LIFESPAN));
        }
    }
}
