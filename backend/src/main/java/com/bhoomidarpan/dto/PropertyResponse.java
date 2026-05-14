package com.bhoomidarpan.dto;

import lombok.Data;

@Data
public class PropertyResponse {
    private Long id;
    private String propertyCode;
    private String location;
    private String status;
    private String landType;
    private Double area;
    private String createdAt;
    private boolean availableForSale;
}
