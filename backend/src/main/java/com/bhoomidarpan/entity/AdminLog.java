package com.bhoomidarpan.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "admin_logs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action;          // CREATE_USER, CLOSE_DISPUTE etc
    private String module;          // USER, DISPUTE, PROPERTY

    private String performedBy;     // Admin email
    private String role;            // ADMIN

    private String description;

    private LocalDateTime createdAt;
}
