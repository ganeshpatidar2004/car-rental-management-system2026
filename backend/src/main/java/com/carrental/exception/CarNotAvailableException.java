package com.carrental.exception;

/**
 * Exception thrown when attempting to book a car that is not in available status.
 */
public class CarNotAvailableException extends RuntimeException {

    public CarNotAvailableException(String message) {
        super(message);
    }

    public CarNotAvailableException(Long carId) {
        super("Car with id " + carId + " is currently not available for rental");
    }
}
