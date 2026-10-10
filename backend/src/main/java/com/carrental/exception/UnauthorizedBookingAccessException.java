package com.carrental.exception;

/**
 * Exception thrown when a user attempts to view or modify another user's booking.
 */
public class UnauthorizedBookingAccessException extends RuntimeException {

    public UnauthorizedBookingAccessException(String message) {
        super(message);
    }

    public UnauthorizedBookingAccessException() {
        super("You are not authorized to view or modify this booking");
    }
}
