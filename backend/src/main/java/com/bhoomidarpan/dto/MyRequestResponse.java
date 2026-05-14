package com.bhoomidarpan.dto;

import lombok.Data;

@Data
public class MyRequestResponse {
    private Long id;
    private String requestType;   // BUY, INHERITANCE, DISPUTE
    private String propertyCode;
    private String location;
    private String status;
    private String createdAt;
    private String redirectUrl;   // frontend navigation
}
