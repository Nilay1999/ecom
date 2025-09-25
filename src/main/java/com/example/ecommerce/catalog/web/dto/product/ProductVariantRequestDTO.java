package com.example.ecommerce.catalog.web.dto.product;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class ProductVariantRequestDTO {

    @NotNull(message = "Product ID cannot be null")
    private UUID productId;

    @NotBlank(message = "Variant name cannot be blank")
    @Size(max = 100, message = "Variant name cannot exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z0-9\\s\\-_]+$", message = "Variant name contains invalid characters. Only alphanumeric characters, spaces, hyphens, and underscores are allowed")
    private String variantName;

    @DecimalMin(value = "0.00", inclusive = true, message = "Price override must be non-negative")
    @Digits(integer = 10, fraction = 2, message = "Price override must have at most 10 integer digits and 2 decimal places")
    private BigDecimal priceOverride = BigDecimal.ZERO;

    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Long stockQuantity = 0L;

    @Valid
    private Map<@NotBlank @Size(max = 50) String, @Size(max = 255) String> attributes = new HashMap<>();

    @Pattern(regexp = "^[a-zA-Z0-9\\-_]*$", message = "SKU contains invalid characters. Only alphanumeric characters, hyphens, and underscores are allowed")
    @Size(max = 50, message = "SKU cannot exceed 50 characters")
    private String sku;

    @Pattern(regexp = "^(ACTIVE|INACTIVE|OUT_OF_STOCK|DISCONTINUED)$", message = "Status must be one of: ACTIVE, INACTIVE, OUT_OF_STOCK, DISCONTINUED")
    private String status = "ACTIVE";
}