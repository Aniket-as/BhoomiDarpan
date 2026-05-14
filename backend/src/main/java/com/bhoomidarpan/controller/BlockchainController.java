package com.bhoomidarpan.controller;

import com.bhoomidarpan.entity.BlockchainAuditLog;
import com.bhoomidarpan.entity.User;
import com.bhoomidarpan.repository.BlockchainAuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/blockchain")
@RequiredArgsConstructor
public class BlockchainController {

    private final BlockchainAuditLogRepository auditLogRepository;

    @GetMapping("/audit/{propertyId}")
    public ResponseEntity<List<BlockchainAuditLog>> getAuditLogs(
            @PathVariable Long propertyId,
            @AuthenticationPrincipal User currentUser) {

        // Check if user is authorized (Sub-Registrar, Tehsildar, or property owner)
        // This is simplified - in production, add proper authorization

        List<BlockchainAuditLog> auditLogs = auditLogRepository.findByPropertyId(propertyId);
        return ResponseEntity.ok(auditLogs);
    }

    @GetMapping("/verify/{transactionHash}")
    public ResponseEntity<Boolean> verifyTransaction(
            @PathVariable String transactionHash,
            @AuthenticationPrincipal User currentUser) {

        boolean exists = auditLogRepository.existsByTransactionHash(transactionHash);
        return ResponseEntity.ok(exists);
    }
}