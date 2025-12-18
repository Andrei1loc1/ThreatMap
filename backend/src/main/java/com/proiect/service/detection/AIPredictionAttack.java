package com.proiect.service.detection;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.proiect.model.LogEntry;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import io.github.cdimascio.dotenv.Dotenv;

public class AIPredictionAttack {
    private static final String API_KEY = Dotenv.load().get("OPENROUTER_API_KEY");
    private final WebClient webClient;
    private final Gson gson;

    /**
     * Construieste un AIPredictionAttack.
     */
public AIPredictionAttack() {
        this.webClient = WebClient.builder()
                .baseUrl("https://openrouter.ai/api/v1")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + API_KEY)
                .build();
        this.gson = new Gson();
    }

    /**
     * Determina daca intrarile de log date indica un atac.
     * @param entries lista de intrari de log
     * @return true daca un atac este detectat, false altfel
     */
    public boolean isAttack(List<LogEntry> entries) {
        if (entries.isEmpty()) {
            return false;
        }

        List<LogEntry> failures = entries.stream()
                .filter(entry -> "FAILURE".equalsIgnoreCase(entry.getStatus()))
                .toList();

        if (failures.isEmpty()) {
            return false;
        }

        String prompt = buildPrompt(failures);

        Map<String, Object> requestBody = Map.of(
                "model", "tngtech/deepseek-r1t2-chimera:free",
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        try {
            String response = webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(gson.toJson(requestBody))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return parseResponse(response);
        } catch (Exception e) {
            System.err.println("Error calling AI API: " + e.getMessage());
            return false;
        }
    }
    /**
     * Prezice procentul de pericol bazat pe intrarile de log.
     * @param entries lista de intrari de log
     * @return procentul de pericol prezis
     */
    public int predictDangerPercent(List<LogEntry> entries) {
        if (entries.isEmpty()) {
            return 0;
        }

        List<LogEntry> failures = entries.stream()
                .filter(entry -> "FAILURE".equalsIgnoreCase(entry.getStatus()))
                .toList();

        if (failures.isEmpty()) {
            return 0;
        }

        String prompt = buildRiskPrompt(failures, entries.size());

        Map<String, Object> requestBody = Map.of(
                "model", "mistralai/mistral-7b-instruct",
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        int fallback = heuristicRiskPercent(failures.size(), entries.size());

        try {
            String response = webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(gson.toJson(requestBody))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return parseRiskResponse(response, fallback);
        } catch (Exception e) {
            System.err.println("Error calling AI API for risk: " + e.getMessage());
            return fallback;
        }
    }

    /**
     * Construieste promptul pentru detectarea atacurilor.
     * @param entries lista de intrari de log de esec
     * @return sirul prompt construit
     */
    private String buildPrompt(List<LogEntry> entries) {
        Map<String, Long> countsByIp = entries.stream()
                .collect(Collectors.groupingBy(LogEntry::getAdresaIP, Collectors.counting()));
        String topIps = countsByIp.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(8)
                .map(e -> e.getKey() + " -> " + e.getValue() + " failures")
                .collect(Collectors.joining("; "));
        long uniqueIps = countsByIp.size();

        StringBuilder sb = new StringBuilder();
        sb.append("You are a strict security detector. Decide if these FAILURE log entries indicate an attack (e.g., brute force, credential stuffing, DDoS probe).\n");
        sb.append("Rules of thumb:\n");
        sb.append("- If any IP has 3 or more failures in this slice, answer YES (likely brute force).\n");
        sb.append("- If multiple IPs show failures, lean YES (coordinated probing).\n");
        sb.append("- Only answer NO if there is a single isolated failure with no repetition.\n");
        sb.append("Respond only YES or NO.\n");
        sb.append("Summary: total failures=").append(entries.size())
                .append(", unique failure IPs=").append(uniqueIps)
                .append(", top IPs: ").append(topIps.isBlank() ? "none" : topIps).append("\n");
        sb.append("Failure entries:\n");
        for (LogEntry entry : entries) {
            sb.append(entry.getTimestamp()).append(", ").append(entry.getAdresaIP()).append(", ").append(entry.getStatus()).append("\n");
        }
        sb.append("Do these entries indicate a cyber attack? Answer only YES or NO.");
        return sb.toString();
    }

    /**
     * Construieste promptul pentru predictia procentului de risc.
     * @param failures lista de intrari de log de esec
     * @param totalEntries numarul total de intrari de log
     * @return sirul prompt construit
     */
    private String buildRiskPrompt(List<LogEntry> failures, int totalEntries) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a security scorer. Given these FAILURE log entries, return only a single integer from 0 to 100 that estimates how attacked the platform is right now.\n");
        sb.append("0 = calm/benign traffic, 30-60 = suspicious, 80-100 = active attack.\n");
        sb.append("Maximum output format allowed: just the number, nothing else.\n");
        sb.append("Total log lines considered: ").append(totalEntries).append("\n");
        sb.append("Failure lines:\n");
        for (LogEntry entry : failures) {
            sb.append(entry.getTimestamp()).append(", ").append(entry.getAdresaIP()).append(", ").append(entry.getStatus()).append("\n");
        }
        sb.append("Respond with the risk percentage (0-100) only.");
        return sb.toString();
    }

    /**
     * Parseaza raspunsul AI pentru detectarea atacurilor.
     * @param response sirul de raspuns JSON
     * @return true daca atacul este confirmat, false altfel
     */
    private boolean parseResponse(String response) {
        try {
            JsonObject json = gson.fromJson(response, JsonObject.class);
            String content = json.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString()
                    .trim().toUpperCase();
            return "YES".equals(content);
        } catch (Exception e) {
            System.err.println("Error parsing AI response: " + e.getMessage());
            return false;
        }
    }

    /**
     * Parseaza raspunsul AI pentru procentul de risc.
     * @param response sirul de raspuns JSON
     * @param fallback valoarea de rezerva
     * @return procentul de risc parsat
     */
    private int parseRiskResponse(String response, int fallback) {
        try {
            JsonObject json = gson.fromJson(response, JsonObject.class);
            String content = json.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString()
                    .trim();

            String digitsOnly = content.replaceAll("[^0-9]", "");
            if (digitsOnly.isEmpty()) {
                return fallback;
            }
            int value = Integer.parseInt(digitsOnly);
            return Math.max(0, Math.min(100, value));
        } catch (Exception e) {
            System.err.println("Error parsing AI risk response, using fallback: " + e.getMessage());
            return fallback;
        }
    }

    /**
     * Calculeaza procentul de risc euristica.
     * @param failureCount numarul de esecuri
     * @param totalCount numarul total
     * @return procentul de risc calculat
     */
    private int heuristicRiskPercent(int failureCount, int totalCount) {
        if (totalCount == 0) {
            return 0;
        }
        double ratio = (double) failureCount / totalCount;
        return (int) Math.min(100, Math.round(ratio * 120));
    }
}
