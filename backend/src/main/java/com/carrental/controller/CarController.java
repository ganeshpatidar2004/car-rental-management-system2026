package com.carrental.controller;

import com.carrental.dto.AvailabilityResponse;
import com.carrental.dto.CarRequest;
import com.carrental.dto.CarResponse;
import com.carrental.dto.PageResponse;
import com.carrental.service.BookingService;
import com.carrental.service.CarService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * REST controller exposing endpoints for Car Management CRUD operations, search, filtering, and availability checks.
 */
@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarService carService;
    private final BookingService bookingService;

    public CarController(CarService carService, BookingService bookingService) {
        this.carService = carService;
        this.bookingService = bookingService;
    }

    /**
     * GET /api/cars
     * Retrieves cars with pagination, sorting, search, and dynamic filtering.
     * Requests without query parameters return the default paginated view.
     */
    @GetMapping
    public ResponseEntity<PageResponse<CarResponse>> getAllCars(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean available) {

        PageResponse<CarResponse> response = carService.searchCars(
                keyword, brand, minPrice, maxPrice, available,
                page, size, sortBy, direction
        );
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/cars/{id}
     * Retrieves a single car by its ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CarResponse> getCarById(@PathVariable Long id) {
        CarResponse car = carService.getCarById(id);
        return ResponseEntity.ok(car);
    }

    /**
     * GET /api/cars/{id}/availability?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD
     * Checks if a vehicle is available for the given date range.
     */
    @GetMapping("/{id}/availability")
    public ResponseEntity<AvailabilityResponse> checkCarAvailability(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        AvailabilityResponse response = bookingService.checkCarAvailability(id, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/cars
     * Creates a new car entity (Restricted to ADMIN).
     */
    @PostMapping
    public ResponseEntity<CarResponse> createCar(@Valid @RequestBody CarRequest request) {
        CarResponse createdCar = carService.createCar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCar);
    }

    /**
     * PUT /api/cars/{id}
     * Updates an existing car by its ID (Restricted to ADMIN).
     */
    @PutMapping("/{id}")
    public ResponseEntity<CarResponse> updateCar(
            @PathVariable Long id,
            @Valid @RequestBody CarRequest request) {
        CarResponse updatedCar = carService.updateCar(id, request);
        return ResponseEntity.ok(updatedCar);
    }

    /**
     * DELETE /api/cars/{id}
     * Deletes a car by its ID (Restricted to ADMIN).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }
}
