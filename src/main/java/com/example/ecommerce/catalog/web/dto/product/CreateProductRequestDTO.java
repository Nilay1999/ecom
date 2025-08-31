package com.example.ecommerce.catalog.web.dto.product;

import jakarta.validation.constraints.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
public class CreateProductRequestDTO {

    @NotBlank
    private String productName;

    private String description;

    @NotBlank
    private String brandName;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = false)
    @Digits(integer = 10, fraction = 2)
    private BigDecimal price;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = false)
    @Digits(integer = 10, fraction = 2)
    private BigDecimal weight;

    @Min(0)
    private long stockQuantity;

    @NotNull
    private UUID categoryId;

    @NotEmpty
    private List<ProductImagePayload> productImageList;
}
