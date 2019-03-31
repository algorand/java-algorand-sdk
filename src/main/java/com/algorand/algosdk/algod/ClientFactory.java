package com.algorand.algosdk.algod;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * ClientFactory creates Algod REST API clients.
 */
public class ClientFactory {

    /**
     * Create a new Client. This uses retrofit2 under the hood.
     * @param algodAddress address of AlgoD REST API (e.g. http://localhost:8080)
     * @param apiToken apiToken for authenticating to AlgoD Rest API
     * @return
     */
    public static Client create(String algodAddress, final String apiToken) {
        final String ALGOD_API_AUTH_HEADER = "X-Algo-API-Token";

        Interceptor interceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder().addHeader(ALGOD_API_AUTH_HEADER, apiToken).build();
                return chain.proceed(newRequest);
            }
        };

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.interceptors().add(interceptor);
        OkHttpClient client = builder.build();
        builder.connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(algodAddress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        return retrofit.create(Client.class);
    }
}
