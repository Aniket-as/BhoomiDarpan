package com.bhoomidarpan.service;

import com.bhoomidarpan.dto.MutationRequest;
import com.bhoomidarpan.dto.OCRResponse;
import com.bhoomidarpan.entity.*;
import com.bhoomidarpan.entity.enums.MutationStatus;
import com.bhoomidarpan.entity.enums.PropertyStatus;
import com.bhoomidarpan.entity.enums.Role;
import com.bhoomidarpan.exception.BhoomiDarpanException;
import com.bhoomidarpan.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MutationService {

    private final MutationRepository mutationRepository;
    private final RegistrationRepository registrationRepository;
    private final PropertyRepository propertyRepository;
    private final OwnershipRepository ownershipRepository;
    private final OCRService ocrService;
    private final FileUploadService fileUploadService;
    private final DocumentRepository documentRepository;

    @Transactional
    public Mutation createMutationFromRegistration(Long registrationId) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new BhoomiDarpanException("Registration not found"));

        if (registration.getStatus() != com.bhoomidarpan.entity.enums.RegistrationStatus.APPROVED) {
            throw new BhoomiDarpanException("Registration must be approved for mutation");
        }

        // Check if mutation already exists
        if (mutationRepository.findByRegistrationId(registrationId).isPresent()) {
            throw new BhoomiDarpanException("Mutation already exists for this registration");
        }

        Mutation mutation = Mutation.builder()
                .property(registration.getProperty())
                .registration(registration)
                .mutationNumber("MUT/" + LocalDateTime.now().getYear() + "/" + System.currentTimeMillis())
                .status(MutationStatus.PENDING)
                .build();

        return mutationRepository.save(mutation);
    }


    @Transactional
    public void processMutation(Long mutationId,
                                MutationRequest request,
                                User officer) {

        Mutation mutation = mutationRepository.findById(mutationId)
                .orElseThrow(() -> new BhoomiDarpanException("Mutation not found"));

        try {

            /* ================= VALIDATION ================= */

            if (request.getSevenTwelve() == null || request.getSevenTwelve().isEmpty()) {
                throw new BhoomiDarpanException("7/12 file is required");
            }

            if (request.getEightA() == null || request.getEightA().isEmpty()) {
                throw new BhoomiDarpanException("8A file is required");
            }

            /* ================= OCR VALIDATION ================= */

            OCRResponse sevenResponse =
                    ocrService.extractTextFromDocument(
                            request.getSevenTwelve(),
                            "SEVEN_TWELVE"
                    );

            OCRResponse eightResponse =
                    ocrService.extractTextFromDocument(
                            request.getEightA(),
                            "EIGHT_A"
                    );

            if (!sevenResponse.isSuccess() || !eightResponse.isSuccess()) {
                throw new BhoomiDarpanException("OCR extraction failed");
            }

            String expectedOwner =
                    mutation.getRegistration().getBuyer().getName();

            boolean valid = ocrService.validateMutationDocs(
                    sevenResponse.getExtractedText(),
                    eightResponse.getExtractedText(),
                    expectedOwner
            );

            if (!valid) {
                throw new BhoomiDarpanException(
                        "Mutation document owner mismatch with buyer name"
                );
            }

            /* ================= FILE STORAGE ================= */

            /* ================= FILE STORAGE (CLOUDINARY) ================= */

            String sevenTwelveUrl =
                    fileUploadService.uploadFile(request.getSevenTwelve());

            String eightAUrl =
                    fileUploadService.uploadFile(request.getEightA());

            mutation.setSevenTwelvePath(sevenTwelveUrl);
            mutation.setEightAPath(eightAUrl);

            mutation.setRemarks(request.getRemarks());
            mutation.setUser(officer);

            // 🔥 ADD THIS BLOCK (VERY IMPORTANT)

// Save 7/12 document
            Document doc1 = new Document();
            doc1.setProperty(mutation.getProperty());
            doc1.setFileUrl(sevenTwelveUrl);
            doc1.setDocumentType("7/12");
            doc1.setVerified(true);

            documentRepository.save(doc1);

// Save 8A document
            Document doc2 = new Document();
            doc2.setProperty(mutation.getProperty());
            doc2.setFileUrl(eightAUrl);
            doc2.setDocumentType("8A");
            doc2.setVerified(true);

            documentRepository.save(doc2);

            mutationRepository.save(mutation);

        } catch (Exception e) {

            e.printStackTrace();

            throw new BhoomiDarpanException(
                    "Error processing mutation: " + e.getMessage()
            );
        }
    }



    @Transactional
    public Mutation approveMutation(Long mutationId, User tehsildar) {
        // Check if user is Tehsildar
        if (tehsildar.getRole() != Role.TEHSILDAR) {
            throw new BhoomiDarpanException("Only Tehsildar can approve mutations");
        }


        Mutation mutation = mutationRepository.findById(mutationId)
                .orElseThrow(() -> new BhoomiDarpanException("Mutation not found"));

        if (mutation.getSevenTwelvePath() == null || mutation.getEightAPath() == null) {
            throw new BhoomiDarpanException("7/12 and 8A documents must be uploaded");
        }

        // Update mutation status
        mutation.setStatus(MutationStatus.APPROVED);
        mutation.setApprovedBy(tehsildar);
        mutation.setApprovedAt(LocalDateTime.now());

        Mutation approvedMutation = mutationRepository.save(mutation);

        // Update property ownership
        updatePropertyOwnership(mutation);

        // Update property status
        Property property = mutation.getProperty();
        property.setStatus(PropertyStatus.CLEAR);
        propertyRepository.save(property);

        return approvedMutation;
    }

    @Transactional(readOnly = true)
    public List<Mutation> getPendingMutations() {
        return mutationRepository.findPendingWithDetails();
    }


    @Transactional
    public Mutation rejectMutation(Long mutationId, String remarks, User tehsildar) {
        // Check if user is Tehsildar
        if (tehsildar.getRole() != Role.TEHSILDAR) {
            throw new BhoomiDarpanException("Only Tehsildar can reject mutations");
        }


        Mutation mutation = mutationRepository.findById(mutationId)
                .orElseThrow(() -> new BhoomiDarpanException("Mutation not found"));

        mutation.setStatus(MutationStatus.REJECTED);
        mutation.setRemarks(remarks);
        mutation.setApprovedBy(tehsildar);
        mutation.setApprovedAt(LocalDateTime.now());

        return mutationRepository.save(mutation);
    }

    @Transactional(readOnly = true)
    public Mutation getMutationDetails(Long id) {
        return mutationRepository.findDetailedById(id)
                .orElseThrow(() -> new BhoomiDarpanException("Mutation not found"));
    }


    @Transactional(readOnly = true)
    public Mutation getMutationById(Long mutationId) {

        return mutationRepository.findDetailedById(mutationId)
                .orElseThrow(() -> new BhoomiDarpanException("Mutation not found"));
    }



    private void updatePropertyOwnership(Mutation mutation) {

        Registration registration = mutation.getRegistration();
        Property property = mutation.getProperty();
        User buyer = registration.getBuyer();

        // Mark all current owners as not current
        List<Ownership> currentOwners =
                ownershipRepository.findCurrentOwners(property.getId());

        for (Ownership owner : currentOwners) {
            owner.setCurrent(false);
            ownershipRepository.save(owner);
        }

        // 🔥 Check if ownership already exists for this property + buyer
        Optional<Ownership> existingOwnership =
                ownershipRepository.findByPropertyIdAndUserId(
                        property.getId(),
                        buyer.getId()
                );

        if (existingOwnership.isPresent()) {

            // Just reactivate it
            Ownership ownership = existingOwnership.get();
            ownership.setCurrent(true);
            ownership.setOwnershipPercentage(100.0);
            ownership.setOwnershipType(
                    com.bhoomidarpan.entity.enums.OwnershipType.SINGLE
            );

            ownershipRepository.save(ownership);

        } else {

            // Create new ownership
            Ownership newOwnership = Ownership.builder()
                    .property(property)
                    .user(buyer)
                    .ownershipPercentage(100.0)
                    .ownershipType(
                            com.bhoomidarpan.entity.enums.OwnershipType.SINGLE
                    )
                    .current(true)
                    .build();

            ownershipRepository.save(newOwnership);
        }
    }


    private byte[] getBytesFromFile(org.springframework.web.multipart.MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new BhoomiDarpanException("Error reading file: " + e.getMessage());
        }
    }

    @Autowired
    private UserService userService;

    public Optional<User> findByAadhaarNumber(String aadhaarNumber) {

        return userService
                .findByAadhaarNumber(aadhaarNumber);
    }

    public List<Mutation> getMutationsForUser(Long userId) {
        return mutationRepository.findByBuyerIdOrOwnerId(userId);
    }



}