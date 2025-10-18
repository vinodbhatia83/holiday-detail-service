package com.accenture.assignment.holiday.exeception;


import com.accenture.assignment.holiday.model.ProblemDetail;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ApplicationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        ProblemDetail response = createProblemDetail(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                errors.toString(),
                "/api"
        );
        return ResponseEntity.badRequest().body(response);
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