package com.bhoomidarpan.dto;

import lombok.Data;

import java.util.List;

@Data
public class PropertyDetailResponse {

    private Long id;
    private String propertyCode;
    private String location;
    private String surveyNumber;
    private String gatNumber;
    private String landType;
    private String status;
    private Double area;
    private String createdAt;

    private List<OwnerInfo> owners;
    private List<DocumentInfo> documents;

    @Data
    public static class OwnerInfo {
        private Long userId;
        private String name;
        private Double ownershipPercentage;
        private String ownershipType;
    }

    @Data
    public static class DocumentInfo {
        private Long id;
        private String documentType;
        private String fileUrl;
        private boolean verified;
    }

    private boolean availableForSale;
}
