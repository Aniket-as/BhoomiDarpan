package com.bhoomidarpan.repository;

import com.bhoomidarpan.entity.Ownership;
import com.bhoomidarpan.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OwnershipRepository extends JpaRepository<Ownership, Long> {
    @Query("""
    SELECT o FROM Ownership o
    JOIN FETCH o.user
    WHERE o.property.id = :propertyId
    AND o.current = true
""")
    List<Ownership> findCurrentOwnersWithUser(@Param("propertyId") Long propertyId);
    Optional<Ownership> findByPropertyIdAndUserIdAndCurrentTrue(Long propertyId, Long userId);

    @Query("SELECT o FROM Ownership o WHERE o.property.id = :propertyId AND o.current = true")
    List<Ownership> findCurrentOwners(Long propertyId);

    @Query("SELECT SUM(o.ownershipPercentage) FROM Ownership o WHERE o.property.id = :propertyId AND o.current = true")
    Double getTotalOwnershipPercentage(Long propertyId);

    long countByPropertyId(Long propertyId);

    long countByUserId(Long userId);

    long countByProperty_Id(Long id);

    @Query("""
    SELECT o FROM Ownership o
    WHERE o.property.id = :propertyId
    AND o.current = true
""")
    Optional<Ownership> findCurrentOwnership(@Param("propertyId") Long propertyId);



    Optional<Ownership> findByPropertyIdAndUserId(Long propertyId, Long userId);

    long countByPropertyIdAndCurrentTrue(Long propertyId);

    boolean existsByPropertyIdAndUserIdAndCurrentTrue(Long propertyId, Long userId);

}