package com.bhoomidarpan.entity;

import com.bhoomidarpan.entity.enums.BlockchainActionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "blockchain_audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlockchainAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private BlockchainActionType actionType;

    @Column(name = "transaction_hash", unique = true)
    private String transactionHash;

    @Column(name = "document_hash")
    private String documentHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "officer_id", nullable = false)
    private User officer;


    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime timestamp;

    @Column(columnDefinition = "TEXT")
    private String metadata;
}