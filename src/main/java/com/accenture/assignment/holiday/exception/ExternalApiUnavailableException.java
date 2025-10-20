package com.accenture.assignment.holiday.exception;

/**
 * Exception thrown when an external API is unavailable or cannot be reached.
 * <p>
 * Used to signal service unavailability errors in the application.
 */
public class ExternalApiUnavailableException extends RuntimeException {

    /**
     * Constructs a new {@code ExternalApiUnavailableException} with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public ExternalApiUnavailableException(String message) {
        super(message);
    }
}