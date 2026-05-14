package com.bhoomidarpan.entity;

import com.bhoomidarpan.entity.enums.BuyRequestStatus;
import com.bhoomidarpan.entity.enums.VisitStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

@Entity
@Table(name = "buy_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ================= PROPERTY & BUYER ================= */

    @ManyToOne
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    /* ================= TRANSACTION DETAILS ================= */

    @Column(name = "offered_price")
    private Double offeredPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50, nullable = false)
    private BuyRequestStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "transaction_id")
    private String transactionId;

    /* ================= OWNER CONSENTS ================= */

    @OneToMany(mappedBy = "buyRequest", cascade = CascadeType.ALL)
    private Set<OwnerConsent> ownerConsents;

    /* ================= VISIT / APPOINTMENT ================= */

    @Column(name = "visit_date")
    private LocalDate visitDate;

    @Column(name = "visit_time_slot")
    private LocalTime visitTimeSlot;   // ✅ FIXED: REQUIRED FIELD

    @Enumerated(EnumType.STRING)
    @Column(name = "visit_status")
    private VisitStatus visitStatus;

    @Column(name = "risk_score")
    private Double riskScore;

    @Column(name = "risk_level")
    private String riskLevel;

    @Column(name = "risk_reason")
    private String riskReason;

}
