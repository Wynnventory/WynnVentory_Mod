package com.wynnventory.api;

import com.wynnventory.core.ModInfo;

import java.net.URI;

public final class ApiConfig {
    private static final String PROD_BASE = "https://www.wynnventory.com/api/";
    private static final String DEV_BASE  = "https://wynn-ventory-dev-2a243523ab77.herokuapp.com/api/";

    private static final URI PROD_URI = URI.create(PROD_BASE);
    private static final URI DEV_URI  = URI.create(DEV_BASE);

    private ApiConfig() {}

    public static URI baseUri() {
        return ModInfo.isDev() ? DEV_URI : PROD_URI;
    }
}