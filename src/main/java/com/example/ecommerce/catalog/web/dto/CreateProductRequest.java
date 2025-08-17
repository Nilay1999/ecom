package com.example.ecommerce.catalog.web.dto;

import lombok.Getter;

import java.math.BigDecimal;

public class CreateProductRequest {
    @Getter
    private String productName;
    @Getter
    private String description;
    @Getter
    private String brandName;
    @Getter
    private BigDecimal price;
}
