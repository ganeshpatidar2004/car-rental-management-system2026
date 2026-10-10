package com.carrental.exception;

/**
 * Exception thrown when a booking overlaps with an existing active booking for the same car.
 */
public class BookingConflictException extends RuntimeException {

    public BookingConflictException(String message) {
        super(message);
    }
}
