package com.bhoomidarpan.repository;

import com.bhoomidarpan.entity.BuyRequest;
import com.bhoomidarpan.entity.enums.BuyRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuyRequestRepository extends JpaRepository<BuyRequest, Long> {

    /* =========================================================
       BASIC QUERIES (UNCHANGED – SAFE)
       ========================================================= */


    List<BuyRequest> findByPropertyId(Long propertyId);

    List<BuyRequest> findByStatus(BuyRequestStatus status);

    /* =========================================================
       OWNER VIEW – PENDING / ACTIVE REQUESTS
       (UPDATED: enum-based, supports workflow expansion)
       ========================================================= */

    @Query("""
        SELECT br
        FROM BuyRequest br
        JOIN br.property p
        JOIN p.ownerships o
        WHERE o.user.id = :ownerId
          AND o.current = true
          AND br.status IN :statuses
    """)
    List<BuyRequest> findRequestsForOwnerByStatus(
            @Param("ownerId") Long ownerId,
            @Param("statuses") List<BuyRequestStatus> statuses
    );

    /* ---------------------------------------------------------
       BACKWARD-COMPATIBLE METHOD (DO NOT REMOVE)
       Existing services may still call this.
       --------------------------------------------------------- */
    default List<BuyRequest> findPendingRequestsForOwner(Long ownerId) {
        return findRequestsForOwnerByStatus(
                ownerId,
                List.of(BuyRequestStatus.PENDING)
        );
    }

    /* =========================================================
       BUYER + PROPERTY CHECK (ACTIVE REQUEST)
       ========================================================= */

    @Query("""
        SELECT br
        FROM BuyRequest br
        WHERE br.property.id = :propertyId
          AND br.buyer.id = :buyerId
          AND br.status IN :statuses
    """)
    Optional<BuyRequest> findRequestByStatuses(
            @Param("propertyId") Long propertyId,
            @Param("buyerId") Long buyerId,
            @Param("statuses") List<BuyRequestStatus> statuses
    );

    /* ---------------------------------------------------------
       BACKWARD-COMPATIBLE METHOD
       --------------------------------------------------------- */
    default Optional<BuyRequest> findPendingRequest(Long propertyId, Long buyerId) {
        return findRequestByStatuses(
                propertyId,
                buyerId,
                List.of(BuyRequestStatus.PENDING)
        );
    }

    /* =========================================================
       OWNER-APPROVED REQUEST (WORKFLOW CORRECT)
       ========================================================= */

    @Query("""
        SELECT br
        FROM BuyRequest br
        WHERE br.property.id = :propertyId
          AND br.buyer.id = :buyerId
          AND br.status = :status
    """)
    Optional<BuyRequest> findApprovedRequest(
            @Param("propertyId") Long propertyId,
            @Param("buyerId") Long buyerId,
            @Param("status") BuyRequestStatus status
    );

    /* ---------------------------------------------------------
       FIXED: APPROVED → OWNER_APPROVED
       --------------------------------------------------------- */
    default Optional<BuyRequest> findApprovedRequest(Long propertyId, Long buyerId) {
        return findApprovedRequest(
                propertyId,
                buyerId,
                BuyRequestStatus.APPROVED
        );
    }


    @Query("""
SELECT br FROM BuyRequest br
JOIN FETCH br.property p
JOIN FETCH br.buyer b
LEFT JOIN FETCH br.ownerConsents oc
WHERE p.id IN (
    SELECT o.property.id FROM Ownership o
    WHERE o.user.id = :ownerId AND o.current = true
)
""")
    List<BuyRequest> findBuyRequestsForOwnerWithDetails(@Param("ownerId") Long ownerId);


    /* =========================================================
       BUYER DASHBOARD – FULL DETAILS
       (UNCHANGED – ALREADY CORRECT)
       ========================================================= */




    @Query("""
        SELECT DISTINCT br
        FROM BuyRequest br
        JOIN FETCH br.property p
        JOIN FETCH br.buyer b
        LEFT JOIN FETCH br.ownerConsents oc
        LEFT JOIN FETCH oc.owner
        WHERE b.id = :buyerId
    """)
    List<BuyRequest> findByBuyerWithAllDetails(
            @Param("buyerId") Long buyerId
    );

    /* =========================================================
       OWNER DASHBOARD – FULL DETAILS
       (FIXED: ensure current ownership only)
       ========================================================= */


    @Query("""
SELECT br FROM BuyRequest br
JOIN br.ownerConsents oc
WHERE br.buyer.id = :userId OR oc.owner.id = :userId
""")
    List<BuyRequest> findAllByBuyerIdOrOwnerId(@Param("userId") Long userId);

    @Query("""
SELECT br FROM BuyRequest br
JOIN br.property p
JOIN p.ownerships o
WHERE br.buyer.id = :userId
   OR (o.user.id = :userId AND o.current = true)
""")
List<BuyRequest> findByBuyerIdOrOwnerId(@Param("userId") Long userId);

    // BuyRequestRepository
    List<BuyRequest> findByBuyerId(Long buyerId);

    Optional<BuyRequest> findByProperty_IdAndBuyer_IdAndStatus(
            Long propertyId,
            Long buyerId,
            BuyRequestStatus status
    );

    @Query("""
    SELECT br
    FROM BuyRequest br
    JOIN FETCH br.property p
    JOIN FETCH p.ownerships o
    JOIN FETCH o.user
    JOIN FETCH br.buyer b
    WHERE br.id = :id
""")
    Optional<BuyRequest> findByIdWithVisitDetails(@Param("id") Long id);


}
