package com.bhoomidarpan.repository;

import com.bhoomidarpan.dto.DisputeResponse;
import com.bhoomidarpan.entity.Dispute;
import com.bhoomidarpan.entity.enums.DisputeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DisputeRepository extends JpaRepository<Dispute, Long> {
    Optional<Dispute> findByDisputeCode(String disputeCode);
    List<Dispute> findByPropertyId(Long propertyId);

    List<Dispute> findByStatus(DisputeStatus status);

    long countByPropertyIdAndStatusNot(Long propertyId, DisputeStatus status);

    @Query("SELECT d FROM Dispute d WHERE d.property.id = :propertyId " +
            "AND d.status IN ('ACTIVE', 'UNDER_REVIEW')")
    List<Dispute> findActiveDisputes(Long propertyId);

    @Query("SELECT COUNT(d) > 0 FROM Dispute d WHERE d.property.id = :propertyId " +
            "AND d.status IN ('ACTIVE', 'UNDER_REVIEW')")
    boolean hasActiveDispute(Long propertyId);

    @Query("""
SELECT COUNT(d) > 0 FROM Dispute d
WHERE d.property.id = :propertyId AND d.status <> 'CLOSED'
""")
    boolean existsActiveDispute(Long propertyId);

    @Query("""
SELECT d FROM Dispute d
JOIN FETCH d.property
JOIN FETCH d.raisedBy
WHERE d.raisedBy.id = :userId
""")
    List<Dispute> findByRaisedById(@Param("userId") Long userId);


    @Query("""
SELECT new com.bhoomidarpan.dto.DisputeResponse(
d.id,
d.disputeCode,
p.propertyCode,
u.name,
d.caseNumber,
d.courtName,
d.disputeType,
d.status,
d.createdAt
)
FROM Dispute d
JOIN d.property p
JOIN d.raisedBy u
WHERE u.id = :userId
""")
    List<DisputeResponse> findUserDisputes(Long userId);

    @Query("""
        SELECT d FROM Dispute d
        JOIN FETCH d.property
        JOIN FETCH d.raisedBy
        WHERE d.status = :status
    """)
    List<Dispute> findByStatusWithProperty(@Param("status") DisputeStatus status);

    @Query("""
    SELECT d FROM Dispute d
    JOIN FETCH d.property
    JOIN FETCH d.raisedBy
    WHERE d.raisedBy.id = :userId
""")
    List<Dispute> findByRaisedByIdWithProperty(@Param("userId") Long userId);


    @Query("""
SELECT d FROM Dispute d
JOIN FETCH d.property p
JOIN FETCH d.raisedBy u
WHERE u.id = :userId
   OR p.id IN (
       SELECT o.property.id FROM Ownership o WHERE o.user.id = :userId
   )
""")
    List<Dispute> findUserRelatedDisputes(@Param("userId") Long userId);
}