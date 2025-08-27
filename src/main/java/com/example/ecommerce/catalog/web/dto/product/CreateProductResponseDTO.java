package com.example.ecommerce.catalog.web.dto.product;

import com.example.ecommerce.catalog.domain.Category;
import com.example.ecommerce.catalog.domain.ProductImage;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class CreateProductResponseDTO {
    private final UUID id;
    private final String description;
    private final String brandName;
    private final BigDecimal price;
    private final BigDecimal weight;
    private final long stockQuantity;
    private final String categoryName;
    private List<ProductImageRes> productImageList;

    public record ProductImageRes(String imageUrl, boolean isPrimary) {
    }


    public CreateProductResponseDTO(UUID id, String brandName, String description, BigDecimal price, BigDecimal weight,
            long stockQuantity, Category category, List<ProductImage> productImages) {
        this.id = id;
        this.brandName = brandName;
        this.description = description;
        this.price = price;
        this.weight = weight;
        this.stockQuantity = stockQuantity;
        this.categoryName = category.getName();
        this.productImageList = new ArrayList<>();
        for (ProductImage image : productImages) {
            ProductImageRes imageRes = new ProductImageRes(image.getImageUrl(), image.isPrimary());
            productImageList.add(imageRes);
        }
    }

    public CreateProductResponseDTO(UUID id, String brandName, String description, BigDecimal price, BigDecimal weight,
            long stockQuantity, Category category) {
        this.id = id;
        this.brandName = brandName;
        this.description = description;
        this.price = price;
        this.weight = weight;
        this.stockQuantity = stockQuantity;
        this.categoryName = category.getName();
    }
}
