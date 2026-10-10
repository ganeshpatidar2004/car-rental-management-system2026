package com.carrental.exception;

/**
 * Exception thrown when requested booking dates violate business validation rules.
 */
public class InvalidBookingDateException extends RuntimeException {

    public InvalidBookingDateException(String message) {
        super(message);
    }
}
