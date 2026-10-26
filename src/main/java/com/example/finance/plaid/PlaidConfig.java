package com.example.finance.plaid;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.plaid.client.request.PlaidApi;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class PlaidConfig {

    @Value("${app.plaid.environment}")
    private String environment;

    @Bean
    public PlaidApi plaidApi() {
        String baseUrl = environment.equals("sandbox")
                ? "https://sandbox.plaid.com/"
                : "https://production.plaid.com/";

        Gson gson = new GsonBuilder()
        .registerTypeAdapter(OffsetDateTime.class,
                (JsonSerializer<OffsetDateTime>) (src, type, ctx) ->
                        ctx.serialize(src.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)))
        .registerTypeAdapter(OffsetDateTime.class,
                (JsonDeserializer<OffsetDateTime>) (json, type, ctx) ->
                        OffsetDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME))
        .registerTypeAdapter(LocalDate.class,
                (JsonSerializer<LocalDate>) (src, type, ctx) ->
                        ctx.serialize(src.format(DateTimeFormatter.ISO_LOCAL_DATE)))
        .registerTypeAdapter(LocalDate.class,
                (JsonDeserializer<LocalDate>) (json, type, ctx) ->
                        LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE))
        .create();

        OkHttpClient httpClient = new OkHttpClient.Builder().build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient)
                .build();

        return retrofit.create(PlaidApi.class);
    }
}