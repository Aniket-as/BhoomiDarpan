package com.bhoomidarpan.service;

import com.bhoomidarpan.dto.InheritanceRequestDTO;
import com.bhoomidarpan.entity.InheritanceRequest;
import com.bhoomidarpan.entity.Property;
import com.bhoomidarpan.entity.User;
import com.bhoomidarpan.exception.BhoomiDarpanException;
import com.bhoomidarpan.repository.InheritanceRepository;
import com.bhoomidarpan.repository.PropertyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class InheritanceService {

    private final PropertyRepository propertyRepository;
    private final InheritanceRepository inheritanceRepository;

    @Transactional
    public void createRequest(InheritanceRequestDTO dto, User user) {

        if (user == null) {
            throw new BhoomiDarpanException("User not authenticated");
        }

        Property property = propertyRepository.findById(dto.getPropertyId())
                .orElseThrow(() -> new BhoomiDarpanException("Property not found"));

        InheritanceRequest req = InheritanceRequest.builder()
                .property(property)
                .requestedBy(user) // ✅ ENTITY USER
                .deceasedName(dto.getDeceasedName())
                .dateOfDeath(dto.getDateOfDeath())
                .deathCertificate(getBytes(dto.getDeathCertificate()))
                .legalHeirCert(getBytes(dto.getLegalHeirCert()))
                .willDocument(
                        dto.getWillDocument() != null
                                ? getBytes(dto.getWillDocument())
                                : null
                )
                .status("PENDING")
                .build();

        inheritanceRepository.save(req);
    }


    private byte[] getBytes(MultipartFile file) {
        try {
            return file != null ? file.getBytes() : null;
        } catch (Exception e) {
            throw new BhoomiDarpanException("File error");
        }
    }
}

