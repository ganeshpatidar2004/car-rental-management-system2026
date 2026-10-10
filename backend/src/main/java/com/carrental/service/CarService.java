package com.carrental.service;

import com.carrental.dto.CarRequest;
import com.carrental.dto.CarResponse;
import com.carrental.dto.PageResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface defining Car management business operations.
 */
public interface CarService {

    /**
     * Creates a new car in the system.
     *
     * @param request the car creation data
     * @return the created car response
     */
    CarResponse createCar(CarRequest request);

    /**
     * Retrieves all cars without pagination.
     *
     * @return list of car responses
     */
    List<CarResponse> getAllCars();

    /**
     * Searches and filters cars with pagination and sorting.
     *
     * @param keyword search term matching brand or model
     * @param brand filter for vehicle brand
     * @param minPrice minimum pricePerDay
     * @param maxPrice maximum pricePerDay
     * @param available filter for vehicle availability
     * @param page zero-based page index (default 0)
     * @param size page size (default 10, max 50)
     * @param sortBy field to sort by (safe allowed fields only)
     * @param direction sort direction ('asc' or 'desc')
     * @return paginated response containing matching cars
     */
    PageResponse<CarResponse> searchCars(
            String keyword,
            String brand,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean available,
            int page,
            int size,
            String sortBy,
            String direction);

    /**
     * Retrieves a single car by its primary key ID.
     *
     * @param id the car ID
     * @return the car response
     */
    CarResponse getCarById(Long id);

    /**
     * Updates an existing car with new data.
     *
     * @param id the car ID to update
     * @param request the updated car data
     * @return the updated car response
     */
    CarResponse updateCar(Long id, CarRequest request);

    /**
     * Deletes a car from the system by its ID.
     *
     * @param id the car ID to delete
     */
    void deleteCar(Long id);
}
