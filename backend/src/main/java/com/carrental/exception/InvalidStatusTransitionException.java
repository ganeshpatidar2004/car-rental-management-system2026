package com.carrental.exception;

/**
 * Exception thrown when attempting an illegal booking status transition.
 */
public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(String message) {
        super(message);
    }
}
