package com.example.ecommerce.catalog.web.dto.mapper;

import com.example.ecommerce.catalog.domain.Category;
import com.example.ecommerce.catalog.domain.Product;
import com.example.ecommerce.catalog.domain.ProductImage;
import com.example.ecommerce.catalog.domain.ProductVariant;
import com.example.ecommerce.catalog.web.dto.category.CategoryResponseDTO;
import com.example.ecommerce.catalog.web.dto.category.CreateCategoryRequest;
import com.example.ecommerce.catalog.web.dto.product.*;

import java.util.List;

/**
 * Central DTO mapping utility that delegates to specific mappers.
 * Provides a single entry point for all DTO mapping operations.
 */
public class DTOMapper {

    // Product mapping methods
    public static Product toEntity(CreateProductRequestDTO dto, Category category) {
        return ProductMapper.toEntity(dto, category);
    }

    public static CreateProductResponseDTO toCreateResponseDTO(Product entity) {
        return ProductMapper.toCreateResponseDTO(entity);
    }

    public static ProductResponseDTO toProductResponseDTO(Product entity) {
        return ProductMapper.toResponseDTO(entity);
    }

    public static List<ProductResponseDTO> toProductResponseDTOList(List<Product> entities) {
        return ProductMapper.toResponseDTOList(entities);
    }

    public static void updateProduct(Product entity, CreateProductRequestDTO dto, Category category) {
        ProductMapper.updateEntityFromDTO(entity, dto, category);
    }

    public static void addImagesToProduct(Product product, List<ProductImagePayload> imagePayloads) {
        ProductMapper.addImagesToProduct(product, imagePayloads);
    }

    // Category mapping methods
    public static Category toEntity(CreateCategoryRequest dto, Category parentCategory) {
        return CategoryMapper.toEntity(dto, parentCategory);
    }

    public static CategoryResponseDTO toCategoryResponseDTO(Category entity) {
        return CategoryMapper.toResponseDTO(entity);
    }

    public static List<CategoryResponseDTO> toCategoryResponseDTOList(List<Category> entities) {
        return CategoryMapper.toResponseDTOList(entities);
    }

    public static void updateCategory(Category entity, CreateCategoryRequest dto, Category parentCategory) {
        CategoryMapper.updateEntityFromDTO(entity, dto, parentCategory);
    }

    public static CategoryResponseDTO.CategorySummary toCategorySummary(Category entity) {
        return CategoryMapper.toCategorySummary(entity);
    }

    // ProductImage mapping methods
    public static ProductImage toEntity(ProductImageRequestDTO dto, Product product) {
        return ProductImageMapper.toEntity(dto, product);
    }

    public static ProductImage toEntity(ProductImagePayload payload, Product product) {
        return ProductImageMapper.toEntity(payload, product);
    }

    public static ProductImageResponseDTO toProductImageResponseDTO(ProductImage entity) {
        return ProductImageMapper.toResponseDTO(entity);
    }

    public static List<ProductImage> toProductImageEntityList(List<ProductImagePayload> payloads, Product product) {
        return ProductImageMapper.toEntityList(payloads, product);
    }

    public static List<ProductImageResponseDTO> toProductImageResponseDTOList(List<ProductImage> entities) {
        return ProductImageMapper.toResponseDTOList(entities);
    }

    public static void updateProductImage(ProductImage entity, ProductImageRequestDTO dto) {
        ProductImageMapper.updateEntityFromDTO(entity, dto);
    }

    // ProductVariant mapping methods
    public static ProductVariant toEntity(ProductVariantRequestDTO dto, Product product) {
        return ProductVariantMapper.toEntity(dto, product);
    }

    public static ProductVariantResponseDTO toProductVariantResponseDTO(ProductVariant entity) {
        return ProductVariantMapper.toResponseDTO(entity);
    }

    public static List<ProductVariant> toProductVariantEntityList(List<ProductVariantRequestDTO> dtos,
            Product product) {
        return ProductVariantMapper.toEntityList(dtos, product);
    }

    public static List<ProductVariantResponseDTO> toProductVariantResponseDTOList(List<ProductVariant> entities) {
        return ProductVariantMapper.toResponseDTOList(entities);
    }

    public static void updateProductVariant(ProductVariant entity, ProductVariantRequestDTO dto) {
        ProductVariantMapper.updateEntityFromDTO(entity, dto);
    }

    // Utility methods for complex mappings
    public static Product createProductWithImagesAndVariants(CreateProductRequestDTO dto, Category category) {
        Product product = toEntity(dto, category);

        if (dto.getProductImageList() != null && !dto.getProductImageList().isEmpty()) {
            addImagesToProduct(product, dto.getProductImageList());
        }

        return product;
    }

    public static ProductResponseDTO toFullProductResponseDTO(Product entity) {
        return toProductResponseDTO(entity);
    }

    // Bidirectional mapping support
    public static void syncProductWithDTO(Product product, CreateProductRequestDTO dto, Category category) {
        updateProduct(product, dto, category);

        // Handle images - this is a simplified approach
        // In a real implementation, you might want more sophisticated image
        // synchronization
        if (dto.getProductImageList() != null) {
            product.getProductImages().clear();
            addImagesToProduct(product, dto.getProductImageList());
        }
    }
}