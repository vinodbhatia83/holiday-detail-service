package com.accenture.assignment.holiday.exception.handler;

import com.accenture.assignment.holiday.exception.ExternalApiUnavailableException;
import com.accenture.assignment.holiday.exception.InvalidCountryException;
import com.accenture.assignment.holiday.model.ProblemDetail;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.apache.hc.client5.http.ConnectTimeoutException;
import org.apache.hc.core5.http.ConnectionRequestTimeoutException;
import org.junit.jupiter.api.Test;
import jakarta.validation.Path;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationExceptionHandlerTest {

    private final ApplicationExceptionHandler handler = new ApplicationExceptionHandler();

    private HttpServletRequest mockRequest(String uri) {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        Mockito.when(req.getRequestURI()).thenReturn(uri);
        return req;
    }

    @Test
    void testHandleConstraintViolationException() {
        ConstraintViolation<?> violation = Mockito.mock(ConstraintViolation.class);
        Path mockPath = Mockito.mock(Path.class);
        Mockito.when(mockPath.toString()).thenReturn("field");
        Mockito.when(violation.getPropertyPath()).thenReturn(mockPath);
        Mockito.when(violation.getMessage()).thenReturn("must not be null");
        Set<ConstraintViolation<?>> violations = Collections.singleton(violation);
        ConstraintViolationException ex = new ConstraintViolationException(violations);

        ResponseEntity<ProblemDetail> response = handler.handleConstraintViolationException(ex, mockRequest("/test"));
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().message().contains("field: must not be null"));
    }

    @Test
    void testHandleTypeMismatch() {
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                "value", String.class, "param", null, new IllegalArgumentException("bad type"));
        ResponseEntity<ProblemDetail> response = handler.handleTypeMismatch(ex, mockRequest("/type"));
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().message().contains("Parameter 'param' must be of type String"));
    }

    @Test
    void testHandleMissingParams() {
        MissingServletRequestParameterException ex = new MissingServletRequestParameterException("param", "String");
        ResponseEntity<ProblemDetail> response = handler.handleMissingParams(ex, mockRequest("/missing"));
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().message().contains("Missing required parameter: param"));
    }

    @Test
    void testHandleInvalidCountry() {
        InvalidCountryException ex = new InvalidCountryException("Invalid country code");
        ResponseEntity<ProblemDetail> response = handler.handleInvalidCountry(ex, mockRequest("/country"));
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid country code", response.getBody().message());
    }

    @Test
    void testHandleExternalApiUnavailableException() {
        ExternalApiUnavailableException ex = new ExternalApiUnavailableException("API down");
        ResponseEntity<ProblemDetail> response = handler.handleExternalApiUnavailableException(ex, mockRequest("/api"));
        assertEquals(503, response.getStatusCodeValue());
        assertEquals("API down", response.getBody().message());
    }

    @Test
    void testHandleRestClientException() {
        RestClientException ex = new RestClientException("Rest error");
        ResponseEntity<ProblemDetail> response = handler.handleRestClientException(ex, mockRequest("/rest"));
        assertEquals(502, response.getStatusCodeValue());
        assertTrue(response.getBody().message().contains("Rest error"));
    }

    @Test
    void testHandleConnectTimeout() {
        ConnectTimeoutException ex = new ConnectTimeoutException("Timeout");
        ResponseEntity<ProblemDetail> response = handler.handleConnectTimeout(ex, mockRequest("/timeout"));
        assertEquals(504, response.getStatusCodeValue());
        assertTrue(response.getBody().message().contains("Connection timed out"));
    }

    @Test
    void testHandleResponseTimeout() {
        ConnectionRequestTimeoutException ex = new ConnectionRequestTimeoutException("Timeout");
        ResponseEntity<ProblemDetail> response = handler.handleResponseTimeout(ex, mockRequest("/timeout2"));
        assertEquals(504, response.getStatusCodeValue());
        assertTrue(response.getBody().message().contains("Response timed out"));
    }

    @Test
    void testHandleNoResourceFoundException() {
        NoResourceFoundException ex = new NoResourceFoundException(HttpMethod.GET, "Resource not found");
        ResponseEntity<ProblemDetail> response = handler.handleNoResourceFoundException(ex, mockRequest("/notfound"));
        assertEquals(404, response.getStatusCodeValue());
        assertTrue(response.getBody().message().contains("not found"));
    }

    @Test
    void testHandleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Bad arg");
        ResponseEntity<ProblemDetail> response = handler.handleIllegalArgumentException(ex, mockRequest("/illegal"));
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Bad arg", response.getBody().message());
    }

    @Test
    void testHandleNullPointerException() {
        NullPointerException ex = new NullPointerException("Null");
        ResponseEntity<ProblemDetail> response = handler.handleNullPointerException(ex, mockRequest("/null"));
        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().message().contains("A required value was missing"));
    }

    @Test
    void testHandleRuntimeException() {
        RuntimeException ex = new RuntimeException("Runtime");
        ResponseEntity<ProblemDetail> response = handler.handleRuntimeException(ex, mockRequest("/runtime"));
        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().message().contains("An unexpected error occurred"));
    }

    @Test
    void testHandleGeneralException() {
        Exception ex = new Exception("General");
        ResponseEntity<ProblemDetail> response = handler.handleGeneralException(ex, mockRequest("/general"));
        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().message().contains("An unexpected error occurred"));
    }
}