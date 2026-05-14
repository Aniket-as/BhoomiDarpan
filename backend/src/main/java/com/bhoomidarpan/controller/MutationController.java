package com.bhoomidarpan.controller;

import com.bhoomidarpan.dto.MutationRequest;
import com.bhoomidarpan.dto.MutationResponse;
import com.bhoomidarpan.entity.Mutation;
import com.bhoomidarpan.entity.Ownership;
import com.bhoomidarpan.entity.User;
import com.bhoomidarpan.entity.enums.Role;
import com.bhoomidarpan.exception.BhoomiDarpanException;
import com.bhoomidarpan.repository.OwnershipRepository;
import com.bhoomidarpan.service.MutationService;
import com.bhoomidarpan.service.UserService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;

@RestController
@RequestMapping("/mutation")
@RequiredArgsConstructor
public class MutationController {

    private final MutationService mutationService;
    private final UserService userService;
    private final OwnershipRepository ownershipRepository;

    @Transactional(readOnly = true)
    @GetMapping("/pending")
    public ResponseEntity<List<MutationResponse>> getPendingMutations(
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        User currentUser = mutationService
                .findByAadhaarNumber(userDetails.getUsername())
                .orElseThrow(() -> new BhoomiDarpanException("User not found"));

        if (currentUser.getRole() != Role.TEHSILDAR) {
            return ResponseEntity.status(403).build();
        }

        List<Mutation> mutations = mutationService.getPendingMutations();

        List<MutationResponse> response = mutations.stream()
                .map(this::convertToResponse)
                .toList();

        return ResponseEntity.ok(response);
    }


    @GetMapping("/my")
    public ResponseEntity<List<MutationResponse>> getMyMutations(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.getUserByEmail(userDetails.getUsername());

        List<MutationResponse> response =
                mutationService.getMutationsForUser(user.getId())
                        .stream()
                        .map(this::convertToResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }


    @PostMapping(
            value = "/process/{mutationId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> processMutation(
            @PathVariable Long mutationId,
            @ModelAttribute MutationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = mutationService
                .findByAadhaarNumber(userDetails.getUsername())
                .orElseThrow(() -> new BhoomiDarpanException("User not found"));

        System.out.println("Mutation ID: " + mutationId);
        System.out.println("SevenTwelve: " + request.getSevenTwelve());
        System.out.println("EightA: " + request.getEightA());

        mutationService.processMutation(mutationId, request, currentUser);

        return ResponseEntity.ok("Mutation processed successfully");
    }


    @PostMapping("/approve/{mutationId}")
    public ResponseEntity<?> approveMutation(
            @PathVariable Long mutationId,
            @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = mutationService
                .findByAadhaarNumber(userDetails.getUsername())
                .orElseThrow(() -> new BhoomiDarpanException("User not found"));

        mutationService.approveMutation(mutationId, currentUser);

        return ResponseEntity.ok("Mutation approved successfully. Ownership transferred.");
    }

    @PostMapping("/reject/{mutationId}")
    public ResponseEntity<?> rejectMutation(
            @PathVariable Long mutationId,
            @RequestParam String remarks,
            @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = mutationService
                .findByAadhaarNumber(userDetails.getUsername())
                .orElseThrow(() -> new BhoomiDarpanException("User not found"));

        mutationService.rejectMutation(mutationId, remarks, currentUser);

        return ResponseEntity.ok("Mutation rejected successfully");
    }

    @PostMapping("/create/{registrationId}")
    public ResponseEntity<?> createMutationFromRegistration(
            @PathVariable Long registrationId,
            @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = mutationService
                .findByAadhaarNumber(userDetails.getUsername())
                .orElseThrow(() -> new BhoomiDarpanException("User not found"));

        if (currentUser.getRole() != Role.TEHSILDAR) {
            return ResponseEntity.status(403).build();
        }

        Mutation mutation = mutationService.createMutationFromRegistration(registrationId);

        // ✅ RETURN FULL DATA
        return ResponseEntity.ok(mutation.getId());
    }

    @Transactional(readOnly = true)
    @GetMapping("/details/{mutationId}")
    public ResponseEntity<MutationResponse> getMutationDetails(
            @PathVariable Long mutationId,
            @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = mutationService
                .findByAadhaarNumber(userDetails.getUsername())
                .orElseThrow(() -> new BhoomiDarpanException("User not found"));

        Mutation mutation = mutationService.getMutationById(mutationId);

        return ResponseEntity.ok(convertToResponse(mutation));
    }


    private MutationResponse convertToResponse(Mutation mutation) {

        MutationResponse response = new MutationResponse();

        response.setId(mutation.getId());
        response.setPropertyId(mutation.getProperty().getId());
        response.setPropertyCode(mutation.getProperty().getPropertyCode());
        response.setMutationNumber(mutation.getMutationNumber());
        response.setStatus(mutation.getStatus().name());
        response.setRemarks(mutation.getRemarks());

        // Buyer
        response.setBuyerName(
                mutation.getRegistration().getBuyer().getName()
        );

        // Seller (current owner before mutation)
        Optional<Ownership> currentOwnerOpt =
                ownershipRepository.findCurrentOwnership(
                        mutation.getProperty().getId()
                );

        if (currentOwnerOpt.isPresent()) {
            User seller = currentOwnerOpt.get().getUser();
            response.setSellerName(seller.getName());
            response.setSellerAadhaar(seller.getAadhaarNumber());
            response.setSellerPan(seller.getPan());
        }



        if (mutation.getApprovedBy() != null) {
            response.setApprovedByName(mutation.getApprovedBy().getName());
            response.setApprovedAt(
                    mutation.getApprovedAt() != null
                            ? mutation.getApprovedAt().toString()
                            : null
            );
        }

        return response;
    }


}
