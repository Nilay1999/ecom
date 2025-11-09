package com.example.ecommerce.catalog.dto.common;

public class ApiResponseDto {

    public static <T> ApiResponse<T> success(int statusCode, String message, T data, Object metadata) {
        return new ApiResponse<>(statusCode, message, data, metadata);
    }

    public static <T> ApiResponse<T> error(int statusCode, String message, T data) {
        return new ApiResponse<>(statusCode, message, data, null);
    }
}