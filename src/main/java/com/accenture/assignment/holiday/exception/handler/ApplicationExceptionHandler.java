package com.accenture.assignment.holiday.exception.handler;

import com.accenture.assignment.holiday.exception.ExternalApiUnavailableException;
import com.accenture.assignment.holiday.exception.InvalidCountryException;
import com.accenture.assignment.holiday.model.ProblemDetail;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.ConnectTimeoutException;
import org.apache.hc.core5.http.ConnectionRequestTimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ApplicationExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolationException(
            ConstraintViolationException ex,
            HttpServletRequest request) {
        String details = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining("; "));
        ProblemDetail response = createProblemDetail(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                details,
                request.getRequestURI()
        );
        return ResponseEntity.badRequest().body(response);
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String message = String.format("Parameter '%s' must be of type %s", ex.getName(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");
        log.warn("Type mismatch at {}: {}", request.getRequestURI(), message);
        ProblemDetail response = createProblemDetail(
                HttpStatus.BAD_REQUEST.value(),
                "Type Mismatch",
                message,
                request.getRequestURI()
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ProblemDetail> handleMissingParams(
            MissingServletRequestParameterException ex,
            HttpServletRequest request) {
        log.warn("MissingServletRequestParameterException at {}: {}", request.getRequestURI(), ex.getMessage());
        ProblemDetail response = createProblemDetail(
                HttpStatus.BAD_REQUEST.value(),
                "Missing Request Parameter",
                "Missing required parameter: " + ex.getParameterName(),
                request.getRequestURI()
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(InvalidCountryException.class)
    public ResponseEntity<ProblemDetail> handleInvalidCountry(InvalidCountryException ex, HttpServletRequest request) {
        ProblemDetail response = createProblemDetail(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Country",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ExternalApiUnavailableException.class)
    public ResponseEntity<ProblemDetail> handleExternalApiUnavailableException(
            ExternalApiUnavailableException ex, HttpServletRequest request) {
        log.error("ExternalApiUnavailableException at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        ProblemDetail response = createProblemDetail(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "Service Unavailable",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ProblemDetail> handleRestClientException(RestClientException ex, HttpServletRequest request) {
        log.error("RestClientException at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        ProblemDetail response = createProblemDetail(
                HttpStatus.BAD_GATEWAY.value(),
                "External API Error",
                "External API error: " + ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(response);
    }

    @ExceptionHandler(ConnectTimeoutException.class)
    public ResponseEntity<ProblemDetail> handleConnectTimeout(ConnectTimeoutException ex, HttpServletRequest request) {
        log.error("ConnectTimeoutException at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        ProblemDetail response = createProblemDetail(
                HttpStatus.GATEWAY_TIMEOUT.value(),
                "Gateway Timeout",
                "Connection timed out. Please try again later.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(response);
    }

    @ExceptionHandler(ConnectionRequestTimeoutException.class)
    public ResponseEntity<ProblemDetail> handleResponseTimeout(
            ConnectionRequestTimeoutException ex,
            HttpServletRequest request) {
        log.error("ResponseTimeoutException at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        ProblemDetail response = createProblemDetail(
                HttpStatus.GATEWAY_TIMEOUT.value(),
                "Gateway Timeout",
                "Response timed out. Please try again later.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(response);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ProblemDetail> handleNoResourceFoundException(
            NoResourceFoundException ex,
            HttpServletRequest request) {
        log.warn("NoResourceFoundException at {}: {}", request.getRequestURI(), ex.getMessage());
        ProblemDetail response = createProblemDetail(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                "The requested resource was not found.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request) {
        log.warn("IllegalArgumentException at {}: {}", request.getRequestURI(), ex.getMessage());
        ProblemDetail response = createProblemDetail(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ProblemDetail> handleNullPointerException(NullPointerException ex, HttpServletRequest request) {
        log.error("NullPointerException at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        ProblemDetail response = createProblemDetail(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Null Pointer Exception",
                "A required value was missing. Please contact support.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ProblemDetail> handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        log.error("RuntimeException at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        ProblemDetail response = createProblemDetail(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred. Please contact support.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneralException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        ProblemDetail response = createProblemDetail(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred. Please contact support.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private ProblemDetail createProblemDetail(int status, String error, String message, String path) {
        return new ProblemDetail(
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                status,
                error,
                message,
                path
        );
    }
}