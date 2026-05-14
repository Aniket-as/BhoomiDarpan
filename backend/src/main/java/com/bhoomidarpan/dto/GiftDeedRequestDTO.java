package com.bhoomidarpan.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class GiftDeedRequestDTO {

    private Long propertyId;

    // Donor
    private String donorName;
    private String relationship;

    // Child (Donee)
    private String childName;
    private String childAadhaar;
    private String childPan;

    // Optional
    private String transferReason;

    // Document
    private MultipartFile giftDeedDocument;

    private boolean declaration;
}