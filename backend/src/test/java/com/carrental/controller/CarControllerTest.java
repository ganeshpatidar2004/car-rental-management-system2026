package com.carrental.controller;

import com.carrental.dto.AvailabilityResponse;
import com.carrental.dto.CarRequest;
import com.carrental.dto.CarResponse;
import com.carrental.dto.PageResponse;
import com.carrental.exception.CarNotFoundException;
import com.carrental.exception.DuplicateRegistrationNumberException;
import com.carrental.security.CustomAccessDeniedHandler;
import com.carrental.security.CustomUserDetailsService;
import com.carrental.security.JwtAuthenticationEntryPoint;
import com.carrental.security.JwtService;
import com.carrental.security.SecurityConfig;
import com.carrental.service.BookingService;
import com.carrental.service.CarService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CarController.class)
@Import({SecurityConfig.class, JwtAuthenticationEntryPoint.class, CustomAccessDeniedHandler.class})
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CarService carService;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    private CarResponse createSampleResponse(Long id, String regNum) {
        return new CarResponse(
                id,
                "Toyota",
                "Camry",
                2023,
                "Silver",
                regNum,
                new BigDecimal("65.00"),
                true,
                "https://example.com/car.jpg",
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("GET /api/cars - default request returns paginated response")
    void getAllCars_DefaultParameters_ReturnsPageResponse() throws Exception {
        List<CarResponse> items = List.of(
                createSampleResponse(1L, "REG-1001"),
                createSampleResponse(2L, "REG-1002")
        );
        PageResponse<CarResponse> pageResponse = new PageResponse<>(
                items, 0, 10, 2L, 1, true, true
        );

        when(carService.searchCars(null, null, null, null, null, 0, 10, "id", "asc"))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.currentPage", is(0)))
                .andExpect(jsonPath("$.pageSize", is(10)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.first", is(true)))
                .andExpect(jsonPath("$.last", is(true)))
                .andExpect(jsonPath("$.content[0].brand", is("Toyota")))
                .andExpect(jsonPath("$.content[0].model", is("Camry")));
    }

    @Test
    @DisplayName("GET /api/cars - handles query parameters for search, filtering, and pagination")
    void getAllCars_WithFiltersAndPagination_ReturnsMatchingPage() throws Exception {
        List<CarResponse> items = List.of(createSampleResponse(1L, "REG-1001"));
        PageResponse<CarResponse> pageResponse = new PageResponse<>(
                items, 1, 5, 6L, 2, false, true
        );

        when(carService.searchCars(
                eq("Camry"), eq("Toyota"), eq(new BigDecimal("50.00")), eq(new BigDecimal("100.00")), eq(true),
                eq(1), eq(5), eq("pricePerDay"), eq("desc")
        )).thenReturn(pageResponse);

        mockMvc.perform(get("/api/cars")
                        .param("page", "1")
                        .param("size", "5")
                        .param("sortBy", "pricePerDay")
                        .param("direction", "desc")
                        .param("keyword", "Camry")
                        .param("brand", "Toyota")
                        .param("minPrice", "50.00")
                        .param("maxPrice", "100.00")
                        .param("available", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.currentPage", is(1)))
                .andExpect(jsonPath("$.pageSize", is(5)))
                .andExpect(jsonPath("$.totalElements", is(6)))
                .andExpect(jsonPath("$.totalPages", is(2)))
                .andExpect(jsonPath("$.first", is(false)))
                .andExpect(jsonPath("$.last", is(true)));
    }

    @Test
    @DisplayName("GET /api/cars - returns 400 Bad Request when price range is invalid")
    void getAllCars_InvalidPriceRange_ReturnsBadRequest() throws Exception {
        when(carService.searchCars(
                any(), any(), eq(new BigDecimal("100.00")), eq(new BigDecimal("50.00")), any(),
                anyInt(), anyInt(), anyString(), anyString()
        )).thenThrow(new IllegalArgumentException("minPrice cannot be greater than maxPrice"));

        mockMvc.perform(get("/api/cars")
                        .param("minPrice", "100.00")
                        .param("maxPrice", "50.00"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", containsString("minPrice cannot be greater than maxPrice")));
    }

    @Test
    @DisplayName("GET /api/cars - returns 400 Bad Request when page size exceeds limit")
    void getAllCars_InvalidPageSize_ReturnsBadRequest() throws Exception {
        when(carService.searchCars(
                any(), any(), any(), any(), any(),
                anyInt(), eq(100), anyString(), anyString()
        )).thenThrow(new IllegalArgumentException("Page size must be between 1 and 50"));

        mockMvc.perform(get("/api/cars")
                        .param("size", "100"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", containsString("Page size must be between 1 and 50")));
    }

    @Test
    @DisplayName("GET /api/cars/{id} - returns 200 OK when car exists")
    void getCarById_Success_ReturnsOk() throws Exception {
        CarResponse response = createSampleResponse(1L, "REG-1001");
        when(carService.getCarById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/cars/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.model", is("Camry")));
    }

    @Test
    @DisplayName("GET /api/cars/{id} - returns 404 Not Found when car does not exist")
    void getCarById_NotFound_ReturnsNotFound() throws Exception {
        when(carService.getCarById(99L)).thenThrow(new CarNotFoundException(99L));

        mockMvc.perform(get("/api/cars/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", containsString("99")));
    }

    @Test
    @DisplayName("GET /api/cars/{id}/availability - returns availability check result")
    void checkAvailability_ReturnsOk() throws Exception {
        AvailabilityResponse response = new AvailabilityResponse(
                1L, "Toyota", "Camry",
                LocalDate.now().plusDays(2), LocalDate.now().plusDays(5),
                true, 3, new BigDecimal("65.00"), new BigDecimal("195.00"),
                "Vehicle is available for the requested dates"
        );

        when(bookingService.checkCarAvailability(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(response);

        mockMvc.perform(get("/api/cars/1/availability")
                        .param("startDate", LocalDate.now().plusDays(2).toString())
                        .param("endDate", LocalDate.now().plusDays(5).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.carId", is(1)))
                .andExpect(jsonPath("$.available", is(true)))
                .andExpect(jsonPath("$.rentalDays", is(3)))
                .andExpect(jsonPath("$.estimatedTotalPrice", is(195.00)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/cars - successfully creates car and returns 201 Created for ADMIN")
    void createCar_ValidRequest_ReturnsCreated() throws Exception {
        CarRequest request = new CarRequest(
                "Toyota",
                "Camry",
                2023,
                "Silver",
                "REG-1001",
                new BigDecimal("65.00"),
                true,
                "https://example.com/car.jpg"
        );

        CarResponse response = createSampleResponse(1L, "REG-1001");
        when(carService.createCar(any(CarRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.brand", is("Toyota")))
                .andExpect(jsonPath("$.model", is("Camry")))
                .andExpect(jsonPath("$.registrationNumber", is("REG-1001")))
                .andExpect(jsonPath("$.pricePerDay", is(65.00)))
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/cars - returns 400 Bad Request when required fields are missing")
    void createCar_MissingRequiredFields_ReturnsBadRequest() throws Exception {
        CarRequest invalidRequest = new CarRequest();
        invalidRequest.setColor("Red");

        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.validationErrors.brand").exists())
                .andExpect(jsonPath("$.validationErrors.model").exists())
                .andExpect(jsonPath("$.validationErrors.year").exists())
                .andExpect(jsonPath("$.validationErrors.registrationNumber").exists())
                .andExpect(jsonPath("$.validationErrors.pricePerDay").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/cars - returns 400 Bad Request when year is invalid")
    void createCar_InvalidYear_ReturnsBadRequest() throws Exception {
        CarRequest invalidYearRequest = new CarRequest(
                "Ford",
                "Mustang",
                1800, // Invalid: year before 1886
                "Red",
                "REG-OLD",
                new BigDecimal("100.00"),
                true,
                null
        );

        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidYearRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.validationErrors.year", containsString("1886")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/cars - returns 400 Bad Request when pricePerDay is zero or negative")
    void createCar_InvalidPrice_ReturnsBadRequest() throws Exception {
        CarRequest invalidPriceRequest = new CarRequest(
                "Honda",
                "Civic",
                2022,
                "Blue",
                "REG-FREE",
                new BigDecimal("0.00"), // Invalid: price <= 0
                true,
                null
        );

        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPriceRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.validationErrors.pricePerDay", containsString("greater than zero")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/cars - returns 409 Conflict on duplicate registration number")
    void createCar_DuplicateRegistrationNumber_ReturnsConflict() throws Exception {
        CarRequest duplicateRequest = new CarRequest(
                "Toyota",
                "Camry",
                2023,
                "Silver",
                "REG-EXISTS",
                new BigDecimal("65.00"),
                true,
                null
        );

        when(carService.createCar(any(CarRequest.class)))
                .thenThrow(DuplicateRegistrationNumberException.forRegistrationNumber("REG-EXISTS"));

        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.message", containsString("REG-EXISTS")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /api/cars/{id} - returns 200 OK when car update succeeds for ADMIN")
    void updateCar_Success_ReturnsOk() throws Exception {
        CarRequest updateRequest = new CarRequest(
                "Toyota",
                "Camry Hybrid",
                2024,
                "Black",
                "REG-1001",
                new BigDecimal("75.00"),
                false,
                null
        );

        CarResponse updatedResponse = new CarResponse(
                1L,
                "Toyota",
                "Camry Hybrid",
                2024,
                "Black",
                "REG-1001",
                new BigDecimal("75.00"),
                false,
                null,
                LocalDateTime.now()
        );

        when(carService.updateCar(eq(1L), any(CarRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/cars/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.model", is("Camry Hybrid")))
                .andExpect(jsonPath("$.year", is(2024)))
                .andExpect(jsonPath("$.available", is(false)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/cars/{id} - returns 204 No Content when delete succeeds for ADMIN")
    void deleteCar_Success_ReturnsNoContent() throws Exception {
        doNothing().when(carService).deleteCar(1L);

        mockMvc.perform(delete("/api/cars/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/cars/{id} - returns 404 Not Found when car does not exist")
    void deleteCar_NotFound_ReturnsNotFound() throws Exception {
        doThrow(new CarNotFoundException(99L)).when(carService).deleteCar(99L);

        mockMvc.perform(delete("/api/cars/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")));
    }
}
