package com.bhoomidarpan.dto;

import lombok.Data;

@Data
public class OCRResponse {
    private boolean success;
    private String extractedText;
    private java.util.Map<String, String> extractedFields;
    private String validationStatus;
    private java.util.List<String> validationErrors;
}