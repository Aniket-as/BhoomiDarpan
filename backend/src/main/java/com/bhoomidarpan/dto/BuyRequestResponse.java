package com.bhoomidarpan.dto;

import lombok.Data;

@Data
public class BuyRequestResponse {
    private Long id;
    private String propertyCode;
    private String buyerName;
    private Double offeredPrice;
    private String status;
    private String createdAt;
    private java.util.List<ConsentStatus> consents;
    private String currentUserRole; // BUYER or OWNER
    private Long myConsentId;       // Only for owner
    private String myConsentStatus; // Only for owner

    private String ownerName;
    private String ownerEmail;
    private String buyerEmail;
    private String visitDate;
    private String location;
    private String landType;
    private String propertyStatus;
    private Double area;
    private Boolean availableForSale;

    @Data
    public static class ConsentStatus {
        private String ownerName;
        private String status;
    }
}
