package com.carrental.exception;

/**
 * Exception thrown when a requested car is not found in the database.
 */
public class CarNotFoundException extends RuntimeException {

    public CarNotFoundException(String message) {
        super(message);
    }

    public CarNotFoundException(Long id) {
        super("Car not found with id: " + id);
    }
}
