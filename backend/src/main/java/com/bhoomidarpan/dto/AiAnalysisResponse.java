package com.bhoomidarpan.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AiAnalysisResponse {
    private Double predictedPrice;
    private Double confidenceScore;
    private Double riskScore;
    private String riskLevel;
    private String reason;
    private String priceTrend;          // NEW
    private Integer similarPropertiesCount; // NEW
    private Boolean documentAnomaly;    // NEW
    private Integer transactionFrequency; // NEW
}