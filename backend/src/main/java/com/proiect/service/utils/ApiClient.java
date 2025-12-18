package com.proiect.service.utils;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class ApiClient {
    private static final Logger logger = LoggerFactory.getLogger(ApiClient.class);
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build();
    /**
     * Efectueaza o cerere GET cu reincercari.
     * @param url URL-ul
     * @param headers headerele
     * @param maxRetries numarul maxim de reincercari
     * @return corpul de raspuns optional
     */
    public Optional<String> get(String url, Map<String, String> headers, int maxRetries) {
        var requestBuilder = new Request.Builder().url(url);
        headers.forEach(requestBuilder::addHeader);
        var request = requestBuilder.build();
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try (var response = client.newCall(request).execute()) {
                logger.debug("ApiClient GET attempt {}: {}", attempt, url);
                if (response.isSuccessful()) {
                    var body = response.body().string();
                    if (body != null && !body.trim().isEmpty()) {
                        logger.debug("ApiClient response: {}", body);
                        return Optional.of(body);
                    }
                }
                logger.warn("HTTP error on attempt {}: {} {}", attempt, response.code(), response.message());
            } catch (IOException e) {
                logger.warn("IOException on attempt {}: {}", attempt, e.getMessage());
            }
            if (attempt < maxRetries) {
                try {
                    Thread.sleep(1000 * attempt);  // Backoff simplu
                } catch (InterruptedException ignored) {}
            }
        }
        return Optional.empty();
    }
    /**
     * Efectueaza o cerere GET simpla.
     * @param url URL-ul
     * @return corpul de raspuns
     * @throws Exception daca esueaza
     */
    public String get(String url) throws Exception {
        return get(url, Map.of(), 1).orElseThrow(() -> new Exception("Failed to get response"));
    }
    /**
     * Efectueaza o cerere POST cu reincercari.
     * @param url URL-ul
     * @param jsonBody corpul JSON
     * @param headers headerele
     * @param maxRetries numarul maxim de reincercari
     * @return corpul de raspuns optional
     */
    public Optional<String> post(String url, String jsonBody, Map<String, String> headers, int maxRetries) {
        var body = RequestBody.create(jsonBody, MediaType.get("application/json"));
        var requestBuilder = new Request.Builder().url(url).post(body);
        headers.forEach(requestBuilder::addHeader);
        var request = requestBuilder.build();
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try (var response = client.newCall(request).execute()) {
                logger.debug("ApiClient POST attempt {}: {}", attempt, url);
                if (response.isSuccessful()) {
                    var responseBody = response.body().string();
                    if (responseBody != null && !responseBody.trim().isEmpty()) {
                        logger.debug("ApiClient response: {}", responseBody);
                        return Optional.of(responseBody);
                    }
                }
                logger.warn("HTTP error on attempt {}: {} {}", attempt, response.code(), response.message());
            } catch (IOException e) {
                logger.warn("IOException on attempt {}: {}", attempt, e.getMessage());
            }
            if (attempt < maxRetries) {
                try {
                    Thread.sleep(1000 * attempt);  // Backoff simplu
                } catch (InterruptedException ignored) {}
            }
        }
        return Optional.empty();
    }
}