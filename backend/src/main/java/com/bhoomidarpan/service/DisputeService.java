package com.bhoomidarpan.service;

import com.bhoomidarpan.dto.DisputeClosureRequest;
import com.bhoomidarpan.dto.DisputeRequestDTO;
import com.bhoomidarpan.entity.Dispute;
import com.bhoomidarpan.entity.Property;
import com.bhoomidarpan.entity.User;
import com.bhoomidarpan.entity.enums.DisputeStatus;
import com.bhoomidarpan.entity.enums.PropertyStatus;
import com.bhoomidarpan.exception.BhoomiDarpanException;
import com.bhoomidarpan.repository.DisputeRepository;
import com.bhoomidarpan.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DisputeService {

    private final DisputeRepository disputeRepository;
    private final PropertyRepository propertyRepository;
    private final OCRService ocrService;
    private final BlockchainService blockchainService;
    private final FileStorageService fileStorageService;

    @Transactional
    public Dispute createDisputeRequest(DisputeRequestDTO request, User raisedBy) {

        Property property = propertyRepository
                .findByPropertyCode(request.getPropertyCode())
                .orElseThrow(() -> new RuntimeException("Property not found"));

        String ocrValidation = validateCourtDocuments(
                request.getCourtOrder(),
                request.getPetitionCopy()
        );

        String courtOrderFile = fileStorageService.saveFile(
                request.getCourtOrder(),
                "COURT_ORDER"
        );

        String petitionCopyFile = fileStorageService.saveFile(
                request.getPetitionCopy(),
                "PETITION_COPY"
        );

        // Create dispute object FIRST
        Dispute dispute = Dispute.builder()
                .disputeCode("DSP-" + System.currentTimeMillis())
                .property(property)
                .raisedBy(raisedBy)
                .caseNumber(request.getCaseNumber())
                .courtName(request.getCourtName())
                .disputeType(request.getDisputeType())
                .status(DisputeStatus.REQUESTED)
                .courtOrderPath(courtOrderFile)
                .petitionCopyPath(petitionCopyFile)
                .ocrValidation(ocrValidation)
                .createdAt(LocalDateTime.now())
                .build();

        String txHash = null;

        try {

            // Calculate evidence hash
            String evidenceHash = blockchainService.calculateDocumentHash(
                    request.getCourtOrder().getBytes()
            );

            // Write to blockchain
            txHash = blockchainService.raiseDispute(
                    property.getPropertyCode(),   // ✅ CORRECT
                    evidenceHash
            );

            dispute.setBlockchainHash(txHash);

            // Save to DB
            return disputeRepository.save(dispute);

        } catch (Exception e) {
            e.printStackTrace();

            // COMPENSATING TRANSACTION
            if (txHash != null) {
                try {
                    blockchainService.resolveDispute(dispute.getDisputeCode());
                } catch (Exception ex) {
                    System.out.println("Blockchain compensation failed");
                    ex.printStackTrace();
                }
            }

            throw new BhoomiDarpanException(
                    "Dispute creation failed: " + e.getMessage()
            );
        }
    }
    private String validateCourtDocuments(MultipartFile courtOrder, MultipartFile petitionCopy) {
        try {
            // Validate court order
            var courtOrderOCR = ocrService.extractTextFromDocument(courtOrder, "COURT_ORDER");
            if (!courtOrderOCR.isSuccess()) {
                return "OCR_FAILED_COURT_ORDER";
            }

            // Validate petition copy
            var petitionOCR = ocrService.extractTextFromDocument(petitionCopy, "COURT_ORDER");
            if (!petitionOCR.isSuccess()) {
                return "OCR_FAILED_PETITION";
            }

            // Validate court document patterns
            if (!ocrService.validateCourtDocument(courtOrderOCR.getExtractedText())) {
                return "INVALID_COURT_DOCUMENT";
            }

            return "VALID";

        } catch (Exception e) {
            return "OCR_PROCESSING_ERROR";
        }
    }

    public long countActiveDisputes(Long propertyId) {
        return disputeRepository.countByPropertyIdAndStatusNot(
                propertyId,
                DisputeStatus.CLOSED
        );
    }

    public List<Dispute> getDisputesByStatus(DisputeStatus status) {
        return disputeRepository.findByStatusWithProperty(status);
    }
    public List<Dispute> getActiveDisputesForProperty(Long propertyId) {
        return disputeRepository.findActiveDisputes(propertyId);
    }

    public boolean hasActiveDispute(Long propertyId) {
        return disputeRepository.hasActiveDispute(propertyId);
    }

    @Transactional
    public Dispute approveDispute(Long disputeId, User officer) {

        if (!officer.getRole().name().equals("SUB_REGISTRAR")) {
            throw new BhoomiDarpanException(
                    "Only Sub-Registrar can approve disputes"
            );
        }

        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() ->
                        new BhoomiDarpanException("Dispute not found"));

        if (dispute.getStatus() != DisputeStatus.REQUESTED) {
            throw new BhoomiDarpanException(
                    "Dispute must be in REQUESTED state"
            );
        }

        String txHash = null;

        try {
            System.out.println("Sending to blockchain: " + dispute.getProperty().getPropertyCode());
            // 🔗 Blockchain approval
            try {
                txHash = blockchainService.approveDispute(
                        dispute.getProperty().getPropertyCode()
                );
            } catch (Exception e) {
                e.printStackTrace();   // 🔥 MUST ADD
                throw e;
            }

            dispute.setBlockchainHash(txHash);

            // 🧾 Update dispute status
            dispute.setStatus(DisputeStatus.ACTIVE);
            dispute.setApprovedBy(officer);
            dispute.setApprovedAt(LocalDateTime.now());

            // 🔒 Lock the property
            Property property = dispute.getProperty();
            property.setStatus(PropertyStatus.UNDER_DISPUTE);

            propertyRepository.save(property);

            return disputeRepository.save(dispute);

        } catch (Exception e) {

            // 🔁 Compensation transaction
            if (txHash != null) {
                try {
                    blockchainService.resolveDispute(
                            dispute.getDisputeCode()
                    );
                } catch (Exception ex) {
                    System.out.println(
                            "CRITICAL: Blockchain compensation failed"
                    );
                    ex.printStackTrace();
                }
            }

            throw new BhoomiDarpanException(
                    "Dispute approval failed: " + e.getMessage()
            );
        }
    }

    @Transactional
    public Dispute closeDispute(
            DisputeClosureRequest request,
            User officer) {

        if (!officer.getRole().name().equals("SUB_REGISTRAR")) {
            throw new BhoomiDarpanException(
                    "Only Sub-Registrar can close disputes"
            );
        }

        Dispute dispute = disputeRepository
                .findById(request.getDisputeId())
                .orElseThrow(() ->
                        new BhoomiDarpanException("Dispute not found"));

        if (dispute.getStatus() != DisputeStatus.ACTIVE) {
            throw new BhoomiDarpanException(
                    "Only ACTIVE disputes can be closed"
            );
        }

        // 📄 OCR validation
        String validation = validateClosureDocuments(
                request.getJudgmentOrder(),
                request.getSettlementDeed()
        );

        if (!validation.equals("VALID")) {
            throw new BhoomiDarpanException(
                    "Invalid closure documents: " + validation
            );
        }

        String txHash = null;

        try {

            // 🔗 Blockchain resolve dispute
            txHash = blockchainService.resolveDispute(
                    dispute.getProperty().getPropertyCode()
            );

            dispute.setBlockchainHash(txHash);

            // 🧾 Update dispute
            dispute.setStatus(DisputeStatus.CLOSED);
            dispute.setClosureReason(request.getClosureReason());
            dispute.setClosedBy(officer);
            dispute.setClosedAt(LocalDateTime.now());

            Property property = dispute.getProperty();

            // 🔓 Unlock property if no active disputes
            if (!hasActiveDispute(property.getId())) {

                property.setStatus(PropertyStatus.CLEAR);

                propertyRepository.save(property);
            }

            return disputeRepository.save(dispute);

        } catch (Exception e) {
            e.printStackTrace();
            throw new BhoomiDarpanException(
                    "Blockchain dispute closure failed: " + e.getMessage()
            );
        }
    }

    private String validateClosureDocuments(MultipartFile judgmentOrder, MultipartFile settlementDeed) {
        try {
            // Validate judgment order
            var judgmentOCR = ocrService.extractTextFromDocument(judgmentOrder, "COURT_ORDER");
            if (!judgmentOCR.isSuccess()) {
                return "OCR_FAILED_JUDGMENT";
            }

            // Check for closure keywords
            String judgmentText = judgmentOCR.getExtractedText().toLowerCase();
            if (!judgmentText.contains("dismissed") &&
                    !judgmentText.contains("withdrawn") &&
                    !judgmentText.contains("settled") &&
                    !judgmentText.contains("vacated")) {
                return "NO_CLOSURE_KEYWORDS";
            }

            return "VALID";

        } catch (Exception e) {
            return "OCR_PROCESSING_ERROR";
        }
    }

    public List<Dispute> getDisputesByUser(Long userId) {
        return disputeRepository.findUserRelatedDisputes(userId);
    }
    private byte[] getBytesFromFile(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new BhoomiDarpanException("Error reading file: " + e.getMessage());
        }
    }
}