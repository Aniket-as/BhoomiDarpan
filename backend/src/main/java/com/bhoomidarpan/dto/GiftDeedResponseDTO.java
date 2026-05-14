package com.bhoomidarpan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GiftDeedResponseDTO {

    private Long id;
    private String propertyCode;
    private String childName;
    private String donorName;
    private String status;
}
