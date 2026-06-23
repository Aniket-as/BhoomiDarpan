package com.bhoomidarpan.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AiService {

    private final WebClient webClient;

    public AiService(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("https://bhoomidarpan-ai-1.onrender.com")
                .build();
    }

    /* ================= FULL PROPERTY ANALYSIS ================= */

    public Map<String, Object> analyzeProperty(Map<String, Object> requestData) {

        try {
            return webClient.post()
                    .uri("/property-analysis")  // Python combined endpoint
                    .bodyValue(requestData)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(3))
                    .block();

        } catch (Exception e) {

            log.error("AI Service Failed: {}", e.getMessage());

            // Safe fallback
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("predicted_price", 0.0);
            fallback.put("confidence_score", 0.0);
            fallback.put("risk_score", 0.0);
            fallback.put("risk_level", "UNKNOWN");
            fallback.put("reason", "AI service unavailable");

            return fallback;
        }
    }

    public Map<String, Object> detectAnomaly(Map<String, Object> requestData) {

        try {
            return webClient.post()
                    .uri("/detect-anomaly")
                    .bodyValue(requestData)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

        } catch (Exception e) {

            log.error("AI Anomaly Service Failed: {}", e.getMessage());

            Map<String, Object> fallback = new HashMap<>();
            fallback.put("anomaly", false);
            fallback.put("reason", "AI service unavailable");

            return fallback;
        }
    }
}
