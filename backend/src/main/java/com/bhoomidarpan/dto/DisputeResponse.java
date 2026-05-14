package com.bhoomidarpan.dto;

import lombok.Data;

@Data
public class DisputeResponse {
    private Long id;
    private String disputeCode;
    private String propertyCode;
    private String raisedByName;
    private String caseNumber;
    private String courtName;
    private String disputeType;
    private String status;
    private String createdAt;
    private String ocrValidation;
}