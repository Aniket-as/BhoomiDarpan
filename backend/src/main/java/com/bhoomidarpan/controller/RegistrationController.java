package com.bhoomidarpan.controller;

import com.bhoomidarpan.dto.*;
import com.bhoomidarpan.entity.*;
import com.bhoomidarpan.entity.enums.GiftDeedStatus;
import com.bhoomidarpan.entity.enums.RegistrationStatus;
import com.bhoomidarpan.exception.BhoomiDarpanException;
import com.bhoomidarpan.repository.GiftDeedRepository;
import com.bhoomidarpan.repository.MutationRepository;
import com.bhoomidarpan.repository.RegistrationRepository;
import com.bhoomidarpan.service.RegistrationService;
import com.bhoomidarpan.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;
    private final UserService userService;
    private final RegistrationRepository registrationRepository;
    private final GiftDeedRepository giftDeedRepository;




    @PreAuthorize("hasRole('SUB_REGISTRAR')")
    @GetMapping("/todays-appointments")
    public ResponseEntity<List<RegistrationResponse>> getTodaysAppointments() {

        return ResponseEntity.ok(
                registrationService.getTodaysAppointments()
                        .stream()
                        .map(this::convertToResponse)
                        .collect(Collectors.toList())
        );
    }
    @PreAuthorize("hasRole('TEHSILDAR')")
    @GetMapping("/details/{id}")
    public ResponseEntity<RegistrationResponse> getRegistrationDetails(
            @PathVariable Long id) {

        Registration registration = registrationRepository
                .findByIdWithDetails(id)
                .orElseThrow(() ->
                        new BhoomiDarpanException("Registration not found"));

        return ResponseEntity.ok(convertToResponse(registration));
    }



    @Transactional
    public Registration createRegistrationFromVisit(BuyRequest buyRequest) {

        Registration registration = Registration.builder()
                .property(buyRequest.getProperty())
                .buyer(buyRequest.getBuyer())
                .appointmentDate(
                        buyRequest.getVisitDate().atTime(10, 0)
                )
                .status(RegistrationStatus.APPOINTMENT_SCHEDULED)
                .build();

        return registrationRepository.save(registration);
    }

    @GetMapping("/gift/pending")
    public List<GiftDeedRequest> getPendingGiftRequests() {
        return giftDeedRepository.findByStatus(GiftDeedStatus.PENDING);
    }

    @PutMapping("/verify-gift/{id}")
    public ResponseEntity<?> verifyGift(
            @PathVariable Long id,
            @RequestParam Boolean approve,
            @RequestParam(required = false) String remarks,

            @RequestParam(required = false) MultipartFile giftDeed,
            @RequestParam(required = false) MultipartFile buyerPhoto,
            @RequestParam(required = false) MultipartFile donorPhoto,
            @RequestParam(required = false) MultipartFile buyerFingerprint,
            @RequestParam(required = false) MultipartFile donorFingerprint,

            @AuthenticationPrincipal UserDetails userDetails
    ) {

        try {

            System.out.println("🔥 VERIFY GIFT API HIT");
            System.out.println("Approve: " + approve);
            System.out.println("GiftDeed: " + giftDeed);
            System.out.println("BuyerPhoto: " + buyerPhoto);
            System.out.println("DonorPhoto: " + donorPhoto);

            User officer = userService
                    .findByAadhaarNumber(userDetails.getUsername())
                    .orElseThrow(() -> new BhoomiDarpanException("User not found"));

            registrationService.verifyGiftDeedRegistration(
                    id, approve, remarks,
                    giftDeed, buyerPhoto, donorPhoto,
                    buyerFingerprint, donorFingerprint,
                    officer
            );

            return ResponseEntity.ok("Gift deed verified successfully");

        } catch (BhoomiDarpanException e) {

            e.printStackTrace(); // 🔥 shows exact error in console
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (Exception e) {

            e.printStackTrace(); // 🔥 unexpected error
            return ResponseEntity.internalServerError()
                    .body("Server Error: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('SUB_REGISTRAR')")
    @GetMapping("/pending-verification")
    public ResponseEntity<List<RegistrationResponse>> getPendingVerifications() {

        return ResponseEntity.ok(
                registrationService.getPendingVerifications()
                        .stream()
                        .map(this::convertToResponse)
                        .collect(Collectors.toList())
        );
    }

    @PostMapping(value = "/verify", consumes = "multipart/form-data")
    public ResponseEntity<?> verifyRegistration(
            @RequestParam("registrationId") Long registrationId,
            @RequestParam("approve") String approveStr,
            @RequestParam(value = "remarks", required = false) String remarks,
            @RequestParam(value = "saleDeed", required = false) MultipartFile saleDeed,
            @RequestParam(value = "buyerPhoto", required = false) MultipartFile buyerPhoto,
            @RequestParam(value = "sellerPhoto", required = false) MultipartFile sellerPhoto,
            @RequestParam(value = "buyerFingerprint", required = false) MultipartFile buyerFingerprint,
            @RequestParam(value = "sellerFingerprint", required = false) MultipartFile sellerFingerprint,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        if (userDetails == null) {
            throw new BhoomiDarpanException("Unauthorized request");
        }

        Boolean approve = Boolean.parseBoolean(approveStr);

        User officer = userService
                .findByAadhaarNumber(userDetails.getUsername())
                .orElseThrow(() -> new BhoomiDarpanException("User not found"));


        Registration registration =
                registrationService.verifyRegistration(
                        registrationId,
                        approve,
                        remarks,
                        saleDeed,
                        buyerPhoto,
                        sellerPhoto,
                        buyerFingerprint,
                        sellerFingerprint,
                        officer
                );
        return ResponseEntity.ok(
                "Registration " + registration.getStatus().name() + " successfully"
        );
    }

    @GetMapping("/approved-without-mutation")
    @PreAuthorize("hasRole('TEHSILDAR')")
    public ResponseEntity<List<RegistrationResponse>> getApprovedRegistrationsWithoutMutation() {

        return ResponseEntity.ok(
                registrationService.getApprovedWithoutMutation()
                        .stream()
                        .map(this::convertToResponse)
                        .toList()
        );
    }



    @PreAuthorize("hasRole('SUB_REGISTRAR')")
    @PostMapping("/approve/{registrationId}")
    public ResponseEntity<?> approveRegistration(
            @PathVariable Long registrationId,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            throw new BhoomiDarpanException("Unauthorized request");
        }

        User officer = userService
                .findByAadhaarNumber(userDetails.getUsername())
                .orElseThrow(() -> new BhoomiDarpanException("User not found"));


        Registration registration =
                registrationService.approveRegistration(registrationId, officer);

        return ResponseEntity.ok(
                "Registration approved. Blockchain hash: "
                        + registration.getBlockchainHash()
        );
    }

    private RegistrationResponse convertToResponse(Registration registration) {

        RegistrationResponse r = new RegistrationResponse();

        r.setId(registration.getId());
        r.setPropertyCode(registration.getProperty().getPropertyCode());
        r.setBuyerName(registration.getBuyer().getName());

        // 🔥 Seller = current property owner
        Ownership currentOwner = registration.getProperty()
                .getOwnerships()
                .stream()
                .filter(Ownership::isCurrent)
                .findFirst()
                .orElse(null);

        if (currentOwner != null) {
            User seller = currentOwner.getUser();
            r.setSellerName(seller.getName());
            r.setSellerAadhaar(seller.getAadhaarNumber());
            r.setSellerPan(seller.getPan());
        }

        r.setStatus(registration.getStatus().name());
        r.setAppointmentDate(registration.getAppointmentDate());

        return r;
    }

}
