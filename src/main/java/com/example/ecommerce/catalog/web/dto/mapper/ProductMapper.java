package com.example.ecommerce.catalog.web.dto.mapper;

import com.example.ecommerce.catalog.domain.Category;
import com.example.ecommerce.catalog.domain.Product;
import com.example.ecommerce.catalog.domain.ProductImage;
import com.example.ecommerce.catalog.domain.ProductVariant;
import com.example.ecommerce.catalog.web.dto.product.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class ProductMapper {

    public static Product toEntity(CreateProductRequestDTO dto, Category category) {
        if (dto == null) {
            return null;
        }

        Product.Status status = Product.Status.OUT_OF_STOCK;
        if (dto.getStatus() != null) {
            try {
                status = Product.Status.valueOf(dto.getStatus());
            } catch (IllegalArgumentException e) {
                // Default to OUT_OF_STOCK if invalid status provided
                status = Product.Status.OUT_OF_STOCK;
            }
        }

        return new Product.Builder()
                .productName(dto.getProductName())
                .description(dto.getDescription())
                .brandName(dto.getBrandName())
                .price(dto.getPrice())
                .weight(dto.getWeight())
                .rating(dto.getRating() != null ? dto.getRating() : BigDecimal.ONE)
                .stockQuantity(dto.getStockQuantity() != null ? dto.getStockQuantity() : 0L)
                .category(category)
                .status(status)
                .build();
    }

    public static CreateProductResponseDTO toCreateResponseDTO(Product entity) {
        if (entity == null) {
            return null;
        }

        return new CreateProductResponseDTO(
                entity.getId(),
                entity.getProductName(),
                entity.getBrandName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getWeight(),
                entity.getRating(),
                entity.getStockQuantity(),
                entity.getStatus().name(),
                entity.getCategory(),
                entity.getProductImages(),
                entity.getProductVariants(),
                entity.isInStock(),
                entity.isActive(),
                entity.isAvailable(),
                entity.hasVariants(),
                entity.hasPrimaryImage(),
                entity.getTotalStock(),
                entity.getLowestPrice(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public static ProductResponseDTO toResponseDTO(Product entity) {
        if (entity == null) {
            return null;
        }

        ProductResponseDTO.CategorySummary categorySummary = new ProductResponseDTO.CategorySummary(
                entity.getCategory().getId(),
                entity.getCategory().getName(),
                entity.getCategory().getSlug());

        List<ProductImageResponseDTO> imageResponseDTOs = ProductImageMapper
                .toResponseDTOList(entity.getProductImages());
        List<ProductVariantResponseDTO> variantResponseDTOs = ProductVariantMapper
                .toResponseDTOList(entity.getProductVariants());

        return new ProductResponseDTO(
                entity.getId(),
                entity.getProductName(),
                entity.getDescription(),
                entity.getBrandName(),
                entity.getPrice(),
                entity.getWeight(),
                entity.getRating(),
                entity.getStockQuantity(),
                entity.getStatus().name(),
                categorySummary,
                entity.isInStock(),
                entity.isActive(),
                entity.isAvailable(),
                entity.hasVariants(),
                entity.hasPrimaryImage(),
                entity.getTotalStock(),
                entity.getLowestPrice(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                imageResponseDTOs,
                variantResponseDTOs);
    }

    public static List<ProductResponseDTO> toResponseDTOList(List<Product> entities) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(ProductMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public static void updateEntityFromDTO(Product entity, CreateProductRequestDTO dto, Category category) {
        if (entity == null || dto == null) {
            return;
        }

        if (dto.getProductName() != null) {
            entity.updateProductName(dto.getProductName());
        }

        if (dto.getDescription() != null) {
            entity.updateDescription(dto.getDescription());
        }

        if (dto.getBrandName() != null) {
            entity.updateBrandName(dto.getBrandName());
        }

        if (dto.getPrice() != null) {
            entity.updatePrice(dto.getPrice());
        }

        if (dto.getWeight() != null) {
            entity.updateWeight(dto.getWeight());
        }

        if (dto.getRating() != null) {
            entity.updateRating(dto.getRating());
        }

        if (dto.getStockQuantity() != null) {
            entity.updateStockQuantity(dto.getStockQuantity());
        }

        if (category != null) {
            entity.updateCategory(category);
        }

        if (dto.getStatus() != null) {
            try {
                Product.Status status = Product.Status.valueOf(dto.getStatus());
                entity.updateStatus(status);
            } catch (IllegalArgumentException e) {
                // Log warning but don't fail the update
                System.err.println("Invalid status provided: " + dto.getStatus());
            }
        }
    }

    public static void addImagesToProduct(Product product, List<ProductImagePayload> imagePayloads) {
        if (product == null || imagePayloads == null) {
            return;
        }

        for (ProductImagePayload payload : imagePayloads) {
            ProductImage image = ProductImageMapper.toEntity(payload, product);
            product.addImage(image);
        }
    }
}