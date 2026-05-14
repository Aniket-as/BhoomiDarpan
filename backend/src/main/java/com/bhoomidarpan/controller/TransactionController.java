package com.bhoomidarpan.controller;

import com.bhoomidarpan.dto.TransactionResponse;
import com.bhoomidarpan.entity.User;
import com.bhoomidarpan.repository.PropertyRepository;
import com.bhoomidarpan.repository.UserRepository;
import com.bhoomidarpan.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;


    @GetMapping("/my")
    public ResponseEntity<List<TransactionResponse>> getMyTransactions(
            @AuthenticationPrincipal User currentUser) {

        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(
                transactionService.getUserTransactions(currentUser)
        );
    }
}
