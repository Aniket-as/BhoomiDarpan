package com.bhoomidarpan.entity;

import com.bhoomidarpan.entity.enums.GiftDeedStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GiftDeedRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Property property;

    @ManyToOne
    private User requestedBy;

    private String donorName;
    private String relationship;

    private String childName;
    private String childAadhaar;
    private String childPan;

    private String transferReason;

    private String documentUrl;

    @Enumerated(EnumType.STRING)
    private GiftDeedStatus status;

    private String documentHash;


}