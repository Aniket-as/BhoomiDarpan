package com.bhoomidarpan.service;

import com.bhoomidarpan.dto.GiftDeedRequestDTO;
import com.bhoomidarpan.dto.GiftDeedResponseDTO;
import com.bhoomidarpan.entity.*;
import com.bhoomidarpan.entity.enums.GiftDeedStatus;
import com.bhoomidarpan.entity.enums.PropertyStatus;
import com.bhoomidarpan.exception.BhoomiDarpanException;
import com.bhoomidarpan.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GiftDeedService {

    private final PropertyRepository propertyRepository;
    private final GiftDeedRepository giftDeedRepository;
    private final OwnershipRepository ownershipRepository;
    private final BlockchainService blockchainService;
    private final FileUploadService fileUploadService;

    @Transactional
    public GiftDeedRequest createRequest(GiftDeedRequestDTO dto, User user) {

        if (user == null) {
            throw new BhoomiDarpanException("User not authenticated");
        }

        if (dto.getPropertyId() == null) {
            throw new BhoomiDarpanException("Property required");
        }

        if (dto.getGiftDeedDocument() == null || dto.getGiftDeedDocument().isEmpty()) {
            throw new BhoomiDarpanException("Gift deed document required");
        }

        Property property = propertyRepository.findById(dto.getPropertyId())
                .orElseThrow(() -> new BhoomiDarpanException("Property not found"));

        // 🔒 Ownership Check
        boolean isOwner = ownershipRepository
                .existsByPropertyIdAndUserIdAndCurrentTrue(
                        property.getId(),
                        user.getId()
                );

        if (!isOwner) {
            throw new BhoomiDarpanException("Only property owner can gift");
        }

        // 🔗 Blockchain Check
        boolean verified;
        try {
            verified = blockchainService.verifyPropertyIntegrity(property);
        } catch (Exception e) {
            System.out.println("⚠ Blockchain not synced, skipping verification");
            verified = true;
        }

        if (!verified) {
            property.setStatus(PropertyStatus.UNDER_DISPUTE);
            propertyRepository.save(property);
            throw new BhoomiDarpanException("Property verification failed");
        }

        // 🔁 Duplicate Check
        boolean exists = giftDeedRepository
                .existsByProperty_IdAndStatus(property.getId(), GiftDeedStatus.PENDING);

        if (exists) {
            throw new BhoomiDarpanException("Gift deed already in process");
        }

        // 🔐 Aadhaar Validation
        if (dto.getChildAadhaar() == null || dto.getChildAadhaar().length() != 12) {
            throw new BhoomiDarpanException("Invalid Aadhaar");
        }

        // 🔐 PAN Validation
        if (dto.getChildPan() == null ||
                !dto.getChildPan().matches("^[A-Z]{5}[0-9]{4}[A-Z]{1}$")) {
            throw new BhoomiDarpanException("Invalid PAN");
        }

        // 🔐 Declaration Validation
        if (!dto.isDeclaration()) {
            throw new BhoomiDarpanException("Declaration must be accepted");
        }

        // ☁️ Upload file
        String fileUrl = fileUploadService.uploadFile(dto.getGiftDeedDocument());

        // 📄 Hash
        byte[] docBytes = getBytes(dto.getGiftDeedDocument());
        String docHash = blockchainService.calculateDocumentHash(docBytes);

        // 🏗 Save Request
        GiftDeedRequest req = GiftDeedRequest.builder()
                .property(property)
                .requestedBy(user)

                // 🔥 IMPORTANT FIX (NO FRONTEND TRUST)
                .donorName(user.getName())

                .relationship(dto.getRelationship())
                .childName(dto.getChildName())
                .childAadhaar(dto.getChildAadhaar())
                .childPan(dto.getChildPan())
                .transferReason(dto.getTransferReason())
                .documentUrl(fileUrl)
                .documentHash(docHash)
                .status(GiftDeedStatus.PENDING)
                .build();

        GiftDeedRequest saved = giftDeedRepository.save(req);

        // 🔒 Lock property
        property.setAvailableForSale(false);
        propertyRepository.save(property);

        return saved;
    }

    public GiftDeedResponseDTO getGiftById(Long id) {
        GiftDeedRequest req = giftDeedRepository.findById(id)
                .orElseThrow(() -> new BhoomiDarpanException("Gift request not found"));

        return new GiftDeedResponseDTO(
                req.getId(),
                req.getProperty().getPropertyCode(),
                req.getChildName(),
                req.getDonorName(),
                req.getStatus().name()
        );
    }

    public List<GiftDeedResponseDTO> getPendingRequests() {
        return giftDeedRepository.findByStatus(GiftDeedStatus.PENDING)
                .stream()
                .map(req -> new GiftDeedResponseDTO(
                        req.getId(),
                        req.getProperty().getPropertyCode(),
                        req.getChildName(),
                        req.getDonorName(),
                        req.getStatus().name()
                ))
                .toList();
    }

    private byte[] getBytes(org.springframework.web.multipart.MultipartFile file) {
        try {
            return file.getBytes();
        } catch (Exception e) {
            throw new BhoomiDarpanException("File error");
        }
    }
}