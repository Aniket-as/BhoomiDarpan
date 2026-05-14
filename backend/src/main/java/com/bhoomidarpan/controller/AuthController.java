package com.bhoomidarpan.controller;

import com.bhoomidarpan.dto.LoginRequest;
import com.bhoomidarpan.dto.LoginResponse;
import com.bhoomidarpan.dto.RegisterRequest;
import com.bhoomidarpan.entity.User;
import com.bhoomidarpan.security.JwtTokenProvider;
import com.bhoomidarpan.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(
            @Valid @RequestBody LoginRequest loginRequest
    ) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequest.getAadhaarNumber(),
                                loginRequest.getPassword()
                        )

                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        // ✅ Generate JWT
        String jwt = jwtTokenProvider.generateToken(userDetails);

        // ✅ FETCH ENTITY SAFELY
        User user = userService.findByAadhaarNumber(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        LoginResponse response = new LoginResponse();
        response.setToken(jwt);
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        User user = userService.registerUser(registerRequest);
        return ResponseEntity.ok("User registered successfully with ID: " + user.getId());
    }
}
