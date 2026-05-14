package com.bhoomidarpan.controller;

import com.bhoomidarpan.entity.Property;
import com.bhoomidarpan.entity.Registration;
import com.bhoomidarpan.entity.User;
import com.bhoomidarpan.entity.enums.RegistrationStatus;
import com.bhoomidarpan.entity.enums.Role;
import com.bhoomidarpan.exception.BhoomiDarpanException;
import com.bhoomidarpan.repository.RegistrationRepository;
import com.bhoomidarpan.service.BlockchainService;
import com.bhoomidarpan.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/registrations")
@RequiredArgsConstructor
public class AdminAIReviewController {

    private final RegistrationRepository registrationRepository;
    private final BlockchainService blockchainService;
    private final UserService userService;

    /* ================= LIST ON HOLD ================= */

    @GetMapping("/on-hold")
    public ResponseEntity<List<Registration>> getOnHoldRegistrations(
            @AuthenticationPrincipal UserDetails userDetails) {

        User admin = userService.getUserByEmail(userDetails.getUsername());

        if (admin.getRole() != Role.ADMIN) {
            throw new BhoomiDarpanException("Access denied");
        }

        return ResponseEntity.ok(
                registrationRepository.findByStatus(RegistrationStatus.ON_HOLD)
        );
    }

    /* ================= APPROVE ANYWAY ================= */

    @PostMapping("/{id}/approve-anyway")
    public ResponseEntity<?> approveAnyway(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        User admin = userService.getUserByEmail(userDetails.getUsername());

        if (admin.getRole() != Role.ADMIN) {
            throw new BhoomiDarpanException("Access denied");
        }

        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new BhoomiDarpanException("Registration not found"));

        if (registration.getStatus() != RegistrationStatus.ON_HOLD) {
            throw new BhoomiDarpanException("Registration is not under AI review");
        }

        Property property = registration.getProperty();
        User buyer = registration.getBuyer();

        // 1️⃣ Transfer ownership on blockchain
        String txHash = blockchainService.transferOwnershipOnBlockchain(
                property,
                buyer,
                registration.getSaleDeedHash(),
                admin
        );

        // 2️⃣ Update registration
        registration.setStatus(RegistrationStatus.APPROVED);
        registration.setApprovedBy(admin);
        registration.setApprovedAt(LocalDateTime.now());
        registration.setBlockchainHash(txHash);

        registrationRepository.save(registration);

        return ResponseEntity.ok("Registration approved and ownership transferred");
    }

    /* ================= REJECT FRAUD ================= */

    @PostMapping("/{id}/reject-fraud")
    public ResponseEntity<?> rejectFraud(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        User admin = userService.getUserByEmail(userDetails.getUsername());

        if (admin.getRole() != Role.ADMIN) {
            throw new BhoomiDarpanException("Access denied");
        }

        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new BhoomiDarpanException("Registration not found"));

        if (registration.getStatus() != RegistrationStatus.ON_HOLD) {
            throw new BhoomiDarpanException("Registration is not under AI review");
        }

        registration.setStatus(RegistrationStatus.REJECTED);
        registration.setApprovedBy(admin);
        registration.setApprovedAt(LocalDateTime.now());

        registrationRepository.save(registration);

        return ResponseEntity.ok("Registration rejected as fraud");
    }
}