package com.example.finance.plaid;

import com.plaid.client.request.PlaidApi;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Configuration
public class PlaidConfig {

    @Value("${app.plaid.environment}")
    private String environment;

    @Bean
    public PlaidApi plaidApi() {
        String baseUrl = environment.equals("sandbox")
                ? "https://sandbox.plaid.com/"
                : "https://production.plaid.com/";

        OkHttpClient httpClient = new OkHttpClient.Builder().build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        return retrofit.create(PlaidApi.class);
    }
}