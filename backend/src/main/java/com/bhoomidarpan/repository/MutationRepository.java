package com.bhoomidarpan.repository;

import com.bhoomidarpan.entity.Mutation;
import com.bhoomidarpan.entity.enums.MutationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MutationRepository extends JpaRepository<Mutation, Long> {
    Optional<Mutation> findByMutationNumber(String mutationNumber);
    List<Mutation> findByPropertyId(Long propertyId);
    List<Mutation> findByStatus(MutationStatus status);

    @Query("SELECT m FROM Mutation m WHERE m.status = 'PENDING' " +
            "ORDER BY m.createdAt DESC")
    List<Mutation> findPendingMutations();

    @Query("SELECT m FROM Mutation m WHERE m.registration.id = :registrationId")
    Optional<Mutation> findByRegistrationId(
            @Param("registrationId") Long registrationId
    );


    @Query("""
SELECT m FROM Mutation m
LEFT JOIN FETCH m.property
LEFT JOIN FETCH m.registration r
LEFT JOIN FETCH r.buyer
LEFT JOIN FETCH m.user
LEFT JOIN FETCH m.approvedBy
WHERE m.id = :id
""")
    Optional<Mutation> findDetailedById(@Param("id") Long id);




    @Query("""
SELECT m FROM Mutation m
JOIN m.registration r
WHERE r.buyer.id = :userId
   OR r.property.id IN (
       SELECT o.property.id FROM Ownership o
       WHERE o.user.id = :userId AND o.current = true
   )
""")
    List<Mutation> findByBuyerIdOrOwnerId(@Param("userId") Long userId);


    @Query("""
SELECT DISTINCT m FROM Mutation m
LEFT JOIN FETCH m.property p
LEFT JOIN FETCH p.ownerships
LEFT JOIN FETCH m.registration r
LEFT JOIN FETCH r.buyer
LEFT JOIN FETCH m.user
LEFT JOIN FETCH m.approvedBy
WHERE m.status = com.bhoomidarpan.entity.enums.MutationStatus.PENDING
ORDER BY m.createdAt DESC
""")
    List<Mutation> findPendingWithDetails();





    @Query("""
    SELECT COUNT(m) > 0 FROM Mutation m
    WHERE m.property.id = :propertyId
    AND m.status = :status
    """)
    boolean existsByPropertyIdAndStatus(
            @Param("propertyId") Long propertyId,
            @Param("status") MutationStatus status
    );

    long countByUserIdAndStatus(Long userId, MutationStatus status);
}