package com.bhoomidarpan.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
public class InheritanceRequestDTO {

    private Long propertyId;
    private String deceasedName;
    private LocalDate dateOfDeath;

    private MultipartFile deathCertificate;
    private MultipartFile legalHeirCert;
    private MultipartFile willDocument;
}

