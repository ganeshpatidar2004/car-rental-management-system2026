package com.carrental.controller;

import com.carrental.dto.BookingResponse;
import com.carrental.dto.CreateBookingRequest;
import com.carrental.dto.PageResponse;
import com.carrental.entity.BookingStatus;
import com.carrental.exception.BookingConflictException;
import com.carrental.exception.BookingNotFoundException;
import com.carrental.exception.CarNotAvailableException;
import com.carrental.exception.InvalidBookingDateException;
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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@Import({SecurityConfig.class, JwtAuthenticationEntryPoint.class, CustomAccessDeniedHandler.class})
class BookingControllerTest {

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

    private BookingResponse createSampleBooking(Long id, Long carId, Long userId, BookingStatus status) {
        return new BookingResponse(
                id,
                userId,
                "John Doe",
                "john@example.com",
                carId,
                "Toyota",
                "Camry",
                "REG-1001",
                new BigDecimal("65.00"),
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(5),
                3,
                new BigDecimal("195.00"),
                status,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("POST /api/bookings - unauthenticated request returns 401 Unauthorized")
    void createBooking_Unauthenticated_ReturnsUnauthorized() throws Exception {
        CreateBookingRequest request = new CreateBookingRequest(
                1L,
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(5)
        );

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "CUSTOMER")
    @DisplayName("POST /api/bookings - authenticated customer successfully creates booking")
    void createBooking_AuthenticatedCustomer_ReturnsCreated() throws Exception {
        CreateBookingRequest request = new CreateBookingRequest(
                1L,
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(5)
        );

        BookingResponse response = createSampleBooking(100L, 1L, 1L, BookingStatus.PENDING);
        when(bookingService.createBooking(eq("john@example.com"), any(CreateBookingRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(100)))
                .andExpect(jsonPath("$.carBrand", is("Toyota")))
                .andExpect(jsonPath("$.totalPrice", is(195.00)))
                .andExpect(jsonPath("$.status", is("PENDING")));
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "CUSTOMER")
    @DisplayName("POST /api/bookings - past date returns 400 Bad Request")
    void createBooking_PastDate_ReturnsBadRequest() throws Exception {
        CreateBookingRequest request = new CreateBookingRequest(
                1L,
                LocalDate.now().minusDays(2),
                LocalDate.now().plusDays(1)
        );

        when(bookingService.createBooking(eq("john@example.com"), any(CreateBookingRequest.class)))
                .thenThrow(new InvalidBookingDateException("Booking startDate cannot be in the past."));

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", containsString("cannot be in the past")));
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "CUSTOMER")
    @DisplayName("POST /api/bookings - overlapping dates returns 409 Conflict")
    void createBooking_OverlapConflict_ReturnsConflict() throws Exception {
        CreateBookingRequest request = new CreateBookingRequest(
                1L,
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(5)
        );

        when(bookingService.createBooking(eq("john@example.com"), any(CreateBookingRequest.class)))
                .thenThrow(new BookingConflictException("The selected car is not available for the requested period."));

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.message", containsString("not available for the requested period")));
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "CUSTOMER")
    @DisplayName("POST /api/bookings - car not available returns 400 Bad Request")
    void createBooking_CarInactive_ReturnsBadRequest() throws Exception {
        CreateBookingRequest request = new CreateBookingRequest(
                1L,
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(5)
        );

        when(bookingService.createBooking(eq("john@example.com"), any(CreateBookingRequest.class)))
                .thenThrow(new CarNotAvailableException("Car with ID 1 is currently not available for rent."));

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", containsString("not available for rent")));
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "CUSTOMER")
    @DisplayName("GET /api/bookings/my - returns paginated bookings of the user")
    void getMyBookings_ReturnsUserBookings() throws Exception {
        BookingResponse booking = createSampleBooking(100L, 1L, 1L, BookingStatus.PENDING);
        PageResponse<BookingResponse> page = new PageResponse<>(
                List.of(booking), 0, 10, 1L, 1, true, true
        );

        when(bookingService.getMyBookings(eq("john@example.com"), anyInt(), anyInt()))
                .thenReturn(page);

        mockMvc.perform(get("/api/bookings/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.content[0].carBrand", is("Toyota")));
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "CUSTOMER")
    @DisplayName("GET /api/bookings/{id} - returns booking details for owner")
    void getBookingById_Owner_ReturnsBooking() throws Exception {
        BookingResponse booking = createSampleBooking(100L, 1L, 1L, BookingStatus.CONFIRMED);
        when(bookingService.getBookingById(eq(100L), eq("john@example.com"), anyBoolean()))
                .thenReturn(booking);

        mockMvc.perform(get("/api/bookings/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(100)))
                .andExpect(jsonPath("$.status", is("CONFIRMED")));
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "CUSTOMER")
    @DisplayName("GET /api/bookings/{id} - returns 404 when booking does not exist")
    void getBookingById_NotFound_Returns404() throws Exception {
        when(bookingService.getBookingById(eq(999L), anyString(), anyBoolean()))
                .thenThrow(new BookingNotFoundException(999L));

        mockMvc.perform(get("/api/bookings/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "CUSTOMER")
    @DisplayName("PATCH /api/bookings/{id}/cancel - cancels own booking successfully")
    void cancelBooking_Success_ReturnsUpdatedBooking() throws Exception {
        BookingResponse cancelled = createSampleBooking(100L, 1L, 1L, BookingStatus.CANCELLED);
        when(bookingService.cancelBooking(eq(100L), eq("john@example.com"), anyBoolean()))
                .thenReturn(cancelled);

        mockMvc.perform(patch("/api/bookings/100/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CANCELLED")));
    }
}
