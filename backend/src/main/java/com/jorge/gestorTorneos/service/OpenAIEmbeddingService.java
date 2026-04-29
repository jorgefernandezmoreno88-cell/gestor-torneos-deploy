package com.jorge.gestorTorneos.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class OpenAIEmbeddingService {

    private final RestClient restClient;
    private final String model;

    public OpenAIEmbeddingService(
            @Value("${openai.api.key}") String apiKey,
            @Value("${openai.embedding.model}") String model) {

        this.model = model;

        this.restClient = RestClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @SuppressWarnings("unchecked")
    public List<Double> generarEmbedding(String texto) {
        Map<String, Object> body = Map.of(
                "model", model,
                "input", texto
        );

        Map<String, Object> response = restClient.post()
                .uri("/embeddings")
                .body(body)
                .retrieve()
                .body(Map.class);

        List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");

        if (data == null || data.isEmpty()) {
            throw new RuntimeException("OpenAI no devolvió embedding");
        }

        return (List<Double>) data.get(0).get("embedding");
    }
}