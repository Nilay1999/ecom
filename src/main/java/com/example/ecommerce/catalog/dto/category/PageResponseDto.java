package com.example.ecommerce.catalog.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Schema(description = "Paginated response")
public class PageResponseDto<T> {

    @Schema(description = "List of item in the current page")
    private List<T> content;

    @Schema(description = "Current page number (0-based)")
    private int number;

    @Schema(description = "Size of the page")
    private int size;

    @Schema(description = "Total number of elements")
    private long totalElements;

    @Schema(description = "Total number of pages")
    private int totalPages;

    @Schema(description = "True if there is a next page")
    private boolean hasNext;

    @Schema(description = "True if there is a previous page")
    private boolean hasPrevious;
}
