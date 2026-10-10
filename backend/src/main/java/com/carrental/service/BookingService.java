package com.carrental.service;

import com.carrental.dto.AvailabilityResponse;
import com.carrental.dto.BookingResponse;
import com.carrental.dto.CreateBookingRequest;
import com.carrental.dto.PageResponse;
import com.carrental.dto.UpdateBookingStatusRequest;
import com.carrental.entity.BookingStatus;

import java.time.LocalDate;

/**
 * Service interface defining Booking management operations.
 */
public interface BookingService {

    /**
     * Creates a new booking for an authenticated user.
     *
     * @param userEmail email of the authenticated user
     * @param request booking details
     * @return created BookingResponse
     */
    BookingResponse createBooking(String userEmail, CreateBookingRequest request);

    /**
     * Retrieves paginated bookings belonging to the authenticated user.
     *
     * @param userEmail user email
     * @param page page index
     * @param size page size
     * @return paginated bookings
     */
    PageResponse<BookingResponse> getMyBookings(String userEmail, int page, int size);

    /**
     * Retrieves a booking by ID with ownership enforcement.
     *
     * @param id booking ID
     * @param userEmail user email
     * @param isAdmin whether the user has ADMIN role
     * @return BookingResponse
     */
    BookingResponse getBookingById(Long id, String userEmail, boolean isAdmin);

    /**
     * Cancels a booking with ownership enforcement.
     *
     * @param id booking ID
     * @param userEmail user email
     * @param isAdmin whether the user has ADMIN role
     * @return updated BookingResponse
     */
    BookingResponse cancelBooking(Long id, String userEmail, boolean isAdmin);

    /**
     * Administrative listing of all bookings with optional status filter.
     *
     * @param status optional status filter
     * @param page page index
     * @param size page size
     * @return paginated bookings
     */
    PageResponse<BookingResponse> getAllBookingsAdmin(BookingStatus status, int page, int size);

    /**
     * Administrative update of a booking's lifecycle status.
     *
     * @param id booking ID
     * @param request status update request
     * @return updated BookingResponse
     */
    BookingResponse updateBookingStatusAdmin(Long id, UpdateBookingStatusRequest request);

    /**
     * Checks if a vehicle is available for a given date range.
     *
     * @param carId target car ID
     * @param startDate start date
     * @param endDate end date
     * @return AvailabilityResponse with price estimate and availability status
     */
    AvailabilityResponse checkCarAvailability(Long carId, LocalDate startDate, LocalDate endDate);
}
