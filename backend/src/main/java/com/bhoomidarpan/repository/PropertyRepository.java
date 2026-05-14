package com.bhoomidarpan.repository;

import com.bhoomidarpan.entity.Property;
import com.bhoomidarpan.entity.enums.PropertyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    Optional<Property> findByPropertyCode(String propertyCode);
    List<Property> findByStatus(PropertyStatus status);
    List<Property> findByLocationContaining(String location);

    @Query("SELECT p FROM Property p WHERE p.status = 'CLEAR' AND " +
            "(p.propertyCode LIKE %:search% OR p.location LIKE %:search%)")
    List<Property> searchAvailableProperties(String search);

    @Query("""
SELECT p FROM Property p
WHERE p.status = com.bhoomidarpan.entity.enums.PropertyStatus.CLEAR
AND p.availableForSale = true
AND p.id NOT IN (
    SELECT d.property.id FROM Dispute d
    WHERE d.status <> com.bhoomidarpan.entity.enums.DisputeStatus.CLOSED
)
""")
    List<Property> findClearPropertiesWithoutDispute();

    @Query("""
SELECT p FROM Property p
WHERE p.status = com.bhoomidarpan.entity.enums.PropertyStatus.CLEAR
AND p.availableForSale = true
AND LOWER(p.location) LIKE LOWER(CONCAT('%', :area, '%'))
AND p.id NOT IN (
    SELECT d.property.id FROM Dispute d
    WHERE d.status <> com.bhoomidarpan.entity.enums.DisputeStatus.CLOSED
)
""")
    List<Property> findClearPropertiesWithoutDisputeByArea(@Param("area") String area);


    @Query("SELECT p FROM Property p LEFT JOIN FETCH p.documents WHERE p.propertyCode = :code")
    Optional<Property> findByPropertyCodeWithDocuments(@Param("code") String code);



    @Query("SELECT p FROM Property p JOIN p.ownerships o WHERE o.user.id = :userId AND o.current = true")
    List<Property> findPropertiesByOwner(Long userId);

    Optional<Property> findBySurveyNumberAndGatNumber(String surveyNumber, String gatNumber);


    @Query("""
SELECT p FROM Property p
WHERE p.status = com.bhoomidarpan.entity.enums.PropertyStatus.CLEAR
AND p.availableForSale = true
AND p.id NOT IN (
    SELECT o.property.id FROM Ownership o
    WHERE o.user.id = :userId AND o.current = true
)
""")
    List<Property> findClearPropertiesExcludingOwner(@Param("userId") Long userId);


    @Query("""
SELECT p FROM Property p
WHERE p.status = com.bhoomidarpan.entity.enums.PropertyStatus.CLEAR
AND p.availableForSale = true
AND (
    LOWER(p.location) LIKE LOWER(CONCAT('%', :search, '%'))
    OR LOWER(p.propertyCode) LIKE LOWER(CONCAT('%', :search, '%'))
)
AND p.id NOT IN (
    SELECT o.property.id FROM Ownership o
    WHERE o.user.id = :userId AND o.current = true
)
""")
    List<Property> searchClearPropertiesExcludingOwner(
            @Param("search") String search,
            @Param("userId") Long userId);
}