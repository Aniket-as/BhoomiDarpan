package com.bhoomidarpan.dto;

import lombok.Data;

@Data
public class MutationResponse {
    private Long id;
    private String propertyCode;
    private String mutationNumber;
    private String status;
    private String remarks;
    private String approvedByName;
    private String approvedAt;
    private Long propertyId;
    private String buyerName;
    private String sellerName;
    private String sellerAadhaar;
    private String sellerPan;

}
