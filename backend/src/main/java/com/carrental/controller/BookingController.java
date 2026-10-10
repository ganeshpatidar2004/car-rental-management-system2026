package com.carrental.controller;

import com.carrental.dto.BookingResponse;
import com.carrental.dto.CreateBookingRequest;
import com.carrental.dto.PageResponse;
import com.carrental.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * REST controller for authenticated customer and administrative booking operations.
 */
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * POST /api/bookings
     * Creates a new booking for the authenticated user.
     */
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody CreateBookingRequest request,
            Principal principal) {
        BookingResponse response = bookingService.createBooking(principal.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/bookings/my
     * Retrieves paginated bookings belonging to the authenticated user.
     */
    @GetMapping("/my")
    public ResponseEntity<PageResponse<BookingResponse>> getMyBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal) {
        PageResponse<BookingResponse> response = bookingService.getMyBookings(principal.getName(), page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/bookings/{id}
     * Retrieves a booking by ID (accessible by owner or ADMIN).
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(
            @PathVariable Long id,
            Authentication authentication) {
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        BookingResponse response = bookingService.getBookingById(id, authentication.getName(), isAdmin);
        return ResponseEntity.ok(response);
    }

    /**
     * PATCH /api/bookings/{id}/cancel
     * Cancels an eligible booking (accessible by owner or ADMIN).
     */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable Long id,
            Authentication authentication) {
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        BookingResponse response = bookingService.cancelBooking(id, authentication.getName(), isAdmin);
        return ResponseEntity.ok(response);
    }
}
