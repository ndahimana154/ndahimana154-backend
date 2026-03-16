package com.bonheur.portfolio.config;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidJson(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();
        Map<String, Object> errorResponse = new HashMap<>();

        String details;

        if (cause instanceof UnrecognizedPropertyException upe) {
            String unknownProperty = upe.getPropertyName();
            details = "Request body contains unrecognized property '" + unknownProperty + "'";
            errorResponse.put("message", "Validation failed: invalid request body");
            Map<String, String> fieldError = new HashMap<>();
            fieldError.put(unknownProperty, "This field is not allowed");
            errorResponse.put("details", fieldError);
        } else {
            details = ex.getMessage();
            errorResponse.put("message", "Invalid request body");
            errorResponse.put("details", details);
        }

        errorResponse.put("action", 0);
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Bad Request");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("message", "An error occurred");
        errorResponse.put("details", ex.getMessage());
        errorResponse.put("action", 0);
        errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.put("error", "Internal Server Error");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
