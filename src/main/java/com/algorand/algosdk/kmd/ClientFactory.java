package com.algorand.algosdk.kmd;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * ClientFactory creates KMD REST API clients.
 */
public class ClientFactory {

    /**
     * Create a new Client. This uses retrofit2 under the hood.
     * @param kmdAddress address of KMD REST API (e.g. http://localhost:7833)
     * @param apiToken apiToken for authenticating to KMD Rest API
     * @return
     */
    public static Client create(String kmdAddress, final String apiToken) {
        final String KMD_API_AUTH_HEADER = "X-KMD-API-Token";

        Interceptor interceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder().addHeader(KMD_API_AUTH_HEADER, apiToken).build();
                return chain.proceed(newRequest);
            }
        };

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.interceptors().add(interceptor);
        builder.connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(kmdAddress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        return retrofit.create(Client.class);
    }
}
