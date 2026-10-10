package com.carrental.exception;

/**
 * Exception thrown when a requested booking is not found in the database.
 */
public class BookingNotFoundException extends RuntimeException {

    public BookingNotFoundException(String message) {
        super(message);
    }

    public BookingNotFoundException(Long id) {
        super("Booking not found with id: " + id);
    }
}
