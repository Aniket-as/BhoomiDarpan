package com.bhoomidarpan.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileUrl;

    private String documentType;

    private boolean verified;

    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;
}
