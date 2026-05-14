package com.bhoomidarpan.entity;

import com.bhoomidarpan.entity.enums.MutationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "mutations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mutation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @ManyToOne
    @JoinColumn(name = "registration_id")
    private Registration registration;

    @Column(name = "mutation_number")
    private String mutationNumber; // e.g., MUT/2026/7821

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MutationStatus status;

    @Column(name = "seven_twelve_path")
    private String sevenTwelvePath;

    @Column(name = "eight_a_path")
    private String eightAPath;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}