package com.bhoomidarpan.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MyAppointmentResponse {
    private Long requestId;
    private String propertyCode;
    private String location;
    private LocalDate visitDate;
    private String timeSlot;
    private String status;
    private String role; // BUYER or OWNER
}

