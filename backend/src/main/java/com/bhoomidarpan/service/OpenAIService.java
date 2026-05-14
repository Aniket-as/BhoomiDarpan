package com.bhoomidarpan.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class OpenAIService {

    @Value("${huggingface.api.key}")
    private String apiKey;

    // ✅ FINAL WORKING URL
    private final String URL =
            "https://api-inference.huggingface.co/models/mistralai/Mistral-7B-Instruct-v0.2";

    public String askAI(String userMessage) {

        try {
            RestTemplate restTemplate = new RestTemplate();

            // ✅ HuggingFace expects "inputs"
            Map<String, Object> body = Map.of(
                    "inputs", "You are BhoomiDarpan assistant. " + userMessage
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(body, headers);

            ResponseEntity<Object> response = restTemplate.exchange(
                    URL,
                    HttpMethod.POST,
                    entity,
                    Object.class
            );

            // ✅ Parse response
            List<Map<String, Object>> list =
                    (List<Map<String, Object>>) response.getBody();

            String output = list.get(0).get("generated_text").toString();

            return output;

        } catch (Exception e) {
            e.printStackTrace();
            return getFallbackResponse(userMessage);
        }
    }

    private String getFallbackResponse(String msg) {

        msg = msg.toLowerCase();

        if (msg.contains("mutation")) {
            return "Mutation means transfer of ownership in land records.";
        }

        if (msg.contains("7/12")) {
            return "7/12 extract is a land ownership document in Maharashtra.";
        }

        if (msg.contains("blockchain")) {
            return "Blockchain ensures secure and tamper-proof property records.";
        }

        if (msg.contains("register")) {
            return "Upload 7/12 and sale deed to register property.";
        }

        return "AI temporarily unavailable 🤖";
    }
}