package com.bhoomidarpan.service;

import com.bhoomidarpan.dto.*;
import com.bhoomidarpan.entity.*;
import com.bhoomidarpan.entity.enums.BuyRequestStatus;
import com.bhoomidarpan.entity.enums.ConsentStatus;
import com.bhoomidarpan.entity.enums.PropertyStatus;
import com.bhoomidarpan.entity.enums.RegistrationStatus;
import com.bhoomidarpan.exception.BhoomiDarpanException;
import com.bhoomidarpan.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BuyService {

    private final BuyRequestRepository buyRequestRepository;
    private final RegistrationRepository registrationRepository;
    private final OwnerConsentRepository ownerConsentRepository;
    private final PropertyRepository propertyRepository;
    private final OwnershipRepository ownershipRepository;
    private final PropertyService propertyService;
    private final AiService aiService;
    private final BlockchainService blockchainService;

    // =====================================================
    // 🏷 CREATE BUY REQUEST
    // =====================================================
    @Transactional
    public BuyRequest createBuyRequest(BuyRequestDTO dto, User buyer) {

        if (buyer == null) {
            throw new BhoomiDarpanException("Buyer cannot be null");
        }

        Property property = propertyRepository
                .findByPropertyCode(dto.getPropertyCode())
                .orElseThrow(() -> new BhoomiDarpanException("Property not found"));

        // 🔥 BLOCKCHAIN VERIFICATION STEP
        boolean verified = blockchainService.verifyPropertyIntegrity(property);

        if (!verified) {

            property.setStatus(PropertyStatus.UNDER_DISPUTE);
            propertyRepository.save(property);

            throw new BhoomiDarpanException(
                    "Property verification failed. Marked as disputed."
            );
        }

        if (!property.isAvailableForSale()) {
            throw new BhoomiDarpanException("Property not available for sale");
        }

        // Duplicate check
        // Replace the existing duplicate check with:
        List<BuyRequestStatus> activeStatuses = List.of(BuyRequestStatus.PENDING, BuyRequestStatus.APPROVED);
        if (buyRequestRepository.findRequestByStatuses(property.getId(), buyer.getId(), activeStatuses).isPresent()) {
            throw new BhoomiDarpanException("You already have an active request for this property");
        }

        // ================= AI RISK =================

        long transferCount =
                ownershipRepository.countByProperty_Id(property.getId());

        Map<String, Object> request = new HashMap<>();
        request.put("price", dto.getOfferedPrice());
        request.put("transfer_count", transferCount);

        Map<String, Object> response =
                aiService.detectAnomaly(request);

        double riskScore = response.get("risk_score") != null
                ? Double.parseDouble(response.get("risk_score").toString())
                : 0.0;

        String riskReason = response.get("reason") != null
                ? response.get("reason").toString()
                : "Normal pattern";

        String riskLevel =
                riskScore > 0.7 ? "HIGH"
                        : riskScore > 0.4 ? "MEDIUM"
                        : "LOW";

        BuyRequest buyRequest = BuyRequest.builder()
                .property(property)
                .buyer(buyer)
                .offeredPrice(dto.getOfferedPrice())
                .status(BuyRequestStatus.PENDING)
                .transactionId("TXN-" + System.currentTimeMillis())
                .riskScore(riskScore)
                .riskLevel(riskLevel)
                .riskReason(riskReason)
                .build();

        BuyRequest savedRequest =
                buyRequestRepository.save(buyRequest);

        // 🔥 LOCK PROPERTY AFTER REQUEST CREATED
        property.setAvailableForSale(false);
        propertyRepository.save(property);

        // Create Owner Consents
        List<Ownership> owners =
                ownershipRepository.findCurrentOwners(property.getId());

        for (Ownership ownership : owners) {
            OwnerConsent consent = OwnerConsent.builder()
                    .buyRequest(savedRequest)
                    .owner(ownership.getUser())
                    .status(ConsentStatus.PENDING)
                    .build();

            ownerConsentRepository.save(consent);
        }

        return savedRequest;
    }

    // =====================================================
    // 📄 GET BUY REQUESTS (BUYER)
    // =====================================================
    @Transactional(readOnly = true)
    public List<BuyRequest> getBuyRequestsByBuyer(Long buyerId) {
        return buyRequestRepository.findByBuyerWithAllDetails(buyerId);
    }

    // =====================================================
    // 🏠 GET OWNER REQUESTS
    // =====================================================
    @Transactional(readOnly = true)
    public List<BuyRequestResponse> getBuyRequestsForOwner(Long ownerId) {

        return buyRequestRepository
                .findBuyRequestsForOwnerWithDetails(ownerId)
                .stream()
                .map(this::convertToResponse)   // ✅ FIX
                .toList();
    }

    // =====================================================
    // ✅ PROCESS OWNER CONSENT
    // =====================================================
    @Transactional
    public OwnerConsent processConsent(
            Long consentId,
            ConsentRequest request,
            User owner
    ) {

        OwnerConsent consent =
                ownerConsentRepository.findById(consentId)
                        .orElseThrow(() ->
                                new BhoomiDarpanException("Consent not found"));

        if (!consent.getOwner().getId().equals(owner.getId())) {
            throw new BhoomiDarpanException("Not authorized");
        }

        consent.setStatus(
                request.isApprove()
                        ? ConsentStatus.APPROVED
                        : ConsentStatus.REJECTED
        );

        consent.setConsentDate(LocalDateTime.now());

        OwnerConsent updated = ownerConsentRepository.save(consent);

        checkAndUpdateBuyRequestStatus(
                consent.getBuyRequest().getId()
        );

        return updated;
    }

    // =====================================================
    // 🔄 UPDATE REQUEST STATUS AFTER CONSENTS
    // =====================================================
    @Transactional
    public void checkAndUpdateBuyRequestStatus(Long requestId) {

        BuyRequest request =
                buyRequestRepository.findById(requestId)
                        .orElseThrow(() ->
                                new BhoomiDarpanException("Buy request not found"));

        Set<OwnerConsent> consents =
                request.getOwnerConsents();

        boolean anyRejected =
                consents.stream()
                        .anyMatch(c -> c.getStatus() == ConsentStatus.REJECTED);

        if (anyRejected) {
            request.setStatus(BuyRequestStatus.REJECTED);

            // 🔥 MAKE PROPERTY AVAILABLE AGAIN
            Property property = request.getProperty();
            property.setAvailableForSale(true);
            propertyRepository.save(property);

            buyRequestRepository.save(request);
            return;
        }

        boolean allApproved =
                consents.stream()
                        .allMatch(c -> c.getStatus() == ConsentStatus.APPROVED);

        if (allApproved) {
            request.setStatus(BuyRequestStatus.APPROVED);
            buyRequestRepository.save(request);
        }
    }


    private BuyRequestResponse convertToResponse(BuyRequest br) {

        BuyRequestResponse res = new BuyRequestResponse();

        res.setId(br.getId());
        res.setPropertyCode(br.getProperty().getPropertyCode());
        res.setBuyerName(br.getBuyer().getName()); // ✅ SAFE NOW
        res.setStatus(br.getStatus().name());

        return res;
    }


    // =====================================================
    // 📅 PROPOSE VISIT DATE (BUYER)
    // =====================================================
    @Transactional
    public void proposeVisitDate(
            Long requestId,
            VisitDateRequest request,
            User buyer
    ) {

        BuyRequest buyRequest =
                buyRequestRepository.findById(requestId)
                        .orElseThrow(() ->
                                new BhoomiDarpanException("Buy request not found"));

        if (!buyRequest.getBuyer().getId().equals(buyer.getId())) {
            throw new BhoomiDarpanException("Not authorized");
        }

        if (buyRequest.getStatus() != BuyRequestStatus.APPROVED) {
            throw new BhoomiDarpanException("Invalid state for visit proposal");
        }

        buyRequest.setVisitDate(request.getVisitDate());
        buyRequest.setStatus(BuyRequestStatus.VISIT_DATE_PROPOSED);
        buyRequest.setVisitTimeSlot(request.getTimeSlot());

        buyRequestRepository.save(buyRequest);
    }

    // =====================================================
    // 📅 CONFIRM VISIT DATE (OWNER)
    // =====================================================
    @Transactional
    public void confirmVisitDate(
            Long requestId,
            VisitDateDecisionRequest decision,
            User owner
    ) {

        BuyRequest buyRequest =
                buyRequestRepository.findById(requestId)
                        .orElseThrow(() ->
                                new BhoomiDarpanException("Buy request not found"));

        // ✅ SAFE OWNER CHECK (no lazy loading)
        boolean isOwner =
                ownershipRepository.existsByPropertyIdAndUserIdAndCurrentTrue(
                        buyRequest.getProperty().getId(),
                        owner.getId()
                );

        if (!isOwner) {
            throw new BhoomiDarpanException("Not authorized");
        }



        if (buyRequest.getStatus() != BuyRequestStatus.VISIT_DATE_PROPOSED) {
            throw new BhoomiDarpanException("Invalid state for confirmation");
        }

        if (buyRequest.getVisitDate() == null) {
            throw new BhoomiDarpanException("Visit date is missing");
        }

        if (decision.isApprove()) {

            // 🔥 SLOT LIMIT CHECK
            long count = registrationRepository.countByAppointmentDate(
                    buyRequest.getVisitDate()
            );

            if (count >= 10) {
                throw new BhoomiDarpanException(
                        "Only 10 appointments allowed per day. Please select another date."
                );
            }

            buyRequest.setStatus(BuyRequestStatus.VISIT_DATE_CONFIRMED);

            // Create Registration
            Registration registration = new Registration();
            registration.setProperty(buyRequest.getProperty());
            registration.setBuyer(buyRequest.getBuyer());
            registration.setAppointmentDate(
                    buyRequest.getVisitDate().atStartOfDay()
            );
            registration.setStatus(
                    RegistrationStatus.APPOINTMENT_SCHEDULED
            );
            LocalDateTime appointmentDateTime = LocalDateTime.of(
                    buyRequest.getVisitDate(),
                    buyRequest.getVisitTimeSlot() != null ? buyRequest.getVisitTimeSlot() : LocalTime.of(10, 0)
            );
            registration.setAppointmentDate(appointmentDateTime);

            registrationRepository.save(registration);

        } else {

            buyRequest.setStatus(BuyRequestStatus.APPROVED);
        }

        buyRequestRepository.save(buyRequest);
    }


    @Transactional
    public void counterOffer(Long requestId, Double newPrice, User owner) {

        BuyRequest request = buyRequestRepository.findById(requestId)
                .orElseThrow(() -> new BhoomiDarpanException("Request not found"));

        boolean isOwner = ownershipRepository
                .existsByPropertyIdAndUserIdAndCurrentTrue(
                        request.getProperty().getId(),
                        owner.getId()
                );

        if (!isOwner) {
            throw new BhoomiDarpanException("Not authorized");
        }

        if (newPrice <= request.getOfferedPrice()) {
            throw new BhoomiDarpanException("Counter must be higher");
        }

        request.setOfferedPrice(newPrice);
        request.setStatus(BuyRequestStatus.COUNTER_OFFERED);

        buyRequestRepository.save(request);
    }


    @Transactional
    public void respondToCounter(Long requestId, boolean accept, Double newPrice, User buyer) {

        BuyRequest request = buyRequestRepository.findById(requestId)
                .orElseThrow(() -> new BhoomiDarpanException("Request not found"));

        if (!request.getBuyer().getId().equals(buyer.getId())) {
            throw new BhoomiDarpanException("Not authorized");
        }

        if (request.getStatus() != BuyRequestStatus.COUNTER_OFFERED) {
            throw new BhoomiDarpanException("Invalid state");
        }

        if (accept) {
            request.setStatus(BuyRequestStatus.APPROVED);
        } else {

            if (newPrice == null || newPrice <= 0) {
                throw new BhoomiDarpanException("Invalid offer amount");
            }

            if (newPrice.equals(request.getOfferedPrice())) {
                throw new BhoomiDarpanException("Offer must be different from current price");
            }

            // 👇 THIS IS KEY
            request.setOfferedPrice(newPrice);

            // Move back to negotiation state
            request.setStatus(BuyRequestStatus.PENDING);
        }

        buyRequestRepository.save(request);
    }



    // =====================================================
    // ❌ CANCEL VISIT
    // =====================================================
    @Transactional
    public void cancelVisit(Long requestId, User user) {

        BuyRequest request =
                buyRequestRepository.findById(requestId)
                        .orElseThrow(() ->
                                new BhoomiDarpanException("Buy request not found"));

        boolean isBuyer =
                request.getBuyer().getId().equals(user.getId());

        boolean isOwner =
                request.getProperty().getOwnerships()
                        .stream()
                        .anyMatch(o ->
                                o.isCurrent() &&
                                        o.getUser().getId().equals(user.getId())
                        );

        if (!isBuyer && !isOwner) {
            throw new BhoomiDarpanException("Not authorized");
        }

        request.setStatus(BuyRequestStatus.VISIT_CANCELLED);
        request.setVisitDate(null);

        buyRequestRepository.save(request);
    }

    // =====================================================
    // 🔁 RESCHEDULE VISIT
    // =====================================================
    @Transactional
    public void rescheduleVisit(
            Long requestId,
            VisitDateRequest request,
            User buyer
    ) {

        BuyRequest buyRequest =
                buyRequestRepository.findById(requestId)
                        .orElseThrow(() ->
                                new BhoomiDarpanException("Buy request not found"));

        if (!buyRequest.getBuyer().getId().equals(buyer.getId())) {
            throw new BhoomiDarpanException("Not authorized");
        }

        buyRequest.setVisitDate(request.getVisitDate());
        buyRequest.setStatus(BuyRequestStatus.VISIT_RESCHEDULE_PROPOSED);

        buyRequestRepository.save(buyRequest);
    }

    // =====================================================
    // 📆 GET VISIT DETAILS
    // =====================================================
    @Transactional(readOnly = true)
    public VisitDateResponse getVisitDetails(Long requestId) {

        BuyRequest request =
                buyRequestRepository.findByIdWithVisitDetails(requestId)
                        .orElseThrow(() ->
                                new BhoomiDarpanException("Buy request not found"));

        VisitDateResponse res = new VisitDateResponse();

        res.setPropertyCode(request.getProperty().getPropertyCode());
        res.setLocation(request.getProperty().getLocation());
        res.setBuyerName(request.getBuyer().getName());
        res.setVisitDate(request.getVisitDate());
        res.setStatus(request.getStatus().name());

        return res;
    }

    // =====================================================
    // 📅 MY APPOINTMENTS
    // =====================================================
    @Transactional(readOnly = true)
    public List<MyAppointmentResponse> getMyAppointments(User user) {

        List<BuyRequest> requests =
                buyRequestRepository.findByBuyerIdOrOwnerId(user.getId());

        return requests.stream().map(req -> {

            MyAppointmentResponse res =
                    new MyAppointmentResponse();

            res.setRequestId(req.getId());
            res.setPropertyCode(req.getProperty().getPropertyCode());
            res.setLocation(req.getProperty().getLocation());
            res.setVisitDate(req.getVisitDate());

            res.setStatus(
                    req.getStatus() != null
                            ? req.getStatus().name()
                            : "NOT_SCHEDULED"
            );

            boolean isBuyer =
                    req.getBuyer().getId().equals(user.getId());

            res.setRole(isBuyer ? "BUYER" : "OWNER");

            return res;

        }).toList();
    }
    @Transactional(readOnly = true)
    public boolean isAllConsentsApproved(Long buyRequestId) {

        BuyRequest buyRequest = buyRequestRepository.findById(buyRequestId)
                .orElseThrow(() -> new BhoomiDarpanException("Buy request not found"));

        Long totalOwners = (long) buyRequest.getOwnerConsents().size();

        Long approvedCount = ownerConsentRepository
                .countApprovedConsents(buyRequestId);

        return approvedCount.equals(totalOwners);
    }

    private void createRegistrationFromVisit(BuyRequest buyRequest) {

        Registration registration = new Registration();

        registration.setProperty(buyRequest.getProperty());
        registration.setBuyer(buyRequest.getBuyer());
        registration.setAppointmentDate(
                buyRequest.getVisitDate().atStartOfDay()
        );

        registration.setStatus(
                RegistrationStatus.APPOINTMENT_SCHEDULED
        );

        registrationRepository.save(registration);
    }



}
