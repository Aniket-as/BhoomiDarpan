package com.bhoomidarpan.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inheritance_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InheritanceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Property involved
    @ManyToOne
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    // Who raised request
    @ManyToOne
    @JoinColumn(name = "requested_by", nullable = false)
    private User requestedBy;

    @Column(name = "deceased_name", nullable = false)
    private String deceasedName;

    @Column(name = "date_of_death", nullable = false)
    private LocalDate dateOfDeath;

    @Lob
    @Column(name = "death_certificate")
    private byte[] deathCertificate;

    @Lob
    @Column(name = "legal_heir_certificate")
    private byte[] legalHeirCert;

    @Lob
    @Column(name = "will_document")
    private byte[] willDocument;

    @Column(name = "status")
    private String status; // PENDING / APPROVED / REJECTED

    @CreationTimestamp
    private LocalDateTime createdAt;
}

