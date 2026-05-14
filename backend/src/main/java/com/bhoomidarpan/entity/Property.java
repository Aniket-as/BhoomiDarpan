package com.bhoomidarpan.entity;

import com.bhoomidarpan.entity.enums.LandType;
import com.bhoomidarpan.entity.enums.PropertyStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "properties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "property_code", unique = true, nullable = false)
    private String propertyCode; // e.g., PROP-1023

    @Column(nullable = false)
    private String location;

    @Column(name = "survey_number")
    private String surveyNumber;

    @Column(name = "gat_number")
    private String gatNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "land_type", nullable = false)
    private LandType landType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyStatus status;

    @Column(name = "area_sqft")
    private Double area;

    @Builder.Default
    @Column(name = "available_for_sale")
    private boolean availableForSale = false;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL)
    private Set<Ownership> ownerships;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL)
    private Set<BuyRequest> buyRequests;

    @Column(name = "document_hash", length = 66)
    private String documentHash;

    @Column(length = 100)
    private String blockchainTxHash;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL)
    private Set<Dispute> disputes;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL)
    private Set<Certificate> certificates;


    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Document> documents;
}