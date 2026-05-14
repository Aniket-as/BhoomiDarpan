package com.bhoomidarpan.service;

import com.bhoomidarpan.dto.PropertyDTO;
import com.bhoomidarpan.entity.Document;
import com.bhoomidarpan.entity.Ownership;
import com.bhoomidarpan.entity.Property;
import com.bhoomidarpan.entity.User;
import com.bhoomidarpan.entity.enums.*;
import com.bhoomidarpan.exception.BhoomiDarpanException;
import com.bhoomidarpan.repository.*;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PropertyService {


    private final PropertyRepository propertyRepository;
    private final OwnershipRepository ownershipRepository;
    private final FileUploadService fileUploadService;
    private final DisputeService disputeService;
    private final TransactionRepository transactionRepository;
    private final MutationRepository mutationRepository;
    private final UserService userService;
    private final CertificateRepository certificateRepository;
    private final BlockchainService blockchainService;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;

    // =====================================================
    // 🔥 CREATE PROPERTY WITH DOCUMENTS
    // =====================================================
    @Transactional
    public Property createPropertyWithDocuments(
            PropertyDTO dto,
            MultipartFile sevenTwelveFile,
            MultipartFile saleDeedFile,
            List<MultipartFile> otherFiles,
            User adminUser) {

        if (adminUser == null) {
            throw new BhoomiDarpanException("Unauthorized access");
        }

        if (sevenTwelveFile == null || sevenTwelveFile.isEmpty()) {
            throw new BhoomiDarpanException("7/12 document is mandatory");
        }

        if (saleDeedFile == null || saleDeedFile.isEmpty()) {
            throw new BhoomiDarpanException("Sale Deed document is mandatory");
        }

        // Validate uniqueness
        if (propertyRepository.findByPropertyCode(dto.getPropertyCode()).isPresent()) {
            throw new BhoomiDarpanException("Property code already exists");
        }

        if (propertyRepository
                .findBySurveyNumberAndGatNumber(dto.getSurveyNumber(), dto.getGatNumber())
                .isPresent()) {
            throw new BhoomiDarpanException("Land already registered");
        }

        // Find owner
        User owner = userService
                .findByAadhaarNumber(dto.getOwnerAadhaar())
                .orElseThrow(() -> new BhoomiDarpanException("Owner not found"));

        // Create property (NEW until verification)
        Property property = Property.builder()
                .propertyCode(dto.getPropertyCode())
                .location(dto.getLocation())
                .surveyNumber(dto.getSurveyNumber())
                .gatNumber(dto.getGatNumber())
                .landType(dto.getLandType())
                .status(PropertyStatus.CLEAR) // 🔥 Changed from CLEAR
                .area(dto.getArea())
                .build();

        Property savedProperty = propertyRepository.save(property);

        // Ownership
        Ownership ownership = Ownership.builder()
                .property(savedProperty)
                .user(owner)
                .ownershipPercentage(100.0)
                .ownershipType(OwnershipType.SINGLE)
                .current(true)
                .build();

        ownershipRepository.save(ownership);



        // ================= HASH CALCULATION =================

        try {

            byte[] saleDeedBytes = saleDeedFile.getBytes();

            String documentHash =
                    blockchainService.calculateDocumentHash(saleDeedBytes);

            savedProperty.setDocumentHash(documentHash);
            propertyRepository.save(savedProperty);

            // ================= BLOCKCHAIN WRITE =================

            String txHash =
                    blockchainService.registerPropertyOnBlockchain(
                            savedProperty,
                            adminUser
                    );

            savedProperty.setBlockchainTxHash(txHash);


            propertyRepository.save(savedProperty);

        } catch (Exception e) {
            throw new BhoomiDarpanException(
                    "Blockchain registration failed: " + e.getMessage()
            );
        }

        // Save documents
        saveDocument(sevenTwelveFile, savedProperty, DocumentType.SEVEN_TWELVE);
        saveDocument(saleDeedFile, savedProperty, DocumentType.SALE_DEED);

        if (otherFiles != null) {
            for (MultipartFile file : otherFiles) {
                saveDocument(file, savedProperty, DocumentType.TAX_RECEIPT);
            }
        }

        return savedProperty;
    }


    public List<Property> getUserProperties(String aadhaar) {

        User user = userRepository.findByAadhaarNumber(aadhaar)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return propertyRepository.findPropertiesByOwner(user.getId());
    }

    public Property getByCode(String code) {
        return propertyRepository.findByPropertyCode(code)
                .orElse(null);
    }

    // =====================================================
    // 🔥 SAVE DOCUMENT
    // =====================================================
    private void saveDocument(
            MultipartFile file,
            Property property,
            DocumentType type) {

        if (file == null || file.isEmpty()) return;

        if (!"application/pdf".equalsIgnoreCase(file.getContentType())) {
            throw new BhoomiDarpanException("Only PDF files allowed");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BhoomiDarpanException("File size must be less than 5MB");
        }

        try {
            String fileUrl = fileUploadService.uploadFile(file);

            Document doc = new Document();
            doc.setProperty(property);
            doc.setFileUrl(fileUrl);
            doc.setDocumentType(type.name()); // ✅ FIXED
            doc.setVerified(true);

            documentRepository.save(doc);

        } catch (Exception e) {
            throw new BhoomiDarpanException(
                    "Failed to upload file: " + file.getOriginalFilename()
            );
        }
    }


    public String getOwnerNameByPropertyCode(String propertyCode) {

        Property property = propertyRepository
                .findByPropertyCode(propertyCode)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        List<Ownership> owners =
                ownershipRepository.findCurrentOwnersWithUser(property.getId());

        if (owners.isEmpty()) {
            return "No Owner Found";
        }

        // For single owner
        return owners.get(0).getUser().getName();
    }

    @Transactional
    public boolean toggleSaleStatus(String propertyCode, Long userId) {

        Property property = propertyRepository.findByPropertyCode(propertyCode)
                .orElseThrow(() -> new BhoomiDarpanException("Property not found"));

        // 🔥 Check ownership
        boolean isOwner = ownershipRepository
                .existsByPropertyIdAndUserIdAndCurrentTrue(
                        property.getId(),
                        userId
                );

        if (!isOwner) {
            throw new BhoomiDarpanException("You are not the owner of this property");
        }

        // 🔥 Only CLEAR property can be listed
        if (property.getStatus() != PropertyStatus.CLEAR) {
            throw new BhoomiDarpanException("Only CLEAR properties can be listed for sale");
        }

        // 🔥 Cannot list if active dispute
        if (disputeService.hasActiveDispute(property.getId())) {
            throw new BhoomiDarpanException("Property has active dispute");
        }

        // 🔥 Cannot list if mutation pending
        if (mutationRepository.existsByPropertyIdAndStatus(
                property.getId(),
                MutationStatus.PENDING
        )) {
            throw new BhoomiDarpanException("Property has pending mutation");
        }

        // 🔥 Cannot list if active transaction
        if (transactionRepository.existsByPropertyIdAndTransactionStatus(
                property.getId(),
                TransactionStatus.ACTIVE
        )) {
            throw new BhoomiDarpanException("Property already in active transaction");
        }

        // 🔥 Toggle
        property.setAvailableForSale(!property.isAvailableForSale());

        propertyRepository.save(property);

        return property.isAvailableForSale();
    }


    @Transactional(readOnly = true)
    public Map<String, Object> analyzePropertyForAI(String propertyCode) {

        Property property = propertyRepository.findByPropertyCode(propertyCode)
                .orElseThrow(() -> new BhoomiDarpanException("Property not found"));

        int disputeCount = (int) disputeService.countActiveDisputes(property.getId());
        int transactionCount = (int) transactionRepository.countByPropertyId(property.getId());

        double priceDeviation = 10.0; // dummy for now

        return Map.of(
                "dispute_count", disputeCount,
                "transaction_count", transactionCount,
                "price_deviation", priceDeviation
        );
    }

    @Transactional(readOnly = true)
    public Map<String, Object> prepareAiRequest(String propertyCode) {

        Property property = propertyRepository.findByPropertyCode(propertyCode)
                .orElseThrow(() -> new BhoomiDarpanException("Property not found"));

        // SAFE: ownerships loaded inside transaction
        int ownerCount = (int) ownershipRepository
                .countByPropertyIdAndCurrentTrue(property.getId());

        Map<String, Object> request = new HashMap<>();
        request.put("area", property.getArea());
        request.put("land_type", property.getLandType().name());
        request.put("status", property.getStatus().name());
        request.put("available_for_sale", property.isAvailableForSale());
        request.put("owner_count", ownerCount);

        return request;
    }


    public List<Property> getAvailablePropertiesExcludingOwner(
            String search,
            Long currentUserId) {

        if (search == null || search.trim().isEmpty()) {
            return propertyRepository
                    .findClearPropertiesExcludingOwner(currentUserId);
        }

        return propertyRepository
                .searchClearPropertiesExcludingOwner(search, currentUserId);
    }

    // =====================================================
    // OTHER METHODS (UNCHANGED LOGIC)
    // =====================================================

    public List<Document> getDocumentsByProperty(Long propertyId) {
        return documentRepository.findByPropertyId(propertyId);
    }

    public Property getPropertyByCode(String code) {
        return propertyRepository
                .findByPropertyCodeWithDocuments(code)
                .orElseThrow(() -> new RuntimeException("Property not found"));
    }

    public List<Property> getAvailableProperties(String search) {
        if (search == null || search.trim().isEmpty()) {
            return propertyRepository.findByStatus(PropertyStatus.CLEAR);
        }
        return propertyRepository.searchAvailableProperties(search);
    }

    public List<Property> getPropertiesByOwner(Long ownerId) {
        return propertyRepository.findPropertiesByOwner(ownerId);
    }

    public boolean isPropertyAvailableForSale(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new BhoomiDarpanException("Property not found"));

        if (property.getStatus() != PropertyStatus.CLEAR) {
            return false;
        }

        return !disputeService.hasActiveDispute(propertyId);
    }

    @Transactional
    public void updatePropertyStatus(Long propertyId, PropertyStatus status) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new BhoomiDarpanException("Property not found"));

        property.setStatus(status);
        propertyRepository.save(property);
    }

    @Transactional(readOnly = true)
    public List<Ownership> getCurrentOwners(Long propertyId) {
        return ownershipRepository.findCurrentOwnersWithUser(propertyId);
    }

    public List<Property> getVerifiedPropertiesForBuy(String area) {
        if (area == null || area.trim().isEmpty()) {
            return propertyRepository.findClearPropertiesWithoutDispute();
        }
        return propertyRepository.findClearPropertiesWithoutDisputeByArea(area);
    }

    public long countPropertiesByOwner(Long userId) {
        return ownershipRepository.countByUserId(userId);
    }

    public long countActiveTransactions(Long userId) {
        return transactionRepository.countByBuyerIdAndTransactionStatus(
                userId,
                TransactionStatus.ACTIVE
        );
    }



    public long countPendingMutations(Long userId) {
        return mutationRepository.countByUserIdAndStatus(
                userId,
                MutationStatus.PENDING
        );
    }

    public long countCertificates(Long userId) {
        return certificateRepository.countByUserId(userId);
    }

    public User getUserByEmail(String email) {
        return userService.getUserByEmail(email);
    }
}
