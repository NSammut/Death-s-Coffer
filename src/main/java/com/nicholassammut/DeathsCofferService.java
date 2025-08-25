package com.nicholassammut;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class DeathsCofferService {

    @Inject
    public DeathsCofferService(Gson gson) {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = gson;
    }

    private static final String API_BASE_URL = "https://osrsdeathscoffer.ddns.net";
    private final Gson gson;
    private final HttpClient httpClient;
    private String rsn;

    public CompletableFuture<Long> getCofferValue(String targetRsn) {
        CompletableFuture<Long> future = new CompletableFuture<>();
        rsn = targetRsn.replaceAll(" ", "%20");
        rsn = rsn.replace("\u00A0", "%20");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/lookup/" + rsn))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        CompletableFuture<HttpResponse<String>> futureResponse = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        futureResponse.whenComplete((response, throwable) -> {
            if (throwable != null) {
                log.error("Failed to get coffer value.", throwable);
                future.completeExceptionally(throwable);
                return;
            }

            if (response.statusCode() == 404) {
                // Player not found, so complete the future with null
                log.info("Player not found: {}", rsn);
                future.complete(null);
                return;
            }

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                try {
                    JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);
                    long value = jsonResponse.get("coffer_value").getAsLong();
                    log.info("Successfully got coffer value for {}: {}", rsn, value);
                    future.complete(value);
                } catch (JsonParseException | NullPointerException e) {
                    log.error("Failed to parse JSON response for {}.", rsn, e);
                    future.completeExceptionally(e);
                }
            } else {
                log.error("Failed to get coffer value. Status code: {}", response.statusCode());
                future.completeExceptionally(new IOException("Server returned an error."));
            }
        });

        return future;
    }

    public void updateCofferValue(String username, long value) {
        JsonObject json = new JsonObject();
        json.addProperty("rsn", username);
        json.addProperty("value", value);

        // Build the request body
        HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofString(json.toString());

        // Build the HttpRequest with the API endpoint, headers, and body
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/update"))
                .header("Content-Type", "application/json") // Specify content type
                .POST(bodyPublisher)
                .build();

        // Send the request asynchronously
        CompletableFuture<HttpResponse<String>> futureResponse = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        // Handle the response when it's available
        futureResponse.whenComplete((response, throwable) -> {
            if (throwable != null) {
                // An error occurred during the request
                log.error("Failed to update coffer value: {}", throwable.getMessage());
            } else {
                // The request was successful (200-level response)
                if (response.statusCode() >= 200 && response.statusCode() < 300) {
                    log.info("Successfully updated coffer value for {}", username);
                } else {
                    // An error occurred on the server side
                    log.error("Failed to update coffer value. Status code: {}", response.statusCode());
                }
            }
        });

    }
}
