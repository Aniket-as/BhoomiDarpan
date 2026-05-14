package com.bhoomidarpan.controller;

import com.bhoomidarpan.dto.AiAnalysisResponse;
import com.bhoomidarpan.service.AiService;
import com.bhoomidarpan.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;
    private final PropertyService propertyService;

    @GetMapping("/property/{propertyCode}/analysis")
    public ResponseEntity<AiAnalysisResponse> analyzeProperty(
            @PathVariable String propertyCode) {

        Map<String, Object> request =
                propertyService.prepareAiRequest(propertyCode);

        Map<String, Object> aiResult =
                aiService.analyzeProperty(request);

        AiAnalysisResponse response = AiAnalysisResponse.builder()
                .predictedPrice(
                        ((Number) aiResult.getOrDefault("predicted_price", 0)).doubleValue()
                )
                .confidenceScore(
                        ((Number) aiResult.getOrDefault("confidence_score", 0)).doubleValue()
                )
                .riskScore(
                        ((Number) aiResult.getOrDefault("risk_score", 0)).doubleValue()
                )
                .riskLevel(
                        String.valueOf(aiResult.getOrDefault("risk_level", "UNKNOWN"))
                )
                .reason(
                        String.valueOf(aiResult.getOrDefault("reason", "AI unavailable"))
                )
                .build();

        return ResponseEntity.ok(response);
    }
}