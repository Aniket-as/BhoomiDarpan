package com.bhoomidarpan.controller;

import com.bhoomidarpan.dto.*;
import com.bhoomidarpan.entity.BuyRequest;
import com.bhoomidarpan.entity.OwnerConsent;
import com.bhoomidarpan.entity.User;
import com.bhoomidarpan.dto.VisitDateDecisionRequest;
import com.bhoomidarpan.exception.BhoomiDarpanException;
import com.bhoomidarpan.repository.RegistrationRepository;
import com.bhoomidarpan.service.BuyService;
import com.bhoomidarpan.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/buy")
@RequiredArgsConstructor
public class BuyController {

    private final BuyService buyService;
    private final UserService userService;
    private final RegistrationRepository registrationRepository;

    // =====================================================
    // 🔐 Helper: Get Logged-in User via Aadhaar
    // =====================================================
    private User getLoggedInUser(UserDetails userDetails) {

        if (userDetails == null) {
            throw new BhoomiDarpanException("Unauthorized access");
        }

        return userService
                .findByAadhaarNumber(userDetails.getUsername())
                .orElseThrow(() -> new BhoomiDarpanException("User not found"));
    }


    @PostMapping("/counter/{requestId}")
    public ResponseEntity<?> counterOffer(
            @PathVariable Long requestId,
            @RequestBody Map<String, Double> body,
            @AuthenticationPrincipal UserDetails userDetails) {

        User owner = userService
                .findByAadhaarNumber(userDetails.getUsername())
                .orElseThrow();

        buyService.counterOffer(requestId, body.get("newPrice"), owner);

        return ResponseEntity.ok("Counter offer sent");
    }


    // =====================================================
    // 🏷 CREATE BUY REQUEST
    // =====================================================
    @PostMapping("/request")
    public ResponseEntity<?> createBuyRequest(
            @RequestBody BuyRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        User buyer = getLoggedInUser(userDetails);

        buyService.createBuyRequest(dto, buyer);

        return ResponseEntity.ok("Buy request created successfully");
    }

    // =====================================================
    // 📄 MY BUY REQUESTS
    // =====================================================
    @GetMapping("/my-requests")
    public ResponseEntity<List<BuyRequestResponse>> getMyBuyRequests(
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        User user = getLoggedInUser(userDetails);

        List<BuyRequest> requests =
                buyService.getBuyRequestsByBuyer(user.getId());

        return ResponseEntity.ok(
                requests.stream()
                        .map(req -> convertToResponse(req, user))
                        .toList()
        );
    }


    // =====================================================
    // 🏠 OWNER REQUESTS
    // =====================================================
    @GetMapping("/owner-requests")
    public ResponseEntity<List<BuyRequestResponse>> getOwnerRequests(
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        User user = getLoggedInUser(userDetails);

        // ✅ Directly return from service (NO mapping here)
        return ResponseEntity.ok(
                buyService.getBuyRequestsForOwner(user.getId())
        );
    }

    // =====================================================
    // ✅ PROCESS OWNER CONSENT
    // =====================================================
    @PostMapping("/consent/{consentId}")
    public ResponseEntity<?> processConsent(
            @PathVariable Long consentId,
            @Valid @RequestBody ConsentRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        User owner = getLoggedInUser(userDetails);

        OwnerConsent consent =
                buyService.processConsent(consentId, request, owner);

        return ResponseEntity.ok(
                "Consent " + consent.getStatus().name() + " successfully"
        );
    }

    // =====================================================
    // 🔍 CHECK IF ALL CONSENTS APPROVED
    // =====================================================
    @GetMapping("/request/{requestId}/status")
    public ResponseEntity<Boolean> checkAllConsentsApproved(
            @PathVariable Long requestId
    ) {
        return ResponseEntity.ok(
                buyService.isAllConsentsApproved(requestId)
        );
    }

    // =====================================================
    // 📅 PROPOSE VISIT DATE
    // =====================================================
    @PostMapping("/request/{requestId}/propose-visit-date")
    public ResponseEntity<?> proposeVisitDate(
            @PathVariable Long requestId,
            @RequestBody VisitDateRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        User buyer = getLoggedInUser(userDetails);

        buyService.proposeVisitDate(requestId, request, buyer);

        return ResponseEntity.ok("Visit date proposed successfully");
    }

    // =====================================================
    // 📅 CONFIRM VISIT DATE (OWNER)
    // =====================================================
    @PostMapping("/request/{requestId}/confirm-visit-date")
    public ResponseEntity<?> confirmVisitDate(
            @PathVariable Long requestId,
            @RequestBody VisitDateDecisionRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        User owner = getLoggedInUser(userDetails);

        buyService.confirmVisitDate(requestId, request, owner);

        return ResponseEntity.ok("Visit date decision processed successfully");
    }


