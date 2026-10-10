package com.carrental.exception;

/**
 * Exception thrown when attempting to register with an email address that already exists.
 */
public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String message) {
        super(message);
    }

    public static DuplicateEmailException forEmail(String email) {
        return new DuplicateEmailException("Email '" + email + "' is already registered");
    }
}
