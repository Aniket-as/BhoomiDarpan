package com.bhoomidarpan.controller;

import com.bhoomidarpan.entity.AdminLog;
import com.bhoomidarpan.service.AdminLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminLogController {

    private final AdminLogService adminLogService;

    @GetMapping
    public ResponseEntity<List<AdminLog>> getLogs() {
        return ResponseEntity.ok(adminLogService.getAllLogs());
    }
}
