package com.backend.cms.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, IllegalArgumentException.class})
    public ResponseEntity<Map<String, String>> handleValidationException(Exception ex) {
        Map<String, String> errors = new HashMap<>();
        if (ex instanceof MethodArgumentNotValidException validationException) {
            validationException.getBindingResult().getAllErrors().forEach(error -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            });
        } else if (ex instanceof IllegalArgumentException) {
            errors.put("error", ex.getMessage());
        }

        // Custom error message
        String customMessage = "Validation failed. Please check the request data.";

        Map<String, String> response = new HashMap<>();
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        response.put("status", String.valueOf(HttpStatus.BAD_REQUEST.value()));
        response.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        response.put("message", customMessage);
        // Include detailed validation errors
        response.put("errors", errors.toString());

        return ResponseEntity.badRequest().body(response);
    }
}

