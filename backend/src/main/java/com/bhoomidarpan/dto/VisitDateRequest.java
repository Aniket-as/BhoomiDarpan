package com.bhoomidarpan.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class VisitDateRequest {
    private LocalDate visitDate;
    private LocalTime timeSlot;
}
