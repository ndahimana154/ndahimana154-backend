package com.bonheur.portfolio.utils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class ValidationUtils {

    private final Validator validator;

    public ValidationUtils(Validator validator) {
        this.validator = validator;
    }

    public <T> ResponseEntity<Map<String, Object>> validateAndBuildResponse(T objectToValidate) {

        ResponseEntity<Map<String, Object>> errorResponse1 = getMapResponseEntity(objectToValidate);
        if (errorResponse1 != null)
            return errorResponse1;

        Set<ConstraintViolation<T>> violations = validator.validate(objectToValidate);

        if (!violations.isEmpty()) {
            Map<String, Object> errorDetails = new HashMap<>();
            for (ConstraintViolation<T> violation : violations) {
                String propertyPath = violation.getPropertyPath().toString();
                String errorMessage = violation.getMessage();
                errorDetails.put(propertyPath, errorMessage);
            }

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Validation failed please fill all required fields");
            errorResponse.put("details", errorDetails);
            errorResponse.put("action", 0);
            return ResponseEntity.status(HttpStatus.OK).body(errorResponse);
        }

        return null; // Validation successful, continue processing
    }

    public <T> ResponseEntity<Map<String, Object>> validateAndBuildResponse(T objectToValidate, Class<?>... groups) {

        ResponseEntity<Map<String, Object>> errorResponse1 = getMapResponseEntity(objectToValidate);
        if (errorResponse1 != null)
            return errorResponse1;

        // Pass groups to validator
        Set<ConstraintViolation<T>> violations = validator.validate(objectToValidate, groups);

        if (!violations.isEmpty()) {
            Map<String, Object> errorDetails = new HashMap<>();
            for (ConstraintViolation<T> violation : violations) {
                String propertyPath = violation.getPropertyPath().toString();
                String errorMessage = violation.getMessage();
                errorDetails.put(propertyPath, errorMessage);
            }

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Validation failed please fill all required fields");
            errorResponse.put("details", errorDetails);
            errorResponse.put("action", 0);
            return ResponseEntity.status(HttpStatus.OK).body(errorResponse);
        }

        return null; // Validation successful
    }

    private <T> ResponseEntity<Map<String, Object>> getMapResponseEntity(T objectToValidate) {
        if (objectToValidate == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Validation failed please fill all required fields");
            errorResponse.put("details", "Request body is empty");
            errorResponse.put("action", 0);
            return ResponseEntity.status(HttpStatus.OK).body(errorResponse);
        }
        return null;
    }
}
