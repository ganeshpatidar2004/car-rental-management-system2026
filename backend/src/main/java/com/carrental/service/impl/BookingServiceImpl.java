package com.carrental.service.impl;

import com.carrental.dto.AvailabilityResponse;
import com.carrental.dto.BookingResponse;
import com.carrental.dto.CreateBookingRequest;
import com.carrental.dto.PageResponse;
import com.carrental.dto.UpdateBookingStatusRequest;
import com.carrental.entity.Booking;
import com.carrental.entity.BookingStatus;
import com.carrental.entity.Car;
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
import com.carrental.service.BookingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Implementation of BookingService managing vehicle reservations, availability,
 * concurrency controls, and lifecycle transitions.
 */
@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private static final List<BookingStatus> ACTIVE_STATUSES = List.of(
            BookingStatus.PENDING,
            BookingStatus.CONFIRMED
    );

    private final BookingRepository bookingRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;

    public BookingServiceImpl(
            BookingRepository bookingRepository,
            CarRepository carRepository,
            UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.carRepository = carRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BookingResponse createBooking(String userEmail, CreateBookingRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

        validateBookingDates(request.getStartDate(), request.getEndDate());

        // Concurrency safeguard: acquire a pessimistic write lock (SELECT ... FOR UPDATE) on the Car record.
        // This serializes concurrent booking requests for the same vehicle at the database row level,
        // eliminating race conditions and double bookings.
        Car car = carRepository.findByIdWithPessimisticLock(request.getCarId())
                .orElseThrow(() -> new CarNotFoundException(request.getCarId()));

        if (!car.isAvailable()) {
            throw new CarNotAvailableException(car.getId());
        }

        // Check for overlapping active bookings within [startDate, endDate)
        boolean hasOverlap = bookingRepository.existsOverlappingBookings(
                car.getId(),
                request.getStartDate(),
                request.getEndDate(),
                ACTIVE_STATUSES
        );

        if (hasOverlap) {
            throw new BookingConflictException("The selected car is already booked for the requested dates");
        }

        // Calculate rental days: return date is excluded as per [startDate, endDate) rule
        long rentalDays = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
        BigDecimal totalPrice = car.getPricePerDay().multiply(BigDecimal.valueOf(rentalDays));

        Booking booking = new Booking(
                user,
                car,
                request.getStartDate(),
                request.getEndDate(),
                totalPrice,
                BookingStatus.PENDING
        );

        Booking savedBooking = bookingRepository.save(booking);
        return BookingResponse.fromEntity(savedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookingResponse> getMyBookings(String userEmail, int page, int size) {
        validatePagination(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Booking> bookingPage = bookingRepository.findByUserEmail(userEmail, pageable);

        return PageResponse.fromPage(bookingPage.map(BookingResponse::fromEntity));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long id, String userEmail, boolean isAdmin) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));

        if (!isAdmin && !booking.getUser().getEmail().equalsIgnoreCase(userEmail)) {
            throw new UnauthorizedBookingAccessException();
        }

        return BookingResponse.fromEntity(booking);
    }

    @Override
    public BookingResponse cancelBooking(Long id, String userEmail, boolean isAdmin) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));

        if (!isAdmin && !booking.getUser().getEmail().equalsIgnoreCase(userEmail)) {
            throw new UnauthorizedBookingAccessException("You are not authorized to cancel this booking");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new InvalidStatusTransitionException("Booking is already cancelled");
        }
        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new InvalidStatusTransitionException("Cannot cancel a completed booking");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Booking updatedBooking = bookingRepository.save(booking);

        return BookingResponse.fromEntity(updatedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookingResponse> getAllBookingsAdmin(BookingStatus status, int page, int size) {
        validatePagination(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Booking> bookingPage;

        if (status != null) {
            bookingPage = bookingRepository.findByStatus(status, pageable);
        } else {
            bookingPage = bookingRepository.findAll(pageable);
        }

        return PageResponse.fromPage(bookingPage.map(BookingResponse::fromEntity));
    }

    @Override
    public BookingResponse updateBookingStatusAdmin(Long id, UpdateBookingStatusRequest request) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));

        BookingStatus currentStatus = booking.getStatus();
        BookingStatus targetStatus = request.getStatus();

        if (currentStatus == targetStatus) {
            return BookingResponse.fromEntity(booking);
        }

        if (currentStatus == BookingStatus.CANCELLED) {
            throw new InvalidStatusTransitionException("Cannot transition a cancelled booking to any other status");
        }
        if (currentStatus == BookingStatus.COMPLETED) {
            throw new InvalidStatusTransitionException("Cannot transition a completed booking to any other status");
        }

        booking.setStatus(targetStatus);
        Booking updatedBooking = bookingRepository.save(booking);

        return BookingResponse.fromEntity(updatedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public AvailabilityResponse checkCarAvailability(Long carId, LocalDate startDate, LocalDate endDate) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new CarNotFoundException(carId));

        validateBookingDates(startDate, endDate);

        long rentalDays = ChronoUnit.DAYS.between(startDate, endDate);
        BigDecimal estimatedPrice = car.getPricePerDay().multiply(BigDecimal.valueOf(rentalDays));

        if (!car.isAvailable()) {
            return new AvailabilityResponse(
                    car.getId(),
                    car.getBrand(),
                    car.getModel(),
                    startDate,
                    endDate,
                    false,
                    rentalDays,
                    car.getPricePerDay(),
                    estimatedPrice,
                    "Vehicle is currently marked unavailable for rental"
            );
        }

        boolean hasOverlap = bookingRepository.existsOverlappingBookings(
                car.getId(),
                startDate,
                endDate,
                ACTIVE_STATUSES
        );

        if (hasOverlap) {
            return new AvailabilityResponse(
                    car.getId(),
                    car.getBrand(),
                    car.getModel(),
                    startDate,
                    endDate,
                    false,
                    rentalDays,
                    car.getPricePerDay(),
                    estimatedPrice,
                    "Vehicle is already booked for the requested dates"
            );
        }

        return new AvailabilityResponse(
                car.getId(),
                car.getBrand(),
                car.getModel(),
                startDate,
                endDate,
                true,
                rentalDays,
                car.getPricePerDay(),
                estimatedPrice,
                "Vehicle is available for the requested dates"
        );
    }

    private void validateBookingDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new InvalidBookingDateException("Start date and end date are required");
        }
        if (startDate.isBefore(LocalDate.now())) {
            throw new InvalidBookingDateException("Start date cannot be in the past");
        }
        if (!endDate.isAfter(startDate)) {
            throw new InvalidBookingDateException("End date must be after start date");
        }
    }

    private void validatePagination(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page index must not be less than zero");
        }
        if (size < 1 || size > 50) {
            throw new IllegalArgumentException("Page size must be between 1 and 50");
        }
    }
}
