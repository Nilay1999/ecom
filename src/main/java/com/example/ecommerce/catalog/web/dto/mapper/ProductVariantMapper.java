package com.example.ecommerce.catalog.web.dto.mapper;

import com.example.ecommerce.catalog.domain.Product;
import com.example.ecommerce.catalog.domain.ProductVariant;
import com.example.ecommerce.catalog.web.dto.product.ProductVariantRequestDTO;
import com.example.ecommerce.catalog.web.dto.product.ProductVariantResponseDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProductVariantMapper {

    public static ProductVariant toEntity(ProductVariantRequestDTO dto, Product product) {
        if (dto == null) {
            return null;
        }

        ProductVariant.Builder builder = new ProductVariant.Builder()
                .product(product)
                .variantName(dto.getVariantName())
                .priceOverride(dto.getPriceOverride())
                .stockQuantity(dto.getStockQuantity() != null ? dto.getStockQuantity() : 0L)
                .sku(dto.getSku());

        // Handle attributes
        if (dto.getAttributes() != null && !dto.getAttributes().isEmpty()) {
            builder.attributes(new HashMap<>(dto.getAttributes()));
        }

        // Handle status
        if (dto.getStatus() != null) {
            try {
                ProductVariant.Status status = ProductVariant.Status.valueOf(dto.getStatus());
                builder.status(status);
            } catch (IllegalArgumentException e) {
                // Default to ACTIVE if invalid status provided
                builder.status(ProductVariant.Status.ACTIVE);
            }
        }

        return builder.build();
    }

    public static ProductVariantResponseDTO toResponseDTO(ProductVariant entity) {
        if (entity == null) {
            return null;
        }

        // Create a copy of attributes to avoid exposing internal map
        Map<String, String> attributesCopy = entity.getAttributes() != null
                ? new HashMap<>(entity.getAttributes())
                : new HashMap<>();

        return new ProductVariantResponseDTO(
                entity.getId(),
                entity.getProduct().getId(),
                entity.getVariantName(),
                entity.getPriceOverride(),
                entity.getEffectivePrice(),
                entity.hasPriceOverride(),
                entity.getStockQuantity(),
                attributesCopy,
                entity.getStatus().name(),
                entity.getSku(),
                entity.isInStock(),
                entity.isActive(),
                entity.isAvailable(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public static List<ProductVariant> toEntityList(List<ProductVariantRequestDTO> dtos, Product product) {
        if (dtos == null) {
            return List.of();
        }

        return dtos.stream()
                .map(dto -> toEntity(dto, product))
                .collect(Collectors.toList());
    }

    public static List<ProductVariantResponseDTO> toResponseDTOList(List<ProductVariant> entities) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(ProductVariantMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public static void updateEntityFromDTO(ProductVariant entity, ProductVariantRequestDTO dto) {
        if (entity == null || dto == null) {
            return;
        }

        if (dto.getVariantName() != null) {
            entity.updateVariantName(dto.getVariantName());
        }

        if (dto.getPriceOverride() != null) {
            entity.updatePriceOverride(dto.getPriceOverride());
        }

        if (dto.getStockQuantity() != null) {
            entity.updateStockQuantity(dto.getStockQuantity());
        }

        if (dto.getSku() != null) {
            entity.updateSku(dto.getSku());
        }

        if (dto.getStatus() != null) {
            try {
                ProductVariant.Status status = ProductVariant.Status.valueOf(dto.getStatus());
                entity.updateStatus(status);
            } catch (IllegalArgumentException e) {
                // Log warning but don't fail the update
                System.err.println("Invalid status provided: " + dto.getStatus());
            }
        }

        // Update attributes
        if (dto.getAttributes() != null) {
            // Clear existing attributes and add new ones
            Map<String, String> currentAttributes = entity.getAttributes();
            currentAttributes.clear();

            for (Map.Entry<String, String> entry : dto.getAttributes().entrySet()) {
                entity.addAttribute(entry.getKey(), entry.getValue());
            }
        }
    }
}