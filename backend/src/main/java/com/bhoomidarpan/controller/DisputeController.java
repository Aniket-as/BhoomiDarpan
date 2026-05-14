package com.bhoomidarpan.controller;

import com.bhoomidarpan.dto.*;
import com.bhoomidarpan.entity.Dispute;
import com.bhoomidarpan.entity.User;
import com.bhoomidarpan.entity.enums.DisputeStatus;
import com.bhoomidarpan.exception.BhoomiDarpanException;
import com.bhoomidarpan.repository.UserRepository;
import com.bhoomidarpan.service.DisputeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dispute")
@RequiredArgsConstructor
public class DisputeController {

    private final DisputeService disputeService;
    private final UserRepository userRepository;

    /* ================= CREATE DISPUTE ================= */

    @PostMapping("/request")
    public ResponseEntity<?> createDisputeRequest(
            @Valid @ModelAttribute DisputeRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = getUser(userDetails);
        Dispute dispute = disputeService.createDisputeRequest(request, currentUser);

        return ResponseEntity.ok(
                "Dispute request created with code: " + dispute.getDisputeCode()
        );
    }

    /* ================= MY DISPUTES ================= */

    @GetMapping("/my-disputes")
    public ResponseEntity<List<DisputeResponse>> getMyDisputes(
            @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = getUser(userDetails);

        List<Dispute> disputes =
                disputeService.getDisputesByUser(currentUser.getId());

        List<DisputeResponse> response = disputes.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /* ================= SUB-REGISTRAR ================= */

    @GetMapping("/pending")
    public ResponseEntity<List<DisputeResponse>> getPendingDisputes(
            @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = getUser(userDetails);

        if (!currentUser.getRole().name().equals("SUB_REGISTRAR")) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(
                disputeService.getDisputesByStatus(DisputeStatus.REQUESTED)
                        .stream()
                        .map(this::convertToResponse)
                        .toList()
        );
    }

    @GetMapping("/active")
    public ResponseEntity<List<DisputeResponse>> getActiveDisputes(
            @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = getUser(userDetails);

        if (!currentUser.getRole().name().equals("SUB_REGISTRAR")) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(
                disputeService.getDisputesByStatus(DisputeStatus.ACTIVE)
                        .stream()
                        .map(this::convertToResponse)
                        .toList()
        );
    }

    @PostMapping("/approve/{disputeId}")
    public ResponseEntity<?> approveDispute(
            @PathVariable Long disputeId,
            @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = getUser(userDetails);
        Dispute dispute = disputeService.approveDispute(disputeId, currentUser);

        return ResponseEntity.ok(
                "Dispute approved. Blockchain hash: " + dispute.getBlockchainHash()
        );
    }

    /* ================= CLOSE DISPUTE ================= */

    @PostMapping("/close")
    public ResponseEntity<?> closeDispute(
            @Valid @ModelAttribute DisputeClosureRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = getUser(userDetails);
        Dispute dispute = disputeService.closeDispute(request, currentUser);

        return ResponseEntity.ok(
                "Dispute closed successfully. Blockchain hash: " + dispute.getBlockchainHash()
        );
    }

    /* ================= PROPERTY ================= */

    @GetMapping("/property/{propertyId}/active")
    public ResponseEntity<List<DisputeResponse>> getActiveDisputesForProperty(
            @PathVariable Long propertyId) {

        return ResponseEntity.ok(
                disputeService.getActiveDisputesForProperty(propertyId)
                        .stream()
                        .map(this::convertToResponse)
                        .toList()
        );
    }

    /* ================= HELPERS ================= */

    private User getUser(UserDetails userDetails) {

        if (userDetails == null) {
            throw new BhoomiDarpanException("Unauthorized");
        }

        String aadhaar = userDetails.getUsername();

        return userRepository.findByAadhaarNumber(aadhaar)
                .orElseThrow(() -> new BhoomiDarpanException("User not found"));
    }
    private DisputeResponse convertToResponse(Dispute dispute) {
        DisputeResponse res = new DisputeResponse();
        res.setId(dispute.getId());
        res.setDisputeCode(dispute.getDisputeCode());
        res.setPropertyCode(dispute.getProperty().getPropertyCode());
        res.setRaisedByName(dispute.getRaisedBy().getName());
        res.setCaseNumber(dispute.getCaseNumber());
        res.setCourtName(dispute.getCourtName());
        res.setDisputeType(
                dispute.getDisputeType() != null
                        ? dispute.getDisputeType().name()
                        : null
        );
        res.setStatus(dispute.getStatus().name());
        res.setCreatedAt(dispute.getCreatedAt().toString());
        res.setOcrValidation(dispute.getOcrValidation());
        return res;
    }
}
