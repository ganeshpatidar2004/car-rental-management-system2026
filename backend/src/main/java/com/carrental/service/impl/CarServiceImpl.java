package com.carrental.service.impl;

import com.carrental.dto.CarRequest;
import com.carrental.dto.CarResponse;
import com.carrental.dto.PageResponse;
import com.carrental.entity.Car;
import com.carrental.exception.CarNotFoundException;
import com.carrental.exception.DuplicateRegistrationNumberException;
import com.carrental.repository.CarRepository;
import com.carrental.service.CarService;
import com.carrental.specification.CarSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * Implementation of CarService managing business logic, search, filtering, and validation for Cars.
 */
@Service
@Transactional
public class CarServiceImpl implements CarService {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "brand", "model", "year", "color",
            "registrationNumber", "pricePerDay", "available", "createdAt"
    );

    private final CarRepository carRepository;

    public CarServiceImpl(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    public CarResponse createCar(CarRequest request) {
        String regNum = request.getRegistrationNumber() != null ? request.getRegistrationNumber().trim() : null;

        if (regNum != null && carRepository.existsByRegistrationNumber(regNum)) {
            throw DuplicateRegistrationNumberException.forRegistrationNumber(regNum);
        }

        Car car = new Car();
        car.setBrand(request.getBrand() != null ? request.getBrand().trim() : null);
        car.setModel(request.getModel() != null ? request.getModel().trim() : null);
        car.setYear(request.getYear());
        car.setColor(request.getColor() != null ? request.getColor().trim() : null);
        car.setRegistrationNumber(regNum);
        car.setPricePerDay(request.getPricePerDay());
        car.setAvailable(request.getAvailable() == null || request.getAvailable());
        car.setImageUrl(request.getImageUrl() != null ? request.getImageUrl().trim() : null);

        Car savedCar = carRepository.save(car);
        return CarResponse.fromEntity(savedCar);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarResponse> getAllCars() {
        return carRepository.findAll()
                .stream()
                .map(CarResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CarResponse> searchCars(
            String keyword,
            String brand,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean available,
            int page,
            int size,
            String sortBy,
            String direction) {

        // Validate pagination arguments
        if (page < 0) {
            throw new IllegalArgumentException("Page index must not be less than zero");
        }
        if (size < 1 || size > 50) {
            throw new IllegalArgumentException("Page size must be between 1 and 50");
        }

        // Validate sorting field
        String cleanSortBy = (sortBy == null || sortBy.trim().isEmpty()) ? "id" : sortBy.trim();
        if (!ALLOWED_SORT_FIELDS.contains(cleanSortBy)) {
            throw new IllegalArgumentException("Invalid sortBy field '" + sortBy + "'. Allowed fields are: " + ALLOWED_SORT_FIELDS);
        }

        // Validate sort direction
        String cleanDirection = (direction == null || direction.trim().isEmpty()) ? "asc" : direction.trim().toLowerCase();
        if (!cleanDirection.equals("asc") && !cleanDirection.equals("desc")) {
            throw new IllegalArgumentException("Invalid direction '" + direction + "'. Direction must be 'asc' or 'desc'");
        }

        // Validate price range filter
        if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("minPrice must not be negative");
        }
        if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("maxPrice must not be negative");
        }
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("minPrice cannot be greater than maxPrice");
        }

        Sort.Direction sortDirection = cleanDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(sortDirection, cleanSortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Car> spec = CarSpecification.filter(keyword, brand, minPrice, maxPrice, available);
        Page<Car> carPage = carRepository.findAll(spec, pageable);

        Page<CarResponse> responsePage = carPage.map(CarResponse::fromEntity);
        return PageResponse.fromPage(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public CarResponse getCarById(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new CarNotFoundException(id));
        return CarResponse.fromEntity(car);
    }

    @Override
    public CarResponse updateCar(Long id, CarRequest request) {
        Car existingCar = carRepository.findById(id)
                .orElseThrow(() -> new CarNotFoundException(id));

        String regNum = request.getRegistrationNumber() != null ? request.getRegistrationNumber().trim() : null;

        if (regNum != null && carRepository.existsByRegistrationNumberAndIdNot(regNum, id)) {
            throw DuplicateRegistrationNumberException.forRegistrationNumber(regNum);
        }

        existingCar.setBrand(request.getBrand() != null ? request.getBrand().trim() : null);
        existingCar.setModel(request.getModel() != null ? request.getModel().trim() : null);
        existingCar.setYear(request.getYear());
        existingCar.setColor(request.getColor() != null ? request.getColor().trim() : null);
        existingCar.setRegistrationNumber(regNum);
        existingCar.setPricePerDay(request.getPricePerDay());
        if (request.getAvailable() != null) {
            existingCar.setAvailable(request.getAvailable());
        }
        existingCar.setImageUrl(request.getImageUrl() != null ? request.getImageUrl().trim() : null);

        Car updatedCar = carRepository.save(existingCar);
        return CarResponse.fromEntity(updatedCar);
    }

    @Override
    public void deleteCar(Long id) {
        if (!carRepository.existsById(id)) {
            throw new CarNotFoundException(id);
        }
        carRepository.deleteById(id);
    }
}
