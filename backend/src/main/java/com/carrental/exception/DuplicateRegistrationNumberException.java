package com.carrental.exception;

/**
 * Exception thrown when attempting to create or update a car with a duplicate registration number.
 */
public class DuplicateRegistrationNumberException extends RuntimeException {

    public DuplicateRegistrationNumberException(String message) {
        super(message);
    }

    public static DuplicateRegistrationNumberException forRegistrationNumber(String registrationNumber) {
        return new DuplicateRegistrationNumberException(
                "Car with registration number '" + registrationNumber + "' already exists"
        );
    }
}
