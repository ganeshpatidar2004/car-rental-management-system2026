package com.carrental.controller;

import com.carrental.dto.BookingResponse;
import com.carrental.dto.PageResponse;
import com.carrental.dto.UpdateBookingStatusRequest;
import com.carrental.entity.BookingStatus;
import com.carrental.exception.BookingNotFoundException;
import com.carrental.exception.InvalidStatusTransitionException;
import com.carrental.security.CustomAccessDeniedHandler;
import com.carrental.security.CustomUserDetailsService;
import com.carrental.security.JwtAuthenticationEntryPoint;
import com.carrental.security.JwtService;
import com.carrental.security.SecurityConfig;
import com.carrental.service.BookingService;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminBookingController.class)
@Import({SecurityConfig.class, JwtAuthenticationEntryPoint.class, CustomAccessDeniedHandler.class})
class AdminBookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    private BookingResponse createSampleBooking(Long id, BookingStatus status) {
        return new BookingResponse(
                id,
                1L,
                "Customer Bob",
                "bob@example.com",
                1L,
                "Honda",
                "Civic",
                "REG-2002",
                new BigDecimal("50.00"),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(4),
                3,
                new BigDecimal("150.00"),
                status,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("GET /api/admin/bookings - unauthenticated request returns 401 Unauthorized")
    void getAllBookings_Unauthenticated_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/bookings"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    @DisplayName("GET /api/admin/bookings - CUSTOMER role returns 403 Forbidden")
    void getAllBookings_CustomerRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/bookings"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/admin/bookings - ADMIN role returns paginated bookings")
    void getAllBookings_AdminRole_ReturnsPaginatedList() throws Exception {
        BookingResponse booking = createSampleBooking(10L, BookingStatus.PENDING);
        PageResponse<BookingResponse> page = new PageResponse<>(
                List.of(booking), 0, 10, 1L, 1, true, true
        );

        when(bookingService.getAllBookingsAdmin(eq(null), anyInt(), anyInt()))
                .thenReturn(page);

        mockMvc.perform(get("/api/admin/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(10)))
                .andExpect(jsonPath("$.content[0].status", is("PENDING")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PATCH /api/admin/bookings/{id}/status - ADMIN successfully updates status to CONFIRMED")
    void updateBookingStatus_AdminRole_ReturnsUpdated() throws Exception {
        UpdateBookingStatusRequest request = new UpdateBookingStatusRequest(BookingStatus.CONFIRMED);
        BookingResponse confirmed = createSampleBooking(10L, BookingStatus.CONFIRMED);

        when(bookingService.updateBookingStatusAdmin(eq(10L), any(UpdateBookingStatusRequest.class)))
                .thenReturn(confirmed);

        mockMvc.perform(patch("/api/admin/bookings/10/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.status", is("CONFIRMED")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PATCH /api/admin/bookings/{id}/status - invalid transition returns 400 Bad Request")
    void updateBookingStatus_InvalidTransition_ReturnsBadRequest() throws Exception {
        UpdateBookingStatusRequest request = new UpdateBookingStatusRequest(BookingStatus.CONFIRMED);

        when(bookingService.updateBookingStatusAdmin(eq(10L), any(UpdateBookingStatusRequest.class)))
                .thenThrow(new InvalidStatusTransitionException("Cannot transition cancelled booking to CONFIRMED."));

        mockMvc.perform(patch("/api/admin/bookings/10/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }
}