    @GetMapping("/appointment-availability")
    public ResponseEntity<?> getAvailability(@RequestParam String date) {

        LocalDate localDate = LocalDate.parse(date);

        long count = registrationRepository.countByAppointmentDate(localDate);

        Map<String, Object> res = new HashMap<>();
        res.put("total", 10);
        res.put("used", count);
        res.put("remaining", 10 - count);

        return ResponseEntity.ok(res);
    }
    // =====================================================
    // ❌ CANCEL VISIT
    // =====================================================
    @PostMapping("/request/{requestId}/cancel-visit")
    public ResponseEntity<?> cancelVisit(
            @PathVariable Long requestId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        User user = getLoggedInUser(userDetails);

        buyService.cancelVisit(requestId, user);

        return ResponseEntity.ok("Visit cancelled successfully");
    }

    // =====================================================
    // 🔁 RESCHEDULE VISIT
    // =====================================================
    @PostMapping("/request/{requestId}/reschedule-visit")
    public ResponseEntity<?> rescheduleVisit(
            @PathVariable Long requestId,
            @RequestBody VisitDateRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        User buyer = getLoggedInUser(userDetails);

        buyService.rescheduleVisit(requestId, request, buyer);

        return ResponseEntity.ok("Visit reschedule proposed successfully");
    }

    // =====================================================
    // 📆 GET VISIT DETAILS
    // =====================================================
    @GetMapping("/request/{requestId}/visit")
    public ResponseEntity<?> getVisitDetails(
            @PathVariable Long requestId
    ) {
        return ResponseEntity.ok(
                buyService.getVisitDetails(requestId)
        );
    }

    // =====================================================
    // 📅 MY APPOINTMENTS (Buyer + Owner)
    // =====================================================
    @GetMapping("/my-appointments")
    public ResponseEntity<?> getMyAppointments(
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        User user = getLoggedInUser(userDetails);

        return ResponseEntity.ok(
                buyService.getMyAppointments(user)
        );
    }

    @PostMapping("/counter/respond/{requestId}")
    public ResponseEntity<?> respondToCounter(
            @PathVariable Long requestId,
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal UserDetails userDetails) {

        User buyer = getLoggedInUser(userDetails);

        boolean accept = (boolean) body.get("accept");
        Double newPrice = body.get("newPrice") != null
                ? Double.valueOf(body.get("newPrice").toString())
                : null;

        buyService.respondToCounter(requestId, accept, newPrice, buyer);

        return ResponseEntity.ok("Response submitted");
    }

    // =====================================================
    // 🔄 CONVERTER
    // =====================================================
    private BuyRequestResponse convertToResponse(
            BuyRequest request,
            User currentUser
    ) {

        BuyRequestResponse response = new BuyRequestResponse();

        response.setId(request.getId());

        // ================= PROPERTY DETAILS =================
        response.setPropertyCode(request.getProperty().getPropertyCode());
        response.setLocation(request.getProperty().getLocation());
        response.setLandType(
                request.getProperty().getLandType() != null
                        ? request.getProperty().getLandType().name()
                        : null
        );
        response.setPropertyStatus(
                request.getProperty().getStatus() != null
                        ? request.getProperty().getStatus().name()
                        : null
        );
        response.setArea(request.getProperty().getArea());
        response.setAvailableForSale(
                request.getProperty().isAvailableForSale()
        );

        // ================= BUYER DETAILS =================
        response.setBuyerName(request.getBuyer().getName());
        response.setBuyerEmail(request.getBuyer().getEmail());

        response.setOfferedPrice(request.getOfferedPrice());

        response.setStatus(
                request.getStatus() != null
                        ? request.getStatus().name()
                        : "UNKNOWN"
        );

        response.setCreatedAt(
                request.getCreatedAt() != null
                        ? request.getCreatedAt().toString()
                        : null
        );
        // ================= VISIT DETAILS =================
        response.setVisitDate(
                request.getVisitDate() != null
                        ? request.getVisitDate().toString()
                        : null
        );

        // ================= ROLE & CONSENT =================
        if (request.getBuyer().getId().equals(currentUser.getId())) {

            response.setCurrentUserRole("BUYER");

            // 👇 ADD THIS BLOCK FOR OWNER DETAILS
            if (request.getOwnerConsents() != null && !request.getOwnerConsents().isEmpty()) {

                OwnerConsent consent = request.getOwnerConsents().iterator().next();

                if (consent.getOwner() != null) {
                    response.setOwnerName(consent.getOwner().getName());
                    response.setOwnerEmail(consent.getOwner().getEmail());
                }
            }

        } else {

            response.setCurrentUserRole("OWNER");

            if (request.getOwnerConsents() != null) {

                request.getOwnerConsents().stream()
                        .filter(c -> c.getOwner().getId().equals(currentUser.getId()))
                        .findFirst()
                        .ifPresent(consent -> {

                            response.setMyConsentId(consent.getId());

                            response.setMyConsentStatus(
                                    consent.getStatus() != null
                                            ? consent.getStatus().name()
                                            : "PENDING"
                            );
                        });
            }
        }

        return response;
    }

}
