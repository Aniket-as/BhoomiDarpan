package com.bhoomidarpan.service;

import com.bhoomidarpan.entity.AdminLog;
import com.bhoomidarpan.repository.AdminLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminLogService {

    private final AdminLogRepository logRepository;

    public void log(
            String action,
            String module,
            String performedBy,
            String role,
            String description
    ) {
        AdminLog log = AdminLog.builder()
                .action(action)
                .module(module)
                .performedBy(performedBy)
                .role(role)
                .description(description)
                .createdAt(LocalDateTime.now())
                .build();

        logRepository.save(log);
    }

    public List<AdminLog> getAllLogs() {
        return logRepository.findAll()
                .stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .toList();
    }
}
