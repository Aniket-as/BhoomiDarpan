package com.bhoomidarpan.dto;


import com.bhoomidarpan.entity.enums.DisputeType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class DisputeRequestDTO {
    private String propertyCode;
    private String caseNumber;
    private String courtName;
    private DisputeType disputeType;
    private String reason;
    private LocalDate filingDate;
    private MultipartFile courtOrder;
    private MultipartFile petitionCopy;
}






