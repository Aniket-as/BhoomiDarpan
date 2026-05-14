package com.bhoomidarpan.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class RegistrationVerificationRequest {
    private Long registrationId;
    private boolean approve;
    private String remarks;
    private MultipartFile buyerPhoto;
    private MultipartFile buyerFingerprint;
    private MultipartFile sellerPhoto;
    private MultipartFile sellerFingerprint;
}