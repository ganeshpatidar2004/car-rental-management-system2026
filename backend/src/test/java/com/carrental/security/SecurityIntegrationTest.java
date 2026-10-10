package com.carrental.security;

import com.carrental.dto.CarRequest;
import com.carrental.entity.Role;
import com.carrental.entity.User;
import com.carrental.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String customerToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User customer = new User("Customer User", "customer@example.com", passwordEncoder.encode("pass123"), Role.CUSTOMER);
        User admin = new User("Admin User", "admin@example.com", passwordEncoder.encode("admin123"), Role.ADMIN);

        userRepository.save(customer);
        userRepository.save(admin);

        customerToken = "Bearer " + jwtService.generateToken("customer@example.com", Role.CUSTOMER);
        adminToken = "Bearer " + jwtService.generateToken("admin@example.com", Role.ADMIN);
    }

    private CarRequest sampleCarRequest(String regNum) {
        return new CarRequest(
                "Tesla",
                "Model 3",
                2024,
                "White",
                regNum,
                new BigDecimal("95.00"),
                true,
                "https://example.com/tesla.jpg"
        );
    }

    @Test
    @DisplayName("GET /api/cars - permits unauthenticated public access to car listings")
    void getCars_Unauthenticated_Permitted() throws Exception {
        mockMvc.perform(get("/api/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("GET /api/cars - permits authenticated CUSTOMER access to car listings")
    void getCars_CustomerToken_Permitted() throws Exception {
        mockMvc.perform(get("/api/cars")
                        .header("Authorization", customerToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/cars - rejects unauthenticated write access with 401 Unauthorized")
    void postCar_Unauthenticated_Returns401() throws Exception {
        CarRequest request = sampleCarRequest("SEC-001");

        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("POST /api/cars - rejects invalid JWT token with 401 Unauthorized")
    void postCar_InvalidToken_Returns401() throws Exception {
        CarRequest request = sampleCarRequest("SEC-002");

        mockMvc.perform(post("/api/cars")
                        .header("Authorization", "Bearer invalid.malformed.jwt.token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("POST /api/cars - forbids CUSTOMER role with 403 Forbidden")
    void postCar_CustomerRole_Returns403() throws Exception {
        CarRequest request = sampleCarRequest("SEC-003");

        mockMvc.perform(post("/api/cars")
                        .header("Authorization", customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("Access denied: insufficient permissions to perform this operation"));
    }

    @Test
    @DisplayName("PUT /api/cars/1 - forbids CUSTOMER role with 403 Forbidden")
    void putCar_CustomerRole_Returns403() throws Exception {
        CarRequest request = sampleCarRequest("SEC-004");

        mockMvc.perform(put("/api/cars/1")
                        .header("Authorization", customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @DisplayName("DELETE /api/cars/1 - forbids CUSTOMER role with 403 Forbidden")
    void deleteCar_CustomerRole_Returns403() throws Exception {
        mockMvc.perform(delete("/api/cars/1")
                        .header("Authorization", customerToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @DisplayName("POST /api/cars - permits ADMIN role with 201 Created")
    void postCar_AdminRole_Permitted() throws Exception {
        CarRequest request = sampleCarRequest("SEC-ADMIN-001");

        mockMvc.perform(post("/api/cars")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.registrationNumber").value("SEC-ADMIN-001"));
    }
}
