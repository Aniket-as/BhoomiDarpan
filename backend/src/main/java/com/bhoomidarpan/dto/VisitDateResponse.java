package com.bhoomidarpan.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class VisitDateResponse {
    private String propertyCode;
    private String location;
    private String buyerName;
    private String ownerName;
    private LocalDate visitDate;
    private String timeSlot;
    private String status;
}
