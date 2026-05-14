package com.bhoomidarpan.service;

import com.bhoomidarpan.dto.AdminCreateUserRequest;
import com.bhoomidarpan.dto.AdminUserResponse;
import com.bhoomidarpan.dto.DisputeResponse;
import com.bhoomidarpan.entity.Dispute;
import com.bhoomidarpan.entity.User;
import com.bhoomidarpan.entity.enums.DisputeStatus;
import com.bhoomidarpan.entity.enums.Role;
import com.bhoomidarpan.exception.BhoomiDarpanException;
import com.bhoomidarpan.repository.DisputeRepository;
import com.bhoomidarpan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /* ===================== CREATE USER ===================== */

    @Transactional
    public User createUserByAdmin(AdminCreateUserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BhoomiDarpanException("Email already exists");
        }

        Role role;
        try {
            role = Role.valueOf(request.getRole().toUpperCase());
        } catch (Exception e) {
            throw new BhoomiDarpanException("Invalid role");
        }

        if (role == Role.ADMIN) {
            throw new BhoomiDarpanException("Admin cannot create another ADMIN");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .mobile(request.getMobile())
                .aadhaarNumber(request.getAadhaarNumber())
                .pan(request.getPan())
                .role(role)
                .active(true)
                .build();

        return userRepository.save(user);
    }

    /* ===================== FETCH USERS ===================== */

    public List<AdminUserResponse> getAllUsersManage() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<AdminUserResponse> getUsersByRole(Role role) {
        return userRepository.findByRole(role)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /* ===================== UPDATE ===================== */

    @Transactional
    public void toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BhoomiDarpanException("User not found"));

        if (user.getRole() == Role.ADMIN) {
            throw new BhoomiDarpanException("Cannot disable ADMIN");
        }

        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    @Transactional
    public void updateUserRole(Long userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BhoomiDarpanException("User not found"));

        if (user.getRole() == Role.ADMIN) {
            throw new BhoomiDarpanException("Cannot modify ADMIN role");
        }

        user.setRole(role);
        userRepository.save(user);
    }

    /* ===================== MAPPER ===================== */

    private AdminUserResponse mapToResponse(User u) {
        AdminUserResponse r = new AdminUserResponse();
        r.setId(u.getId());
        r.setName(u.getName());
        r.setEmail(u.getEmail());
        r.setMobile(u.getMobile());
        r.setRole(u.getRole().name());
        r.setActive(u.isActive());
        return r;
    }

    public List<AdminUserResponse> getAllOfficers() {
        return userRepository
                .findByRoleIn(List.of(Role.SUB_REGISTRAR, Role.TEHSILDAR))
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public void updateOfficerRole(Long userId, Role role) {

        if (role != Role.SUB_REGISTRAR && role != Role.TEHSILDAR) {
            throw new BhoomiDarpanException("Invalid officer role");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BhoomiDarpanException("User not found"));

        user.setRole(role);
        userRepository.save(user);
    }

    @Autowired
    private DisputeRepository disputeRepository;

    public List<DisputeResponse> getAllDisputes() {
        return disputeRepository.findAll()
                .stream()
                .map(this::mapDispute)
                .toList();
    }

    public List<DisputeResponse> getDisputesByStatus(DisputeStatus status) {
        return disputeRepository.findByStatus(status)
                .stream()
                .map(this::mapDispute)
                .toList();
    }

    private DisputeResponse mapDispute(Dispute d) {
        DisputeResponse r = new DisputeResponse();
        r.setId(d.getId());
        r.setDisputeCode(d.getDisputeCode());
        r.setPropertyCode(d.getProperty().getPropertyCode());
        r.setRaisedByName(d.getRaisedBy().getName());
        r.setCaseNumber(d.getCaseNumber());
        r.setCourtName(d.getCourtName());
        r.setDisputeType(
                d.getDisputeType() != null ? d.getDisputeType().name() : null
        );
        r.setStatus(d.getStatus().name());
        r.setCreatedAt(d.getCreatedAt().toString());
        r.setOcrValidation(d.getOcrValidation());
        return r;
    }


}
