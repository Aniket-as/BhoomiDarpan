package com.bhoomidarpan.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class OCRRequest {
    private MultipartFile document;
    private String documentType; // SALE_DEED, COURT_ORDER, etc.
}

