package com.bhoomidarpan.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RegistrationResponse {
    private Long id;
    private String propertyCode;
    private String buyerName;
    private String status;
    private LocalDateTime appointmentDate;
    private String ocrResult;
    private String blockchainHash;
    private String sellerName;
    private String sellerAadhaar;
    private String sellerPan;

}