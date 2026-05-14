package com.bhoomidarpan.controller;


import com.bhoomidarpan.dto.InheritanceRequestDTO;
import com.bhoomidarpan.entity.User;
import com.bhoomidarpan.service.InheritanceService;
import com.bhoomidarpan.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inheritance")
@RequiredArgsConstructor
public class InheritanceController {

    private final InheritanceService inheritanceService;
    private final UserService userService;

    @PostMapping("/request")
    public ResponseEntity<?> submitRequest(
            @ModelAttribute InheritanceRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {


        User currentUser = userService.getUserByEmail(userDetails.getUsername());
        inheritanceService.createRequest(request, currentUser);
        return ResponseEntity.ok("Inheritance mutation request submitted");
    }
}

