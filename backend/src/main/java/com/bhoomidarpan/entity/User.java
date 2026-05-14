package com.bhoomidarpan.entity;

import com.bhoomidarpan.entity.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "aadhaar"),
        @UniqueConstraint(columnNames = "pan")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;


    @Column(name = "aadhaar", unique = true, nullable = false, length = 12)
    private String aadhaarNumber;


    @Column(unique = true, nullable = false, length = 10)
    private String pan;

    @Column(nullable = false, length = 10)
    private String mobile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Lob
    @Column(name = "photo_blob")
    private byte[] photo;

    @Lob
    @Column(name = "fingerprint_blob")
    private byte[] fingerprint;
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Ownership> ownerships;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Certificate> certificates;

    @Column(name = "wallet_address")
    private String walletAddress;
}