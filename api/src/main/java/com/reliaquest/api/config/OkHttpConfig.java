package com.reliaquest.api.config;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;

public class OkHttpConfig {

    public static OkHttpClient configureClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }
}
