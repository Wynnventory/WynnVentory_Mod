package com.wynnventory.api;

import com.wynnventory.core.ModInfo;

import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class ApiConfig {
    private static final String PROD_BASE = "https://www.wynnventory.com/api/";
    private static final String DEV_BASE  = "https://wynn-ventory-dev-2a243523ab77.herokuapp.com/api/";

    private static final URI PROD_URI = URI.create(PROD_BASE);
    private static final URI DEV_URI  = URI.create(DEV_BASE);

    private static final byte MASK = (byte)0x5A;
    private static String apiKey;


    private ApiConfig() {}

    public static URI baseUri() {
        return ModInfo.isDev() ? DEV_URI : PROD_URI;
    }

    public static String getApiKey() {
        if (apiKey != null) return apiKey;

        try (InputStream in = ApiConfig.class.getResourceAsStream("/api_key.dat")) {
            if (in == null) throw new IllegalStateException("Missing api_key.dat!");

            byte[] b64 = in.readAllBytes();
            byte[] ob  = Base64.getDecoder().decode(b64);

            for (int i = 0; i < ob.length; i++) {
                ob[i] ^= MASK;
            }

            apiKey = new String(ob, StandardCharsets.UTF_8);
            return apiKey;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load API key", e);
        }
    }
}