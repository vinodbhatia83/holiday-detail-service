package com.accenture.assignment.holiday.exception;

/** Exception thrown when an invalid country is provided in a request.
 * <p>
 * Used to indicate validation errors related to country input.
 */
public class InvalidCountryException extends RuntimeException {

    /**
     * Constructs a new {@code InvalidCountryException} with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public InvalidCountryException(String message) {
        super(message);
    }
}