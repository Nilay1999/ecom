package com.example.ecommerce.catalog.web.dto.product;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateProductRequestDTO {

    @NotBlank(message = "Product name cannot be blank")
    @Size(max = 255, message = "Product name cannot exceed 255 characters")
    private String productName;

    @Size(max = 20000, message = "Description cannot exceed 20000 characters")
    private String description;

    @NotBlank(message = "Brand name cannot be blank")
    @Size(max = 100, message = "Brand name cannot exceed 100 characters")
    private String brandName;

    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.00", inclusive = false, message = "Price must be greater than zero")
    @Digits(integer = 10, fraction = 2, message = "Price must have at most 10 integer digits and 2 decimal places")
    private BigDecimal price;

    @NotNull(message = "Weight cannot be null")
    @DecimalMin(value = "0.00", inclusive = false, message = "Weight must be greater than zero")
    @Digits(integer = 10, fraction = 2, message = "Weight must have at most 10 integer digits and 2 decimal places")
    private BigDecimal weight;

    @DecimalMin(value = "0.00", inclusive = true, message = "Rating must be between 0 and 5")
    @DecimalMax(value = "5.00", inclusive = true, message = "Rating must be between 0 and 5")
    @Digits(integer = 1, fraction = 2, message = "Rating must have at most 1 integer digit and 2 decimal places")
    private BigDecimal rating = BigDecimal.ONE;

    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Long stockQuantity = 0L;

    @NotNull(message = "Category ID cannot be null")
    private UUID categoryId;

    @Pattern(regexp = "^(ACTIVE|IN_ACTIVE|OUT_OF_STOCK)$", message = "Status must be one of: ACTIVE, IN_ACTIVE, OUT_OF_STOCK")
    private String status = "OUT_OF_STOCK";

    @NotEmpty(message = "Product must have at least one image")
    @Valid
    private List<ProductImagePayload> productImageList;
}
