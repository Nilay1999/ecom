package com.example.ecommerce.catalog.dto.common;

import lombok.Getter;

@Getter
public class ApiResponse<T> {
    private int status;
    private String message;
    private T data;
    private Object metadata;

    public ApiResponse(int status, String message, T data, Object metadata) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.metadata = metadata;
    }
}
