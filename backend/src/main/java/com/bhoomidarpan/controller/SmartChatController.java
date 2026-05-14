package com.bhoomidarpan.controller;

import com.bhoomidarpan.entity.Property;
import com.bhoomidarpan.service.OpenAIService;
import com.bhoomidarpan.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
@CrossOrigin("*")
public class SmartChatController {

    @Autowired
    private OpenAIService aiService;

    @Autowired
    private PropertyService propertyService;

    @PostMapping
    public Map<String, String> chat(@RequestBody Map<String, String> req,
                                    @AuthenticationPrincipal UserDetails userDetails) {

        String message = req.get("message").toLowerCase();

        try {

            // 🔥 HANDLE NULL USER (IMPORTANT)
            if (userDetails != null && message.contains("my properties")) {

                List<Property> props = propertyService.getUserProperties(userDetails.getUsername());

                if (props.isEmpty()) {
                    return Map.of("reply", "You have no properties.");
                }

                StringBuilder reply = new StringBuilder("🏠 Your Properties:\n\n");

                for (Property p : props) {
                    reply.append("• ")
                            .append(p.getPropertyCode())
                            .append(" - ")
                            .append(p.getLocation())
                            .append("\n");
                }

                return Map.of("reply", reply.toString());
            }

            // 🔥 PROPERTY CHECK
            if (message.contains("prop")) {
                String code = extractPropertyCode(message);

                Property p = propertyService.getByCode(code);

                if (p == null) {
                    return Map.of("reply", "Property not found.");
                }

                return Map.of("reply",
                        "🏠 Property: " + p.getPropertyCode() +
                                "\n📍 Location: " + p.getLocation() +
                                "\n📊 Status: " + p.getStatus());
            }

            // 🔥 AI FALLBACK
            String aiReply = aiService.askAI(message);
            return Map.of("reply", aiReply);

        } catch (Exception e) {
            e.printStackTrace(); // VERY IMPORTANT FOR DEBUG
            return Map.of("reply", "Something went wrong ❌");
        }
    }

    private String extractPropertyCode(String msg) {
        return msg.toUpperCase().replaceAll("[^A-Z0-9-]", "");
    }
}
