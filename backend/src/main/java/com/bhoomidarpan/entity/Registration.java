package com.bhoomidarpan.entity;

import com.bhoomidarpan.entity.enums.RegistrationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "registrations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Registration {

    /* ================= PRIMARY KEY ================= */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ================= RELATIONSHIPS ================= */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    /* ================= FILE STORAGE ================= */

    @Column(name = "sale_deed_path", nullable = false)
    private String saleDeedPath;

    @Column(name = "sale_deed_hash", length = 66)
    private String saleDeedHash; // 0x + 64 hex chars (SHA-256)

    /* ================= OCR DATA ================= */

    @Column(name = "ocr_result", columnDefinition = "TEXT")
    private String ocrResult; // full extracted text

    @Column(name = "ocr_extracted_fields", columnDefinition = "TEXT")
    private String ocrExtractedFields; // JSON structured fields

    /* ================= APPOINTMENT ================= */

    @Column(name = "appointment_date", nullable = false)
    private LocalDateTime appointmentDate;

    /* ================= STATUS ================= */

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegistrationStatus status;

    /* ================= APPROVAL ================= */

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    /* ================= BLOCKCHAIN ================= */

    @Column(name = "blockchain_hash", length = 120)
    private String blockchainHash;

    @Column
    private Boolean blockchainTransferCompleted;

    @Column
    private String reverseTransactionHash;

    /* ================= AI RISK ANALYSIS ================= */

    @Column(name = "ai_flag")
    private Boolean aiFlag;

    @Column(name = "risk_score")
    private Double riskScore;

    @Column(name = "risk_reason", columnDefinition = "TEXT")
    private String riskReason;

    /* ================= AUDIT ================= */

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;


    private String buyerPhotoUrl;
    private String sellerPhotoUrl;
    private String buyerFingerprintUrl;
    private String sellerFingerprintUrl;
}
