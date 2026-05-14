package com.bhoomidarpan.service;

import com.bhoomidarpan.dto.OCRResponse;
import com.bhoomidarpan.entity.*;
import com.bhoomidarpan.entity.enums.*;
import com.bhoomidarpan.exception.BhoomiDarpanException;
import com.bhoomidarpan.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final DisputeRepository disputeRepository;
    private final OwnershipRepository ownershipRepository;
    private final BlockchainService blockchainService;
    private final AiService aiService;
    private final MutationRepository mutationRepository;
    private final TransactionRepository transactionRepository;
    private final BuyRequestRepository buyRequestRepository;
    private final OCRService ocrService;
    private final FileUploadService fileUploadService;
    private final DocumentRepository documentRepository;

    // =====================================================
    // VERIFY REGISTRATION (SUB-REGISTRAR UPLOADS FILES)
    // =====================================================
    @Transactional
    public Registration verifyRegistration(
            Long registrationId,
            Boolean approve,
            String remarks,
            MultipartFile saleDeed,
            MultipartFile buyerPhoto,
            MultipartFile sellerPhoto,
            MultipartFile buyerFingerprint,
            MultipartFile sellerFingerprint,
            User officer
    ) {

        if (officer.getRole() != Role.SUB_REGISTRAR) {
            throw new BhoomiDarpanException("Only Sub-Registrar allowed");
        }

        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new BhoomiDarpanException("Registration not found"));

        if (registration.getStatus() == RegistrationStatus.APPROVED) {
            throw new BhoomiDarpanException("Registration already approved");
        }

        if (!approve) {
            registration.setStatus(RegistrationStatus.REJECTED);
            registration.setApprovedBy(officer);
            registration.setApprovedAt(LocalDateTime.now());
            return registrationRepository.save(registration);
        }

        Property property = registration.getProperty();

        if (property.getStatus() != PropertyStatus.CLEAR) {
            throw new BhoomiDarpanException("Property is not available for transfer");
        }

        if (!disputeRepository.findActiveDisputes(property.getId()).isEmpty()) {
            throw new BhoomiDarpanException("Property has active disputes");
        }

        boolean blockchainExecuted = false;
        String blockchainHash = null;

        List<Ownership> currentOwners =
                ownershipRepository.findCurrentOwners(property.getId());

        if (currentOwners.isEmpty()) {
            throw new BhoomiDarpanException("No current owner found");
        }

        User seller = currentOwners.get(0).getUser();

        try {

        /* =====================================================
           STEP 1 — OCR VALIDATION
        ===================================================== */

            if (saleDeed == null || saleDeed.isEmpty()) {
                throw new BhoomiDarpanException("Sale deed is required");
            }

            OCRResponse ocrResponse =
                    ocrService.extractTextFromDocument(saleDeed, "SALE_DEED");

            boolean isValid = ocrService.validateSaleDeed(
                    ocrResponse.getExtractedText(),
                    registration.getBuyer().getName(),
                    seller.getName()
            );

            if (!isValid) {
                throw new BhoomiDarpanException(
                        "OCR validation failed: Buyer/Seller mismatch"
                );
            }

        /* =====================================================
           STEP 2 — SAVE DOCUMENTS
        ===================================================== */

            /* STEP 2 — SAVE DOCUMENTS (CLOUD) */

            String deedUrl = fileUploadService.uploadFile(saleDeed);
            registration.setSaleDeedPath(deedUrl);

// 🔥 ADD THIS BLOCK (MOST IMPORTANT)
            Document doc = new Document();
            doc.setProperty(property);
            doc.setFileUrl(deedUrl);
            doc.setDocumentType("SALE_DEED");
            doc.setVerified(true);

            documentRepository.save(doc);
            registration.setSaleDeedHash(
                    blockchainService.calculateDocumentHash(saleDeed.getBytes())
            );

// ✅ SAVE URL IN ENTITY (IMPORTANT)
            if (buyerPhoto != null && !buyerPhoto.isEmpty()) {
                registration.setBuyerPhotoUrl(
                        fileUploadService.uploadFile(buyerPhoto)
                );
            }

            if (sellerPhoto != null && !sellerPhoto.isEmpty()) {
                registration.setSellerPhotoUrl(
                        fileUploadService.uploadFile(sellerPhoto)
                );
            }

            if (buyerFingerprint != null && !buyerFingerprint.isEmpty()) {
                registration.setBuyerFingerprintUrl(
                        fileUploadService.uploadFile(buyerFingerprint)
                );
            }

            if (sellerFingerprint != null && !sellerFingerprint.isEmpty()) {
                registration.setSellerFingerprintUrl(
                        fileUploadService.uploadFile(sellerFingerprint)
                );
            }

        /* =====================================================
           STEP 3 — BLOCKCHAIN TRANSFER
        ===================================================== */

            blockchainHash =
                    blockchainService.transferOwnershipOnBlockchain(
                            property,
                            seller,
                            registration.getBuyer().getWalletAddress(),
                            officer
                    );

            blockchainExecuted = true;

            registration.setBlockchainHash(blockchainHash);

        /* =====================================================
           STEP 4 — OWNERSHIP TRANSFER (DB)
        ===================================================== */

            for (Ownership oldOwner : currentOwners) {

                oldOwner.setCurrent(false);
                oldOwner.setEndDate(LocalDateTime.now());

                ownershipRepository.save(oldOwner);
            }

            Ownership newOwnership = new Ownership();

            newOwnership.setProperty(property);
            newOwnership.setUser(registration.getBuyer());
            newOwnership.setCurrent(true);
            newOwnership.setOwnershipPercentage(100.0);
            newOwnership.setOwnershipType(OwnershipType.SINGLE);
            newOwnership.setStartDate(LocalDateTime.now());

            ownershipRepository.save(newOwnership);

        /* =====================================================
           STEP 5 — PROPERTY TRANSACTION
        ===================================================== */

            PropertyTransaction transaction = new PropertyTransaction();

            transaction.setProperty(property);
            transaction.setSeller(seller);
            transaction.setBuyer(registration.getBuyer());
            transaction.setTransactionStatus(TransactionStatus.COMPLETED);
            transaction.setAnomalyFlag(false);
            transaction.setRiskLevel("LOW");
            transaction.setCreatedAt(LocalDateTime.now());

            transactionRepository.save(transaction);

        /* =====================================================
           STEP 6 — UPDATE BUY REQUEST
        ===================================================== */

            BuyRequest buyRequest = buyRequestRepository
                    .findByProperty_IdAndBuyer_IdAndStatus(
                            property.getId(),
                            registration.getBuyer().getId(),
                            BuyRequestStatus.APPROVED
                    )
                    .orElse(null);

            if (buyRequest != null) {

                buyRequest.setStatus(BuyRequestStatus.REGISTERED);

                buyRequestRepository.save(buyRequest);
            }

        /* =====================================================
           STEP 7 — CREATE MUTATION
        ===================================================== */

            boolean mutationExists =
                    mutationRepository.findByRegistrationId(registration.getId())
                            .isPresent();

            if (!mutationExists) {

                Mutation mutation = Mutation.builder()
                        .property(property)
                        .registration(registration)
                        .mutationNumber(
                                "MUT/" + LocalDateTime.now().getYear()
                                        + "/" + System.currentTimeMillis()
                        )
                        .status(MutationStatus.PENDING)
                        .build();

                mutationRepository.save(mutation);
            }

        /* =====================================================
           STEP 8 — FINAL APPROVAL
        ===================================================== */

            registration.setStatus(RegistrationStatus.APPROVED);
            registration.setApprovedBy(officer);
            registration.setApprovedAt(LocalDateTime.now());

            return registrationRepository.save(registration);

        } catch (Exception e) {

            e.printStackTrace();

        /* =====================================================
           REVERSE BLOCKCHAIN TRANSFER
        ===================================================== */

            if (blockchainExecuted) {

                try {

                    blockchainService.reverseOwnershipTransfer(
                            property,
                            seller.getWalletAddress(),
                            officer
                    );

                } catch (Exception reverseException) {

                    System.out.println("CRITICAL: Reverse blockchain failed");
                    reverseException.printStackTrace();
                }
            }

            throw new BhoomiDarpanException(
                    "Verification failed: " + e.getMessage()
            );
        }
    }

    private String getCurrentSellerName(Registration registration) {
        return registration.getProperty()
                .getOwnerships()
                .stream()
                .filter(Ownership::isCurrent)
                .findFirst()
                .map(o -> o.getUser().getName())
                .orElse("");
    }

    @Transactional(readOnly = true)
    public List<Registration> getApprovedWithoutMutation() {
        return registrationRepository.findApprovedWithoutMutation();
    }


    // =====================================================
    // FINAL APPROVAL + BLOCKCHAIN WRITE
    // =====================================================
    @Transactional
    public Registration approveRegistration(Long registrationId, User officer) {

        if (officer.getRole() != Role.SUB_REGISTRAR) {
            throw new BhoomiDarpanException("Only Sub-Registrar allowed");
        }

        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() ->
                        new BhoomiDarpanException("Registration not found"));

        if (registration.getStatus() != RegistrationStatus.VERIFIED) {
            throw new BhoomiDarpanException("Must be verified first");
        }

        // AI Fraud Detection
        long transferCount =
                ownershipRepository.countByPropertyId(
                        registration.getProperty().getId());

        Map<String, Object> request = new HashMap<>();
        request.put("price", 0.0);
        request.put("transfer_count", transferCount);

        Map<String, Object> response =
                aiService.detectAnomaly(request);

        boolean anomaly =
                Boolean.parseBoolean(response.get("anomaly").toString());

        if (anomaly) {
            registration.setStatus(RegistrationStatus.ON_HOLD);
            return registrationRepository.save(registration);
        }

        registration.setStatus(RegistrationStatus.APPROVED);

        String blockchainHash =
                blockchainService.transferOwnershipOnBlockchain(
                        registration.getProperty(),
                        registration.getBuyer(),
                        registration.getSaleDeedHash(),
                        officer
                );

        registration.setBlockchainHash(blockchainHash);




        return registrationRepository.save(registration);
    }

    // =====================================================
    // TODAY APPOINTMENTS
    // =====================================================
    public List<Registration> getTodaysAppointments() {

        LocalDate today = LocalDate.now();

        return registrationRepository.findTodaysAppointments(today);
    }



    @Transactional
    public Registration verifyGiftDeedRegistration(
            Long registrationId,
            Boolean approve,
            String remarks,
            MultipartFile giftDeed,
            MultipartFile buyerPhoto,
            MultipartFile donorPhoto,
            MultipartFile buyerFingerprint,
            MultipartFile donorFingerprint,
            User officer
    ) {

        if (officer.getRole() != Role.SUB_REGISTRAR) {
            throw new BhoomiDarpanException("Only Sub-Registrar allowed");
        }

        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new BhoomiDarpanException("Registration not found"));

        if (!approve) {
            registration.setStatus(RegistrationStatus.REJECTED);
            registration.setApprovedBy(officer);
            registration.setApprovedAt(LocalDateTime.now());
            return registrationRepository.save(registration);
        }

        Property property = registration.getProperty();
        System.out.println("Property Status: " + property.getStatus());
        if (property.getStatus() != PropertyStatus.CLEAR) {
            throw new BhoomiDarpanException("Property not available");
        }

        if (!disputeRepository.findActiveDisputes(property.getId()).isEmpty()) {
            throw new BhoomiDarpanException("Active disputes exist");
        }

        boolean blockchainExecuted = false;

        List<Ownership> currentOwners =
                ownershipRepository.findCurrentOwners(property.getId());

        if (currentOwners.isEmpty()) {
            throw new BhoomiDarpanException("No current owner");
        }

        User donor = currentOwners.get(0).getUser();

        try {

        /* =====================================================
           STEP 1 — VALIDATION (GIFT DEED)
        ===================================================== */

            if (giftDeed == null || giftDeed.isEmpty()) {
                throw new BhoomiDarpanException("Gift deed required");
            }

        /* =====================================================
           STEP 2 — SAVE DOCUMENTS (CLOUD)
        ===================================================== */

            String deedUrl = fileUploadService.uploadFile(giftDeed);
            registration.setSaleDeedPath(deedUrl); // reuse field

            registration.setSaleDeedHash(
                    blockchainService.calculateDocumentHash(giftDeed.getBytes())
            );

            if (buyerPhoto != null && !buyerPhoto.isEmpty()) {
                registration.setBuyerPhotoUrl(fileUploadService.uploadFile(buyerPhoto));
            }

            if (donorPhoto != null && !donorPhoto.isEmpty()) {
                registration.setSellerPhotoUrl(fileUploadService.uploadFile(donorPhoto));
            }

            if (buyerFingerprint != null && !buyerFingerprint.isEmpty()) {
                registration.setBuyerFingerprintUrl(fileUploadService.uploadFile(buyerFingerprint));
            }

            if (donorFingerprint != null && !donorFingerprint.isEmpty()) {
                registration.setSellerFingerprintUrl(fileUploadService.uploadFile(donorFingerprint));
            }

        /* =====================================================
           STEP 3 — BLOCKCHAIN TRANSFER
        ===================================================== */

            String blockchainHash =
                    blockchainService.transferOwnershipOnBlockchain(
                            property,
                            donor,
                            registration.getBuyer().getWalletAddress(),
                            officer
                    );

            blockchainExecuted = true;
            registration.setBlockchainHash(blockchainHash);

        /* =====================================================
           STEP 4 — OWNERSHIP TRANSFER
        ===================================================== */

            for (Ownership oldOwner : currentOwners) {
                oldOwner.setCurrent(false);
                oldOwner.setEndDate(LocalDateTime.now());
                ownershipRepository.save(oldOwner);
            }

            Ownership newOwnership = new Ownership();
            newOwnership.setProperty(property);
            newOwnership.setUser(registration.getBuyer());
            newOwnership.setCurrent(true);
            newOwnership.setOwnershipPercentage(100.0);
            newOwnership.setOwnershipType(OwnershipType.SINGLE);
            newOwnership.setStartDate(LocalDateTime.now());

            ownershipRepository.save(newOwnership);

        /* =====================================================
           STEP 5 — TRANSACTION ENTRY
        ===================================================== */

            PropertyTransaction transaction = new PropertyTransaction();
            transaction.setProperty(property);
            transaction.setSeller(donor);
            transaction.setBuyer(registration.getBuyer());
            transaction.setTransactionStatus(TransactionStatus.COMPLETED);
            transaction.setRiskLevel("LOW");
            transaction.setCreatedAt(LocalDateTime.now());

            transactionRepository.save(transaction);

        /* =====================================================
           STEP 6 — MUTATION
        ===================================================== */

            Mutation mutation = Mutation.builder()
                    .property(property)
                    .registration(registration)
                    .mutationNumber(
                            "MUT/" + LocalDateTime.now().getYear()
                                    + "/" + System.currentTimeMillis()
                    )
                    .status(MutationStatus.PENDING)
                    .build();

            mutationRepository.save(mutation);

        /* =====================================================
           STEP 7 — FINAL STATUS
        ===================================================== */

            registration.setStatus(RegistrationStatus.APPROVED);
            registration.setApprovedBy(officer);
            registration.setApprovedAt(LocalDateTime.now());

            return registrationRepository.save(registration);

        } catch (Exception e) {

            if (blockchainExecuted) {
                try {
                    blockchainService.reverseOwnershipTransfer(
                            property,
                            donor.getWalletAddress(),
                            officer
                    );
                } catch (Exception ex) {
                    System.out.println("Reverse failed");
                }
            }

            throw new BhoomiDarpanException("Gift verification failed: " + e.getMessage());
        }
    }


    @Transactional(readOnly = true)
    public List<Registration> getPendingVerifications() {
        return registrationRepository.findPendingVerifications();
    }

    // =====================================================
    // FILE SAVE METHOD
    // =====================================================

}
