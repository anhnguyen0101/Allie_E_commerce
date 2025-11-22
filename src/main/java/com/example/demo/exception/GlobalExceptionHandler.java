package com.example.demo.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.demo.dto.common.ApiError;

/**
 * Global exception handler that transforms exceptions into `ApiError` responses.
 *
 * Main concept:
 * - Centralize exception-to-response mapping so controllers remain clean and
 *   clients receive consistent error payloads.
 *
 * Responsibilities:
 * - Handle validation failures (`MethodArgumentNotValidException`) and return HTTP 400 with
 *   a map of field errors.
 * - Handle `EntityNotFoundException` and return HTTP 404.
 * - Construct an `ApiError` object containing timestamp, status, message, path, etc.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle validation errors thrown when @Valid fails on controller method arguments.
     *
     * @param ex the MethodArgumentNotValidException
     * @param request the HttpServletRequest (used to capture request path)
     * @return ResponseEntity containing ApiError with HTTP 400
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        // collect field errors into a map
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        // build ApiError payload
        ApiError apiError = ApiError.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Validation failed")
                .path(request.getRequestURI())
                .validationErrors(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    /**
     * Handle cases where an entity wasn't found in the database.
     *
     * @param ex the EntityNotFoundException
     * @param request the HttpServletRequest
     * @return ResponseEntity containing ApiError with HTTP 404
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(EntityNotFoundException ex, HttpServletRequest request) {
        // build ApiError payload
        ApiError apiError = ApiError.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

}
