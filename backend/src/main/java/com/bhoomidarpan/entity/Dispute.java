package com.bhoomidarpan.entity;

import com.bhoomidarpan.entity.enums.DisputeStatus;
import com.bhoomidarpan.entity.enums.DisputeType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "disputes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dispute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dispute_code", unique = true)
    private String disputeCode; // e.g., DSP-1007

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raised_by_id", nullable = false)
    private User raisedBy;

    @Column(name = "case_number")
    private String caseNumber;

    @Column(name = "court_name")
    private String courtName;

    @Enumerated(EnumType.STRING)
    @Column(name = "dispute_type")
    private DisputeType disputeType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DisputeStatus status;

    @Column(name = "court_order_path")
    private String courtOrderPath;

    @Column(name = "petition_copy_path")
    private String petitionCopyPath;

    @Column(name = "ocr_validation")
    private String ocrValidation;

    @Column(name = "closure_reason")
    private String closureReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "closed_by")
    private User closedBy;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "blockchain_hash")
    private String blockchainHash;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
