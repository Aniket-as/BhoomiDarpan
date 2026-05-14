package com.bhoomidarpan.controller;

import com.bhoomidarpan.dto.AdminCreateUserRequest;
import com.bhoomidarpan.dto.AdminUserResponse;
import com.bhoomidarpan.dto.DisputeResponse;
import com.bhoomidarpan.entity.enums.DisputeStatus;
import com.bhoomidarpan.entity.enums.Role;
import com.bhoomidarpan.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    /* ===================== USERS ===================== */

    // ✅ Get all users (for manage-users page)
    @GetMapping("/users")
    public ResponseEntity<List<AdminUserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsersManage());
    }

    // ✅ Get users by role (USER / SUB_REGISTRAR / TEHSILDAR)
    @GetMapping("/users/role/{role}")
    public ResponseEntity<List<AdminUserResponse>> getUsersByRole(
            @PathVariable Role role
    ) {
        return ResponseEntity.ok(adminService.getUsersByRole(role));
    }

    // ✅ Toggle ACTIVE / INACTIVE
    @PutMapping("/users/{id}/status")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long id) {
        adminService.toggleUserStatus(id);
        return ResponseEntity.ok("User status updated successfully");
    }

    // ✅ Update role
    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> updateUserRole(
            @PathVariable Long id,
            @RequestParam Role role
    ) {
        adminService.updateUserRole(id, role);
        return ResponseEntity.ok("User role updated successfully");
    }

    // ✅ Admin creates USER / SUB_REGISTRAR / TEHSILDAR
    @PostMapping("/add-user")
    public ResponseEntity<?> addUser(
            @RequestBody AdminCreateUserRequest request
    ) {
        return ResponseEntity.ok(
                "User created with ID: " +
                        adminService.createUserByAdmin(request).getId()
        );
    }
    /* ===================== OFFICERS ===================== */

    // Get all officers (SUB_REGISTRAR + TEHSILDAR)
    @GetMapping("/officers")
    public ResponseEntity<List<AdminUserResponse>> getAllOfficers() {
        return ResponseEntity.ok(adminService.getAllOfficers());
    }

    // Change officer role
    @PutMapping("/officers/{id}/role")
    public ResponseEntity<?> updateOfficerRole(
            @PathVariable Long id,
            @RequestParam Role role
    ) {
        adminService.updateOfficerRole(id, role);
        return ResponseEntity.ok("Officer role updated");
    }

    // Toggle officer status
    @PutMapping("/officers/{id}/status")
    public ResponseEntity<?> toggleOfficerStatus(@PathVariable Long id) {
        adminService.toggleUserStatus(id);
        return ResponseEntity.ok("Officer status updated");
    }
    /* ===================== ADMIN – DISPUTES ===================== */

    @GetMapping("/disputes")
    public ResponseEntity<List<DisputeResponse>> getAllDisputes() {
        return ResponseEntity.ok(adminService.getAllDisputes());
    }

    @GetMapping("/disputes/status/{status}")
    public ResponseEntity<List<DisputeResponse>> getDisputesByStatus(
            @PathVariable DisputeStatus status
    ) {
        return ResponseEntity.ok(adminService.getDisputesByStatus(status));
    }

}
