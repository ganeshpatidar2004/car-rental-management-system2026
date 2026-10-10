package com.carrental.service;

import com.carrental.dto.AvailabilityResponse;
import com.carrental.dto.BookingResponse;
import com.carrental.dto.CreateBookingRequest;
import com.carrental.dto.PageResponse;
import com.carrental.dto.UpdateBookingStatusRequest;
import com.carrental.entity.Booking;
import com.carrental.entity.BookingStatus;
import com.carrental.entity.Car;
import com.carrental.entity.Role;
import com.carrental.entity.User;
import com.carrental.exception.BookingConflictException;
import com.carrental.exception.BookingNotFoundException;
import com.carrental.exception.CarNotAvailableException;
import com.carrental.exception.CarNotFoundException;
import com.carrental.exception.InvalidBookingDateException;
import com.carrental.exception.InvalidStatusTransitionException;
import com.carrental.exception.UnauthorizedBookingAccessException;
import com.carrental.repository.BookingRepository;
import com.carrental.repository.CarRepository;
import com.carrental.repository.UserRepository;
import com.carrental.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User sampleUser;
    private Car sampleCar;
    private Booking sampleBooking;

    @BeforeEach
    void setUp() {
        sampleUser = new User("John Doe", "john@example.com", "hash", Role.CUSTOMER);
        sampleUser.setId(10L);

        sampleCar = new Car("BMW", "3 Series", 2023, "Blue", "REG-BMW-1", new BigDecimal("100.00"), true, null);
        sampleCar.setId(20L);

        sampleBooking = new Booking(
                sampleUser,
                sampleCar,
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(8),
                new BigDecimal("300.00"),
                BookingStatus.PENDING
        );
        sampleBooking.setId(100L);
        sampleBooking.setCreatedAt(LocalDateTime.now());
        sampleBooking.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("createBooking - successfully calculates price [startDate, endDate) and returns BookingResponse")
    void createBooking_Success() {
        LocalDate start = LocalDate.now().plusDays(2);
        LocalDate end = LocalDate.now().plusDays(6); // 4 rental days
        CreateBookingRequest request = new CreateBookingRequest(20L, start, end);

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(sampleUser));
        when(carRepository.findByIdWithPessimisticLock(20L)).thenReturn(Optional.of(sampleCar));
        when(bookingRepository.existsOverlappingBookings(eq(20L), eq(start), eq(end), any())).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking b = invocation.getArgument(0);
            b.setId(101L);
            b.setCreatedAt(LocalDateTime.now());
            b.setUpdatedAt(LocalDateTime.now());
            return b;
        });

        BookingResponse response = bookingService.createBooking("john@example.com", request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(101L);
        assertThat(response.getCarId()).isEqualTo(20L);
        assertThat(response.getUserId()).isEqualTo(10L);
        assertThat(response.getRentalDays()).isEqualTo(4L);
        // 4 days * 100.00 per day = 400.00
        assertThat(response.getTotalPrice()).isEqualByComparingTo(new BigDecimal("400.00"));
        assertThat(response.getStatus()).isEqualTo(BookingStatus.PENDING);

        verify(carRepository).findByIdWithPessimisticLock(20L);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    @DisplayName("createBooking - rejects start date in the past")
    void createBooking_PastStartDate_ThrowsException() {
        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now().plusDays(2);
        CreateBookingRequest request = new CreateBookingRequest(20L, start, end);

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(sampleUser));

        assertThatThrownBy(() -> bookingService.createBooking("john@example.com", request))
                .isInstanceOf(InvalidBookingDateException.class)
                .hasMessageContaining("past");
    }

    @Test
    @DisplayName("createBooking - rejects end date on or before start date")
    void createBooking_EndDateBeforeStartDate_ThrowsException() {
        LocalDate start = LocalDate.now().plusDays(5);
        LocalDate end = LocalDate.now().plusDays(5); // 0 rental days
        CreateBookingRequest request = new CreateBookingRequest(20L, start, end);

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(sampleUser));

        assertThatThrownBy(() -> bookingService.createBooking("john@example.com", request))
                .isInstanceOf(InvalidBookingDateException.class)
                .hasMessageContaining("after");
    }

    @Test
    @DisplayName("createBooking - rejects booking when car is marked unavailable")
    void createBooking_CarNotAvailable_ThrowsException() {
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = LocalDate.now().plusDays(3);
        CreateBookingRequest request = new CreateBookingRequest(20L, start, end);

        sampleCar.setAvailable(false);
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(sampleUser));
        when(carRepository.findByIdWithPessimisticLock(20L)).thenReturn(Optional.of(sampleCar));

        assertThatThrownBy(() -> bookingService.createBooking("john@example.com", request))
                .isInstanceOf(CarNotAvailableException.class)
                .hasMessageContaining("not available");
    }

    @Test
    @DisplayName("createBooking - rejects booking when overlapping active booking exists")
    void createBooking_OverlappingActiveBooking_ThrowsConflict() {
        LocalDate start = LocalDate.now().plusDays(2);
        LocalDate end = LocalDate.now().plusDays(5);
        CreateBookingRequest request = new CreateBookingRequest(20L, start, end);

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(sampleUser));
        when(carRepository.findByIdWithPessimisticLock(20L)).thenReturn(Optional.of(sampleCar));
        when(bookingRepository.existsOverlappingBookings(eq(20L), eq(start), eq(end), any())).thenReturn(true);

        assertThatThrownBy(() -> bookingService.createBooking("john@example.com", request))
                .isInstanceOf(BookingConflictException.class)
                .hasMessageContaining("already booked");
    }

    @Test
    @DisplayName("getMyBookings - returns paginated list of caller's bookings")
    void getMyBookings_ReturnsUserBookings() {
        Page<Booking> page = new PageImpl<>(List.of(sampleBooking), PageRequest.of(0, 10), 1);
        when(bookingRepository.findByUserEmail(eq("john@example.com"), any(Pageable.class))).thenReturn(page);

        PageResponse<BookingResponse> response = bookingService.getMyBookings("john@example.com", 0, 10);

        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getUserEmail()).isEqualTo("john@example.com");
    }

    @Test
    @DisplayName("getBookingById - owner can view booking")
    void getBookingById_Owner_Success() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(sampleBooking));

        BookingResponse response = bookingService.getBookingById(100L, "john@example.com", false);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("getBookingById - non-owner non-admin is forbidden")
    void getBookingById_NonOwner_ThrowsUnauthorized() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(sampleBooking));

        assertThatThrownBy(() -> bookingService.getBookingById(100L, "stranger@example.com", false))
                .isInstanceOf(UnauthorizedBookingAccessException.class);
    }

    @Test
    @DisplayName("getBookingById - admin can view any booking")
    void getBookingById_Admin_Success() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(sampleBooking));

        BookingResponse response = bookingService.getBookingById(100L, "admin@example.com", true);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("cancelBooking - owner can cancel eligible booking")
    void cancelBooking_Owner_Success() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(sampleBooking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));

        BookingResponse response = bookingService.cancelBooking(100L, "john@example.com", false);

        assertThat(response.getStatus()).isEqualTo(BookingStatus.CANCELLED);
    }

    @Test
    @DisplayName("cancelBooking - non-owner cannot cancel booking")
    void cancelBooking_NonOwner_ThrowsUnauthorized() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(sampleBooking));

        assertThatThrownBy(() -> bookingService.cancelBooking(100L, "stranger@example.com", false))
                .isInstanceOf(UnauthorizedBookingAccessException.class);
    }

    @Test
    @DisplayName("cancelBooking - cannot cancel already cancelled booking")
    void cancelBooking_AlreadyCancelled_ThrowsException() {
        sampleBooking.setStatus(BookingStatus.CANCELLED);
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(sampleBooking));

        assertThatThrownBy(() -> bookingService.cancelBooking(100L, "john@example.com", false))
                .isInstanceOf(InvalidStatusTransitionException.class)
                .hasMessageContaining("already cancelled");
    }

    @Test
    @DisplayName("updateBookingStatusAdmin - admin can confirm booking")
    void updateBookingStatusAdmin_Confirm_Success() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(sampleBooking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));

        BookingResponse response = bookingService.updateBookingStatusAdmin(
                100L, new UpdateBookingStatusRequest(BookingStatus.CONFIRMED)
        );

        assertThat(response.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
    }

    @Test
    @DisplayName("updateBookingStatusAdmin - cannot modify completed booking")
    void updateBookingStatusAdmin_CompletedBooking_ThrowsException() {
        sampleBooking.setStatus(BookingStatus.COMPLETED);
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(sampleBooking));

        assertThatThrownBy(() -> bookingService.updateBookingStatusAdmin(
                100L, new UpdateBookingStatusRequest(BookingStatus.CONFIRMED)))
                .isInstanceOf(InvalidStatusTransitionException.class)
                .hasMessageContaining("completed");
    }

    @Test
    @DisplayName("checkCarAvailability - returns true and estimated price when available")
    void checkCarAvailability_Available_ReturnsTrue() {
        LocalDate start = LocalDate.now().plusDays(2);
        LocalDate end = LocalDate.now().plusDays(5); // 3 days

        when(carRepository.findById(20L)).thenReturn(Optional.of(sampleCar));
        when(bookingRepository.existsOverlappingBookings(eq(20L), eq(start), eq(end), any())).thenReturn(false);

        AvailabilityResponse response = bookingService.checkCarAvailability(20L, start, end);

        assertThat(response.isAvailable()).isTrue();
        assertThat(response.getRentalDays()).isEqualTo(3L);
        assertThat(response.getEstimatedTotalPrice()).isEqualByComparingTo(new BigDecimal("300.00"));
    }

    @Test
    @DisplayName("checkCarAvailability - returns false when overlapping booking exists")
    void checkCarAvailability_Overlap_ReturnsFalse() {
        LocalDate start = LocalDate.now().plusDays(2);
        LocalDate end = LocalDate.now().plusDays(5);

        when(carRepository.findById(20L)).thenReturn(Optional.of(sampleCar));
        when(bookingRepository.existsOverlappingBookings(eq(20L), eq(start), eq(end), any())).thenReturn(true);

        AvailabilityResponse response = bookingService.checkCarAvailability(20L, start, end);

        assertThat(response.isAvailable()).isFalse();
        assertThat(response.getMessage()).contains("already booked");
    }
}
