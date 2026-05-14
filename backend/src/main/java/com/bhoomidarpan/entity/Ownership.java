package com.bhoomidarpan.entity;

import com.bhoomidarpan.entity.enums.OwnershipType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ownerships") // ❌ Removed unique constraint
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ownership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "ownership_percentage", nullable = false)
    private Double ownershipPercentage;

    @Enumerated(EnumType.STRING)
    @Column(name = "ownership_type", nullable = false)
    private OwnershipType ownershipType;

    @Builder.Default
    @Column(name = "is_current", nullable = false)
    private boolean current = true;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;
}