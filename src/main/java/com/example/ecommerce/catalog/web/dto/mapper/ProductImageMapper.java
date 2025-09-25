package com.example.ecommerce.catalog.web.dto.mapper;

import com.example.ecommerce.catalog.domain.Product;
import com.example.ecommerce.catalog.domain.ProductImage;
import com.example.ecommerce.catalog.web.dto.product.ProductImageRequestDTO;
import com.example.ecommerce.catalog.web.dto.product.ProductImageResponseDTO;
import com.example.ecommerce.catalog.web.dto.product.ProductImagePayload;

import java.util.List;
import java.util.stream.Collectors;

public class ProductImageMapper {

    public static ProductImage toEntity(ProductImageRequestDTO dto, Product product) {
        if (dto == null) {
            return null;
        }

        return new ProductImage.Builder()
                .product(product)
                .imageUrl(dto.getImageUrl())
                .isPrimary(dto.getIsPrimary() != null ? dto.getIsPrimary() : false)
                .displayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0)
                .altText(dto.getAltText())
                .build();
    }

    public static ProductImage toEntity(ProductImagePayload payload, Product product) {
        if (payload == null) {
            return null;
        }

        return new ProductImage.Builder()
                .product(product)
                .imageUrl(payload.getImageUrl())
                .isPrimary(payload.getIsPrimary() != null ? payload.getIsPrimary() : false)
                .displayOrder(payload.getDisplayOrder() != null ? payload.getDisplayOrder() : 0)
                .altText(payload.getAltText())
                .build();
    }

    public static ProductImageResponseDTO toResponseDTO(ProductImage entity) {
        if (entity == null) {
            return null;
        }

        return new ProductImageResponseDTO(
                entity.getId(),
                entity.getProduct().getId(),
                entity.getImageUrl(),
                entity.isPrimary(),
                entity.getDisplayOrder(),
                entity.getAltText(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public static List<ProductImage> toEntityList(List<ProductImagePayload> payloads, Product product) {
        if (payloads == null) {
            return List.of();
        }

        return payloads.stream()
                .map(payload -> toEntity(payload, product))
                .collect(Collectors.toList());
    }

    public static List<ProductImageResponseDTO> toResponseDTOList(List<ProductImage> entities) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(ProductImageMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public static void updateEntityFromDTO(ProductImage entity, ProductImageRequestDTO dto) {
        if (entity == null || dto == null) {
            return;
        }

        if (dto.getImageUrl() != null) {
            entity.updateImageUrl(dto.getImageUrl());
        }

        if (dto.getIsPrimary() != null) {
            if (dto.getIsPrimary()) {
                entity.markAsPrimary();
            } else {
                entity.markAsSecondary();
            }
        }

        if (dto.getDisplayOrder() != null) {
            entity.updateDisplayOrder(dto.getDisplayOrder());
        }

        if (dto.getAltText() != null) {
            entity.updateAltText(dto.getAltText());
        }
    }
}