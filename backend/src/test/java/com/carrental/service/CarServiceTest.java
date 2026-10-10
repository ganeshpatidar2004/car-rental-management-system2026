package com.carrental.service;

import com.carrental.dto.CarRequest;
import com.carrental.dto.CarResponse;
import com.carrental.dto.PageResponse;
import com.carrental.entity.Car;
import com.carrental.exception.CarNotFoundException;
import com.carrental.exception.DuplicateRegistrationNumberException;
import com.carrental.repository.CarRepository;
import com.carrental.service.impl.CarServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarServiceImpl carService;

    private Car sampleCar;
    private CarRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleCar = new Car(
                "Toyota",
                "Camry",
                2023,
                "Silver",
                "REG-1001",
                new BigDecimal("65.00"),
                true,
                "https://example.com/car.jpg"
        );
        sampleCar.setId(1L);
        sampleCar.setCreatedAt(LocalDateTime.now());

        sampleRequest = new CarRequest(
                "Toyota",
                "Camry",
                2023,
                "Silver",
                "REG-1001",
                new BigDecimal("65.00"),
                true,
                "https://example.com/car.jpg"
        );
    }

    @Test
    @DisplayName("createCar - successfully creates and returns CarResponse")
    void createCar_Success() {
        when(carRepository.existsByRegistrationNumber("REG-1001")).thenReturn(false);
        when(carRepository.save(any(Car.class))).thenReturn(sampleCar);

        CarResponse response = carService.createCar(sampleRequest);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getBrand()).isEqualTo("Toyota");
        assertThat(response.getModel()).isEqualTo("Camry");
        assertThat(response.getRegistrationNumber()).isEqualTo("REG-1001");
        assertThat(response.getPricePerDay()).isEqualByComparingTo(new BigDecimal("65.00"));
        assertThat(response.isAvailable()).isTrue();

        verify(carRepository).existsByRegistrationNumber("REG-1001");
        verify(carRepository).save(any(Car.class));
    }

    @Test
    @DisplayName("createCar - throws DuplicateRegistrationNumberException when registration number exists")
    void createCar_DuplicateRegistrationNumber_ThrowsException() {
        when(carRepository.existsByRegistrationNumber("REG-1001")).thenReturn(true);

        assertThatThrownBy(() -> carService.createCar(sampleRequest))
                .isInstanceOf(DuplicateRegistrationNumberException.class)
                .hasMessageContaining("REG-1001");

        verify(carRepository).existsByRegistrationNumber("REG-1001");
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    @DisplayName("searchCars - returns default paginated response")
    void searchCars_DefaultPagination_ReturnsPageResponse() {
        Page<Car> page = new PageImpl<>(List.of(sampleCar), PageRequest.of(0, 10, Sort.by("id")), 1);
        when(carRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        PageResponse<CarResponse> response = carService.searchCars(
                null, null, null, null, null,
                0, 10, "id", "asc"
        );

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getCurrentPage()).isEqualTo(0);
        assertThat(response.getPageSize()).isEqualTo(10);
        assertThat(response.getTotalElements()).isEqualTo(1L);
        assertThat(response.getTotalPages()).isEqualTo(1);
        assertThat(response.isFirst()).isTrue();
        assertThat(response.isLast()).isTrue();
        assertThat(response.getContent().get(0).getBrand()).isEqualTo("Toyota");
    }

    @Test
    @DisplayName("searchCars - supports multiple pages and returns correct slice")
    void searchCars_MultiplePages_ReturnsSecondPage() {
        Car car2 = new Car("Honda", "Civic", 2022, "Blue", "REG-1002", new BigDecimal("55.00"), true, null);
        car2.setId(2L);
        car2.setCreatedAt(LocalDateTime.now());

        Page<Car> page = new PageImpl<>(List.of(car2), PageRequest.of(1, 1, Sort.by("id")), 2);
        when(carRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        PageResponse<CarResponse> response = carService.searchCars(
                null, null, null, null, null,
                1, 1, "id", "asc"
        );

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getCurrentPage()).isEqualTo(1);
        assertThat(response.getPageSize()).isEqualTo(1);
        assertThat(response.getTotalElements()).isEqualTo(2L);
        assertThat(response.getTotalPages()).isEqualTo(2);
        assertThat(response.isFirst()).isFalse();
        assertThat(response.isLast()).isTrue();
    }

    @Test
    @DisplayName("searchCars - combined search keyword and filters execute successfully")
    void searchCars_CombinedFilters_Success() {
        Page<Car> page = new PageImpl<>(List.of(sampleCar), PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "pricePerDay")), 1);
        when(carRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        PageResponse<CarResponse> response = carService.searchCars(
                "Camry", "Toyota", new BigDecimal("50.00"), new BigDecimal("100.00"), true,
                0, 10, "pricePerDay", "desc"
        );

        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getModel()).isEqualTo("Camry");

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(carRepository).findAll(any(Specification.class), pageableCaptor.capture());

        Pageable captured = pageableCaptor.getValue();
        assertThat(captured.getSort().getOrderFor("pricePerDay")).isNotNull();
        assertThat(captured.getSort().getOrderFor("pricePerDay").getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    @DisplayName("searchCars - empty search results return empty page")
    void searchCars_EmptyResults_ReturnsEmptyPage() {
        Page<Car> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(carRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(emptyPage);

        PageResponse<CarResponse> response = carService.searchCars(
                "NonExistent", null, null, null, null,
                0, 10, "id", "asc"
        );

        assertThat(response.getContent()).isEmpty();
        assertThat(response.getTotalElements()).isZero();
        assertThat(response.getTotalPages()).isZero();
    }

    @Test
    @DisplayName("searchCars - rejects negative page index")
    void searchCars_NegativePage_ThrowsException() {
        assertThatThrownBy(() -> carService.searchCars(
                null, null, null, null, null,
                -1, 10, "id", "asc"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Page index must not be less than zero");
    }

    @Test
    @DisplayName("searchCars - rejects invalid page size")
    void searchCars_InvalidPageSize_ThrowsException() {
        assertThatThrownBy(() -> carService.searchCars(
                null, null, null, null, null,
                0, 0, "id", "asc"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Page size must be between 1 and 50");

        assertThatThrownBy(() -> carService.searchCars(
                null, null, null, null, null,
                0, 51, "id", "asc"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Page size must be between 1 and 50");
    }

    @Test
    @DisplayName("searchCars - rejects unsupported sortBy field")
    void searchCars_InvalidSortBy_ThrowsException() {
        assertThatThrownBy(() -> carService.searchCars(
                null, null, null, null, null,
                0, 10, "dangerousField", "asc"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid sortBy field");
    }

    @Test
    @DisplayName("searchCars - rejects invalid direction value")
    void searchCars_InvalidDirection_ThrowsException() {
        assertThatThrownBy(() -> carService.searchCars(
                null, null, null, null, null,
                0, 10, "id", "sideways"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Direction must be 'asc' or 'desc'");
    }

    @Test
    @DisplayName("searchCars - rejects negative minPrice")
    void searchCars_NegativeMinPrice_ThrowsException() {
        assertThatThrownBy(() -> carService.searchCars(
                null, null, new BigDecimal("-10.00"), null, null,
                0, 10, "id", "asc"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("minPrice must not be negative");
    }

    @Test
    @DisplayName("searchCars - rejects negative maxPrice")
    void searchCars_NegativeMaxPrice_ThrowsException() {
        assertThatThrownBy(() -> carService.searchCars(
                null, null, null, new BigDecimal("-5.00"), null,
                0, 10, "id", "asc"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("maxPrice must not be negative");
    }

    @Test
    @DisplayName("searchCars - rejects minPrice greater than maxPrice")
    void searchCars_MinPriceGreaterThanMaxPrice_ThrowsException() {
        assertThatThrownBy(() -> carService.searchCars(
                null, null, new BigDecimal("100.00"), new BigDecimal("50.00"), null,
                0, 10, "id", "asc"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("minPrice cannot be greater than maxPrice");
    }

    @Test
    @DisplayName("getCarById - successfully returns CarResponse when car exists")
    void getCarById_Success() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(sampleCar));

        CarResponse response = carService.getCarById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getModel()).isEqualTo("Camry");
        verify(carRepository).findById(1L);
    }

    @Test
    @DisplayName("getCarById - throws CarNotFoundException when car does not exist")
    void getCarById_NotFound_ThrowsException() {
        when(carRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carService.getCarById(99L))
                .isInstanceOf(CarNotFoundException.class)
                .hasMessageContaining("99");

        verify(carRepository).findById(99L);
    }

    @Test
    @DisplayName("updateCar - successfully updates car attributes and returns CarResponse")
    void updateCar_Success() {
        CarRequest updateRequest = new CarRequest(
                "Toyota",
                "Camry Hybrid",
                2024,
                "Black",
                "REG-1001-NEW",
                new BigDecimal("75.00"),
                false,
                "https://example.com/hybrid.jpg"
        );

        when(carRepository.findById(1L)).thenReturn(Optional.of(sampleCar));
        when(carRepository.existsByRegistrationNumberAndIdNot("REG-1001-NEW", 1L)).thenReturn(false);
        when(carRepository.save(any(Car.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CarResponse response = carService.updateCar(1L, updateRequest);

        assertThat(response).isNotNull();
        assertThat(response.getModel()).isEqualTo("Camry Hybrid");
        assertThat(response.getYear()).isEqualTo(2024);
        assertThat(response.getColor()).isEqualTo("Black");
        assertThat(response.getRegistrationNumber()).isEqualTo("REG-1001-NEW");
        assertThat(response.getPricePerDay()).isEqualByComparingTo(new BigDecimal("75.00"));
        assertThat(response.isAvailable()).isFalse();

        verify(carRepository).findById(1L);
        verify(carRepository).existsByRegistrationNumberAndIdNot("REG-1001-NEW", 1L);
        verify(carRepository).save(sampleCar);
    }

    @Test
    @DisplayName("updateCar - throws DuplicateRegistrationNumberException when another car has registration number")
    void updateCar_DuplicateRegistrationNumber_ThrowsException() {
        CarRequest updateRequest = new CarRequest(
                "Toyota",
                "Camry",
                2023,
                "Silver",
                "REG-DUPLICATE",
                new BigDecimal("65.00"),
                true,
                null
        );

        when(carRepository.findById(1L)).thenReturn(Optional.of(sampleCar));
        when(carRepository.existsByRegistrationNumberAndIdNot("REG-DUPLICATE", 1L)).thenReturn(true);

        assertThatThrownBy(() -> carService.updateCar(1L, updateRequest))
                .isInstanceOf(DuplicateRegistrationNumberException.class)
                .hasMessageContaining("REG-DUPLICATE");

        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    @DisplayName("deleteCar - successfully deletes existing car")
    void deleteCar_Success() {
        when(carRepository.existsById(1L)).thenReturn(true);

        carService.deleteCar(1L);

        verify(carRepository).existsById(1L);
        verify(carRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteCar - throws CarNotFoundException when car does not exist")
    void deleteCar_NotFound_ThrowsException() {
        when(carRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> carService.deleteCar(99L))
                .isInstanceOf(CarNotFoundException.class)
                .hasMessageContaining("99");

        verify(carRepository, never()).deleteById(99L);
    }
}
