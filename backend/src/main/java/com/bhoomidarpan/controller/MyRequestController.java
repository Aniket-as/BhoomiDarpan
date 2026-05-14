package com.bhoomidarpan.controller;

import com.bhoomidarpan.dto.MyRequestResponse;
import com.bhoomidarpan.entity.User;
import com.bhoomidarpan.repository.BuyRequestRepository;
import com.bhoomidarpan.repository.DisputeRepository;
import com.bhoomidarpan.repository.InheritanceRepository;
import com.bhoomidarpan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class MyRequestController {

    private final BuyRequestRepository buyRequestRepository;
    private final InheritanceRepository inheritanceRepository;
    private final DisputeRepository disputeRepository;
    private final UserRepository userRepository;

    @GetMapping("/my")
    public ResponseEntity<List<MyRequestResponse>> getMyRequests(Authentication authentication) {

        // 🔐 STEP 1: Get logged-in email from SecurityContext
        String email = authentication.getName();

        // 🔐 STEP 2: Load actual User entity
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<MyRequestResponse> responses = new ArrayList<>();

        // ================= BUY REQUESTS =================
        buyRequestRepository.findByBuyerId(currentUser.getId())
                .forEach(req -> {
                    MyRequestResponse r = new MyRequestResponse();
                    r.setId(req.getId());
                    r.setRequestType("🏘️ Buy Property");
                    r.setPropertyCode(req.getProperty().getPropertyCode());
                    r.setLocation(req.getProperty().getLocation());
                    r.setStatus(req.getStatus().name());
                    r.setCreatedAt(req.getCreatedAt().toLocalDate().toString());
                    r.setRedirectUrl("/transactions");
                    responses.add(r);
                });

        // ================= INHERITANCE REQUESTS =================
        inheritanceRepository.findByRequestedById(currentUser.getId())
                .forEach(req -> {
                    MyRequestResponse r = new MyRequestResponse();
                    r.setId(req.getId());
                    r.setRequestType("🕊️ Inheritance Mutation");
                    r.setPropertyCode(req.getProperty().getPropertyCode());
                    r.setLocation(req.getProperty().getLocation());
                    r.setStatus(req.getStatus());
                    r.setCreatedAt(req.getCreatedAt().toLocalDate().toString());
                    r.setRedirectUrl("/mutation-tracking");
                    responses.add(r);
                });

        // ================= DISPUTE REQUESTS =================
        disputeRepository.findByRaisedById(currentUser.getId())
                .forEach(d -> {
                    MyRequestResponse r = new MyRequestResponse();
                    r.setId(d.getId());
                    r.setRequestType("⚖️ Dispute Filed");
                    r.setPropertyCode(d.getProperty().getPropertyCode());
                    r.setLocation(d.getProperty().getLocation());
                    r.setStatus(d.getStatus().name());
                    r.setCreatedAt(d.getCreatedAt().toLocalDate().toString());
                    r.setRedirectUrl("/dispute-status");
                    responses.add(r);
                });

        return ResponseEntity.ok(responses);
    }
}
