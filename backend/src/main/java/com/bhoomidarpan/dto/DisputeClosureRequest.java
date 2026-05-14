package com.bhoomidarpan.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class DisputeClosureRequest {
    private Long disputeId;
    private String closureReason;
    private LocalDate courtOrderDate;
    private MultipartFile judgmentOrder;
    private MultipartFile settlementDeed;
}