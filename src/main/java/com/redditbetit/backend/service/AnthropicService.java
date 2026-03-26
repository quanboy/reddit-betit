package com.redditbetit.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redditbetit.backend.dto.TrendQueryParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class AnthropicService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${anthropic.api.key}")
    private String apiKey;

    @Value("${anthropic.model}")
    private String model;

    private static final String SYSTEM_PROMPT = """
            You are a sports betting trend query parser. Extract structured parameters from the user's natural language query.
            Return ONLY valid JSON - no markdown, no backticks, no preamble - matching this exact schema:
            {
              "queryType": "PROP_TREND" | "STAT_TREND" | "ATS_MATCHUP" | "ATS_SITUATION",
              "sport": "NBA" | "NFL" | "MLB" | null,
              "playerName": string | null,
              "teamName": string | null,
              "opponentName": string | null,
              "stat": string | null,
              "propLine": number | null,
              "sampleSize": number,
              "season": string | null
            }
            Rules:
            - If sampleSize is not specified, default to 15.
            - stat should be snake_case: points, assists, rebounds, passing_yards, rushing_yards, receiving_yards, hits, home_runs, strikeouts_pitcher, etc.
            - If sport cannot be inferred, set it to null.
            - For ATS queries with no specific opponent, use ATS_SITUATION.
            """;

    public AnthropicService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.anthropic.com")
                .build();
        this.objectMapper = objectMapper;
    }

    public TrendQueryParams parseQuery(String rawQuery) {
        try {
            var body = Map.of(
                    "model", model,
                    "max_tokens", 512,
                    "system", SYSTEM_PROMPT,
                    "messages", List.of(Map.of("role", "user", "content", rawQuery))
            );

            var response = webClient.post()
                    .uri("/v1/messages")
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            var content = (List<Map<String, Object>>) response.get("content");
            var text = (String) content.get(0).get("text");

            return objectMapper.readValue(text, TrendQueryParams.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not parse query: " + e.getMessage(), e);
        }
    }

    public String generateInsight(TrendQueryParams params, int hits, int sampleSize, Double avgStat) {
        try {
            String userPrompt = String.format(
                    "Trend: %s %s, %d/%d hit rate (%.0f%%). Avg stat: %s. Write a 2-sentence plain-english insight for a sports bettor.",
                    params.playerName() != null ? params.playerName() : params.teamName(),
                    params.stat() != null ? params.stat() : "ATS",
                    hits, sampleSize,
                    (double) hits / sampleSize * 100,
                    avgStat != null ? String.format("%.1f", avgStat) : "N/A"
            );

            var body = Map.of(
                    "model", model,
                    "max_tokens", 150,
                    "messages", List.of(Map.of("role", "user", "content", userPrompt))
            );

            var response = webClient.post()
                    .uri("/v1/messages")
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            var content = (List<Map<String, Object>>) response.get("content");
            return (String) content.get(0).get("text");
        } catch (Exception e) {
            return "Trend data computed successfully.";
        }
    }
}
