package com.example.ecommerce.catalog.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

import org.springframework.data.domain.Page;

@Schema(description = "Paginated response")
public record PageResponseDto<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last) {
    public static <T> PageResponseDto<T> from(Page<T> page) {
        return new PageResponseDto<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());
    }
}
