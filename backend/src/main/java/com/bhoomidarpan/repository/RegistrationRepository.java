package com.bhoomidarpan.repository;

import com.bhoomidarpan.entity.Registration;
import com.bhoomidarpan.entity.enums.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    List<Registration> findByBuyerId(Long buyerId);
    List<Registration> findByStatus(RegistrationStatus status);
    Optional<Registration> findByPropertyIdAndStatus(Long propertyId, RegistrationStatus status);

    @Query("SELECT COUNT(r) FROM Registration r WHERE DATE(r.appointmentDate) = :date")
    long countByAppointmentDate(LocalDate date);

    @Query("""
SELECT DISTINCT r FROM Registration r
JOIN FETCH r.property p
JOIN FETCH r.buyer b
LEFT JOIN FETCH p.ownerships
WHERE FUNCTION('DATE', r.appointmentDate) = :today
AND r.status = 'APPOINTMENT_SCHEDULED'
""")
    List<Registration> findTodaysAppointments(
            @Param("today") LocalDate today
    );




    @Query("""
SELECT DISTINCT r FROM Registration r
JOIN FETCH r.property p
JOIN FETCH r.buyer
LEFT JOIN FETCH p.ownerships
WHERE r.status = 'APPOINTMENT_SCHEDULED'
""")

    List<Registration> findPendingVerifications();


    @Query("""
SELECT r FROM Registration r
JOIN FETCH r.property p
JOIN FETCH r.buyer b
LEFT JOIN FETCH p.ownerships o
WHERE r.status = 'APPROVED'
AND r.id NOT IN (
    SELECT m.registration.id FROM Mutation m
)
""")
    List<Registration> findApprovedWithoutMutation();

    @Query("""
SELECT r FROM Registration r
JOIN FETCH r.property p
JOIN FETCH r.buyer
LEFT JOIN FETCH p.ownerships
WHERE r.id = :id
""")
    Optional<Registration> findByIdWithDetails(@Param("id") Long id);


}