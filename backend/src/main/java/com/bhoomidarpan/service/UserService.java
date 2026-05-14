package com.bhoomidarpan.service;

import com.bhoomidarpan.dto.RegisterRequest;
import com.bhoomidarpan.entity.User;
import com.bhoomidarpan.entity.enums.Role;
import com.bhoomidarpan.exception.BhoomiDarpanException;
import com.bhoomidarpan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /* ===================== REGISTRATION ===================== */

    @Transactional
    public User registerUser(RegisterRequest request) {

        Role role = Role.USER;

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BhoomiDarpanException("Email already registered");
        }

        if (request.getAadhaarNumber() != null &&
                userRepository.existsByAadhaarNumber(request.getAadhaarNumber())) {
            throw new BhoomiDarpanException("Aadhaar already registered");
        }

        if (request.getPan() != null &&
                userRepository.existsByPan(request.getPan())) {
            throw new BhoomiDarpanException("PAN already registered");
        }

        if (request.getAadhaarNumber() == null) {
            throw new BhoomiDarpanException("Aadhaar is required for citizens");
        }


        // ✅ ALWAYS DEFAULT ROLE


        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .aadhaarNumber(request.getAadhaarNumber())
                .pan(request.getPan())
                .mobile(request.getMobile())
                .role(role)
                .active(true)
                .build();

        return userRepository.save(user);
    }

    /* ===================== QUERY METHODS ===================== */

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BhoomiDarpanException("User not found"));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BhoomiDarpanException("User not found"));
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    /* ===================== BIOMETRIC UPDATES ===================== */

    @Transactional
    public void updateUserPhoto(Long userId, byte[] photo) {
        User user = getUserById(userId);
        user.setPhoto(photo);
        userRepository.save(user);
    }

    @Transactional
    public void updateUserFingerprint(Long userId, byte[] fingerprint) {
        User user = getUserById(userId);
        user.setFingerprint(fingerprint);
        userRepository.save(user);
    }
    @Transactional
    public void updateUserStatus(Long userId, boolean active) {
        User user = getUserById(userId);
        user.setActive(active);
        userRepository.save(user);
    }

    public Optional<User> findByAadhaarNumber(String aadhaarNumber) {
        return userRepository.findByAadhaarNumber(aadhaarNumber);
    }

}
