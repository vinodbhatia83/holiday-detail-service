package com.accenture.assignment.holiday.model;

public record ProblemDetail(
        String timestamp,
        int status,
        String error,
        String message,
        String path
) {}
