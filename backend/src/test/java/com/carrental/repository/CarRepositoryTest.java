package com.carrental.repository;

import com.carrental.entity.Car;
import com.carrental.specification.CarSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CarRepositoryTest {

    @Autowired
    private CarRepository carRepository;

    private Car car1;
    private Car car2;
    private Car car3;

    @BeforeEach
    void setUp() {
        carRepository.deleteAll();

        car1 = new Car("Honda", "Accord", 2023, "White", "REG-H100", new BigDecimal("70.00"), true, null);
        car2 = new Car("Toyota", "Camry", 2022, "Silver", "REG-T200", new BigDecimal("60.00"), true, null);
        car3 = new Car("BMW", "3 Series", 2024, "Black", "REG-B300", new BigDecimal("120.00"), false, null);

        carRepository.save(car1);
        carRepository.save(car2);
        carRepository.save(car3);
    }

    @Test
    @DisplayName("existsByRegistrationNumber - returns true when registration number exists")
    void existsByRegistrationNumber_ExistingNumber_ReturnsTrue() {
        boolean exists = carRepository.existsByRegistrationNumber("REG-H100");
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByRegistrationNumber - returns false when registration number does not exist")
    void existsByRegistrationNumber_NonExistingNumber_ReturnsFalse() {
        boolean exists = carRepository.existsByRegistrationNumber("NON-EXISTENT");
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("existsByRegistrationNumberAndIdNot - returns false when checking own ID")
    void existsByRegistrationNumberAndIdNot_SameCarId_ReturnsFalse() {
        boolean exists = carRepository.existsByRegistrationNumberAndIdNot("REG-H100", car1.getId());
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("existsByRegistrationNumberAndIdNot - returns true when another car has registration number")
    void existsByRegistrationNumberAndIdNot_DifferentCarId_ReturnsTrue() {
        boolean exists = carRepository.existsByRegistrationNumberAndIdNot("REG-H100", car2.getId());
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("findByRegistrationNumber - returns car when exists")
    void findByRegistrationNumber_Success() {
        Optional<Car> found = carRepository.findByRegistrationNumber("REG-H100");
        assertThat(found).isPresent();
        assertThat(found.get().getBrand()).isEqualTo("Honda");
        assertThat(found.get().getModel()).isEqualTo("Accord");
    }

    @Test
    @DisplayName("Specification - searches by keyword matching brand or model")
    void specification_KeywordSearch_MatchesBrandOrModel() {
        Specification<Car> spec = CarSpecification.filter("camry", null, null, null, null);
        Page<Car> page = carRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(1L);
        assertThat(page.getContent().get(0).getModel()).isEqualTo("Camry");
    }

    @Test
    @DisplayName("Specification - filters by brand and availability")
    void specification_BrandAndAvailability_FiltersCorrectly() {
        Specification<Car> spec = CarSpecification.filter(null, "BMW", null, null, false);
        Page<Car> page = carRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(1L);
        assertThat(page.getContent().get(0).getBrand()).isEqualTo("BMW");
        assertThat(page.getContent().get(0).isAvailable()).isFalse();
    }

    @Test
    @DisplayName("Specification - filters by price range with pagination and sorting")
    void specification_PriceRangeAndSorting_AppliesCorrectly() {
        Specification<Car> spec = CarSpecification.filter(null, null, new BigDecimal("60.00"), new BigDecimal("100.00"), true);
        Page<Car> page = carRepository.findAll(spec, PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "pricePerDay")));

        assertThat(page.getTotalElements()).isEqualTo(2L);
        assertThat(page.getContent().get(0).getPricePerDay()).isEqualByComparingTo(new BigDecimal("70.00"));
        assertThat(page.getContent().get(1).getPricePerDay()).isEqualByComparingTo(new BigDecimal("60.00"));
    }

    @Test
    @DisplayName("Specification - handles multiple pages with correct page boundaries")
    void specification_Pagination_NavigatesAcrossPages() {
        Specification<Car> spec = CarSpecification.filter(null, null, null, null, null);
        Page<Car> page0 = carRepository.findAll(spec, PageRequest.of(0, 2, Sort.by("id")));
        Page<Car> page1 = carRepository.findAll(spec, PageRequest.of(1, 2, Sort.by("id")));

        assertThat(page0.getTotalElements()).isEqualTo(3L);
        assertThat(page0.getNumberOfElements()).isEqualTo(2);
        assertThat(page0.isFirst()).isTrue();
        assertThat(page0.isLast()).isFalse();

        assertThat(page1.getNumberOfElements()).isEqualTo(1);
        assertThat(page1.isFirst()).isFalse();
        assertThat(page1.isLast()).isTrue();
    }
}
