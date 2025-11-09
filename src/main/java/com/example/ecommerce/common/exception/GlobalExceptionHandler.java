package com.example.ecommerce.common.exception;

import com.example.ecommerce.catalog.dto.common.ApiResponse;
import com.example.ecommerce.catalog.dto.common.ApiResponseDto;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        ApiResponse<Map<String, String>> response = ApiResponseDto.error(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleDataIntegrityViolation(
            DataIntegrityViolationException ex) {
        Map<String, String> error = new HashMap<>();
        String errorMessage;

        String message = ex.getMessage();
        if (message != null && message.contains("categories_slug_key")) {
            error.put("slug", "A category with this slug already exists");
            errorMessage = "Duplicate slug";
        } else if (message != null && message.contains("categories_name_key")) {
            error.put("name", "A category with this name already exists");
            errorMessage = "Duplicate name";
        } else {
            error.put("error", "Data integrity violation: " + ex.getMostSpecificCause().getMessage());
            errorMessage = "Data integrity violation";
        }

        ApiResponse<Map<String, String>> response = ApiResponseDto.error(
                HttpStatus.CONFLICT.value(),
                errorMessage,
                error);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());

        ApiResponse<Map<String, String>> response = ApiResponseDto.error(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid argument",
                error);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
