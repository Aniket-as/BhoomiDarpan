package com.bhoomidarpan.dto;


import lombok.Data;

@Data
public class TransactionResponse {

    private Long transactionId;
    private String propertyCode;
    private String propertyLocation;

    private String buyerName;
    private String sellerName;

    private String currentStage; // Registration / Mutation / Dispute / Completed
    private String status;       // IN_PROGRESS / ON_HOLD / FINALIZED
}

