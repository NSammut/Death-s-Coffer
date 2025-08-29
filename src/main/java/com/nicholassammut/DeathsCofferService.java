package com.nicholassammut;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import javax.inject.Inject;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

@Slf4j
public class DeathsCofferService {

    @Inject
    public DeathsCofferService(Gson gson, OkHttpClient httpClient) {
        this.gson = gson;
        this.httpClient = httpClient;
    }

    private static final String API_BASE_URL = "https://osrsdeathscoffer.ddns.net";
    private final Gson gson;
    private final OkHttpClient httpClient;
    private String rsn;

    public void getCofferValue(String targetRsn, Consumer<Long> callback) {

        rsn = targetRsn.replaceAll(" ", "%20");
        rsn = rsn.replace("\u00A0", "%20");

        Request request = new Request.Builder()
                .url(API_BASE_URL + "/lookup/" + rsn)
                .addHeader("Content-Type", "application/json")
                .get()
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                log.error("Request to grab coffer value failed: {}", e.getMessage());
                callback.accept(null);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 404) {
                    log.info("Player not found: {}", rsn);
                    callback.accept(null);
                }

                if (response.isSuccessful()) {
                    try (ResponseBody responseBody = response.body()) {
                        JsonObject jsonResponse = gson.fromJson(responseBody.string(), JsonObject.class);
                        long value = jsonResponse.get("coffer_value").getAsLong();
                        log.info("Successfully got coffer value for {}: {}", rsn, value);
                        callback.accept(value);
                    } catch (JsonParseException | NullPointerException e) {
                        log.error("Failed to parse JSON response for {}.", rsn, e);
                        callback.accept(null);
                    }
                } else {
                    log.error("Failed to get coffer value. Status code: {}", response.code());
                    callback.accept(null);
                }
                response.close();
            }
        });
    }

    public void updateCofferValue(String username, long value) {
        JsonObject json = new JsonObject();
        json.addProperty("rsn", username);
        json.addProperty("value", value);

        RequestBody requestBody = RequestBody.create(
                MediaType.get("application/json"),
                gson.toJson(json)
        );

        Request request = new Request.Builder()
                .url(API_BASE_URL + "/update")
                .addHeader("Content-Type", "application/json") // Specify content type
                .post(requestBody)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                log.error("Failed to update coffer value: {}", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    log.info("Successfully updated coffer value for {}", username);
                } else {
                    log.error("Failed to update coffer value. Status code: {}", response.code());
                }
                response.close();
            };
        });
    }
}