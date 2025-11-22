package com.example.demo.dto.common;

import java.time.Instant;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a structured API error payload.
 *
 * Main concept:
 * - Provide a consistent error response body for REST endpoints.
 *
 * Responsibilities:
 * - Carry standard fields like timestamp, HTTP status, error message, and optionally
 *   a map of validation errors.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiError {

    // time the error occurred
    private Instant timestamp;

    // numeric HTTP status code
    private int status;

    // short HTTP status reason (e.g., "Bad Request")
    private String error;

    // human-readable message
    private String message;

    // request path where the error occurred
    private String path;

    // optional map of field -> message for validation errors
    private Map<String, String> validationErrors;

}
