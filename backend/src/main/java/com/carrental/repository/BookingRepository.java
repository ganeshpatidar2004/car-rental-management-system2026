package com.carrental.repository;

import com.carrental.entity.Booking;
import com.carrental.entity.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

/**
 * Repository interface for Booking persistence and overlap detection.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Finds paginated bookings belonging to a specific user identified by email.
     */
    Page<Booking> findByUserEmail(String email, Pageable pageable);

    /**
     * Finds a single booking by ID and user email (enforcing customer ownership).
     */
    Optional<Booking> findByIdAndUserEmail(Long id, String email);

    /**
     * Checks if any active bookings overlap with the requested [startDate, endDate) rental period.
     * The half-open interval rule [startDate, endDate) specifies overlap when:
     * existing.startDate < requested.endDate AND existing.endDate > requested.startDate.
     *
     * @param carId target car ID
     * @param startDate proposed start date
     * @param endDate proposed return date
     * @param activeStatuses collection of active booking statuses (e.g. PENDING, CONFIRMED)
     * @return true if an active booking overlaps, false otherwise
     */
    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.car.id = :carId " +
            "AND b.status IN :activeStatuses " +
            "AND b.startDate < :endDate " +
            "AND b.endDate > :startDate")
    boolean existsOverlappingBookings(
            @Param("carId") Long carId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("activeStatuses") Collection<BookingStatus> activeStatuses);

    /**
     * Finds all bookings filtered by status for administrative views.
     */
    Page<Booking> findByStatus(BookingStatus status, Pageable pageable);
}
