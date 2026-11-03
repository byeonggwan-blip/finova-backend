package com.finova.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final JsonMapper jsonMapper;
    private final String apiKey;
    private final String model;

    public GeminiService(
            JsonMapper jsonMapper,
            @Value("${gemini.api-key}") String apiKey,
            @Value("${gemini.model}") String model) {

        this.jsonMapper = jsonMapper;
        this.apiKey = apiKey;
        this.model = model;
    }

    public String generateAnalysis(String prompt) throws Exception {

        String url = "https://generativelanguage.googleapis.com/v1beta/models/"
                + model + ":generateContent";

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );

        String requestJson = jsonMapper.writeValueAsString(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(60))
                .header("Content-Type", "application/json")
                .header("x-goog-api-key", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(
                        requestJson, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );

        if (response.statusCode() != 200) {
            throw new IllegalStateException(
                    "Gemini API 요청 실패 (HTTP "
                            + response.statusCode() + ")"
            );
        }

        JsonNode result = jsonMapper.readTree(response.body());
        JsonNode candidates = result.path("candidates");

        if (candidates.isEmpty()) {
            throw new IllegalStateException("Gemini 응답에 분석 결과가 없습니다.");
        }

        JsonNode parts = candidates.get(0)
                .path("content")
                .path("parts");

        StringBuilder analysis = new StringBuilder();

        for (JsonNode part : parts) {
            if (part.has("text")) {
                analysis.append(part.get("text").asText());
            }
        }

        if (analysis.isEmpty()) {
            throw new IllegalStateException("Gemini 응답 텍스트가 비어 있습니다.");
        }

        return analysis.toString();
    }
}