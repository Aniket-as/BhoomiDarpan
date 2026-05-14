package com.bhoomidarpan.repository;


import com.bhoomidarpan.entity.OwnerConsent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OwnerConsentRepository extends JpaRepository<OwnerConsent, Long> {
    List<OwnerConsent> findByBuyRequestId(Long buyRequestId);
    Optional<OwnerConsent> findByBuyRequestIdAndOwnerId(Long buyRequestId, Long ownerId);

    @Query("SELECT COUNT(oc) FROM OwnerConsent oc WHERE oc.buyRequest.id = :buyRequestId " +
            "AND oc.status = 'APPROVED'")
    Long countApprovedConsents(Long buyRequestId);

    @Query("SELECT COUNT(oc) FROM OwnerConsent oc WHERE oc.buyRequest.id = :buyRequestId " +
            "AND oc.status = 'REJECTED'")
    Long countRejectedConsents(Long buyRequestId);




}