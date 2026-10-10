package com.carrental.repository;

import com.carrental.entity.Car;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Car persistence operations.
 * Extends JpaSpecificationExecutor for database-level dynamic filtering and pagination.
 */
@Repository
public interface CarRepository extends JpaRepository<Car, Long>, JpaSpecificationExecutor<Car> {

    /**
     * Checks if a car exists with the given registration number.
     *
     * @param registrationNumber the registration number to check
     * @return true if a car with this registration number exists, false otherwise
     */
    boolean existsByRegistrationNumber(String registrationNumber);

    /**
     * Checks if another car (excluding the specified ID) exists with the given registration number.
     * Useful for uniqueness validation during car updates.
     *
     * @param registrationNumber the registration number to check
     * @param id the car ID to exclude
     * @return true if another car with this registration number exists, false otherwise
     */
    boolean existsByRegistrationNumberAndIdNot(String registrationNumber, Long id);

    /**
     * Finds a car by its registration number.
     *
     * @param registrationNumber the registration number
     * @return optional containing the car if found
     */
    Optional<Car> findByRegistrationNumber(String registrationNumber);

    /**
     * Acquires a pessimistic write lock (SELECT ... FOR UPDATE) on a car record
     * to prevent race conditions during concurrent booking requests.
     *
     * @param id the car ID to lock
     * @return optional containing the locked car entity
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Car c WHERE c.id = :id")
    Optional<Car> findByIdWithPessimisticLock(@Param("id") Long id);
}
