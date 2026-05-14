package com.bhoomidarpan.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.bhoomidarpan.entity.enums.TransactionStatus;


import java.time.LocalDateTime;

@Entity
@Table(name = "property_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @Column(name = "anomaly_flag")
    private Boolean anomalyFlag;

    @Column(name = "risk_level")
    private String riskLevel;


    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;

    private LocalDateTime createdAt;
}
