package com.carrental.controller;

import com.carrental.dto.BookingResponse;
import com.carrental.dto.PageResponse;
import com.carrental.dto.UpdateBookingStatusRequest;
import com.carrental.entity.BookingStatus;
import com.carrental.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for administrative booking management restricted to ADMIN role.
 */
@RestController
@RequestMapping("/api/admin/bookings")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBookingController {

    private final BookingService bookingService;

    public AdminBookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * GET /api/admin/bookings
     * Lists all bookings with pagination and optional status filter.
     */
    @GetMapping
    public ResponseEntity<PageResponse<BookingResponse>> getAllBookings(
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<BookingResponse> response = bookingService.getAllBookingsAdmin(status, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * PATCH /api/admin/bookings/{id}/status
     * Updates a booking's status (e.g. CONFIRMED, COMPLETED, CANCELLED).
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<BookingResponse> updateBookingStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBookingStatusRequest request) {
        BookingResponse response = bookingService.updateBookingStatusAdmin(id, request);
        return ResponseEntity.ok(response);
    }
}
