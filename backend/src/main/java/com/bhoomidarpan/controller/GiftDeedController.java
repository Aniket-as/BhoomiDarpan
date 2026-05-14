package com.bhoomidarpan.controller;

import com.bhoomidarpan.dto.GiftDeedRequestDTO;
import com.bhoomidarpan.entity.User;
import com.bhoomidarpan.exception.BhoomiDarpanException;
import com.bhoomidarpan.service.GiftDeedService;
import com.bhoomidarpan.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/gift-deed")
@RequiredArgsConstructor
public class GiftDeedController {

    private final GiftDeedService giftDeedService;
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(GiftDeedController.class);

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingRequests() {
        return ResponseEntity.ok(giftDeedService.getPendingRequests());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGiftById(@PathVariable Long id) {
        return ResponseEntity.ok(giftDeedService.getGiftById(id));
    }

    @PostMapping("/request")
    public ResponseEntity<?> submitRequest(
            @ModelAttribute GiftDeedRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {

            if (userDetails == null) {
                throw new BhoomiDarpanException("Unauthorized");
            }

            User currentUser = userService
                    .findByAadhaarNumber(userDetails.getUsername())
                    .orElseThrow(() -> new BhoomiDarpanException("User not found"));

            giftDeedService.createRequest(request, currentUser);

            return ResponseEntity.ok("Gift Deed request submitted successfully");

        } catch (BhoomiDarpanException e) {

            log.warn("Gift Deed validation error: {}", e.getMessage());

            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (Exception e) {

            log.error("Gift Deed system error", e);

            return ResponseEntity.internalServerError()
                    .body("Something went wrong. Please try again.");
        }
    }
}