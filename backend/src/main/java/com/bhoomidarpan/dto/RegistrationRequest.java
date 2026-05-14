package com.bhoomidarpan.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
public class RegistrationRequest {
    private Long propertyId;
    private LocalDateTime appointmentDate;
    private MultipartFile saleDeed;
}
