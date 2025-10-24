package com.example.ecommerce.catalog.app;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.ecommerce.catalog.domain.Product;
import com.example.ecommerce.catalog.domain.ProductImage;
import com.example.ecommerce.catalog.dto.image.ImageOrderRequestDto;
import com.example.ecommerce.catalog.dto.image.ProductImageRequestDto;
import com.example.ecommerce.catalog.dto.image.ProductImageResponseDto;
import com.example.ecommerce.catalog.infra.ProductImageRepository;
import com.example.ecommerce.catalog.infra.ProductRepository;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductImageService {
    private final ProductImageRepository imageRepository;
    private final ProductRepository productRepository;

    public ProductImageResponseDto addImageToProduct(ProductImageRequestDto request) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + request.productId()));

        ProductImage image = new ProductImage.Builder()
                .imageUrl(request.imageUrl())
                .product(product)
                .isPrimary(request.isPrimary() != null ? request.isPrimary():false)
                .displayOrder(request.displayOrder() != null ? request.displayOrder():0)
                .altText(request.altText())
                .imageType(request.imageType() != null ? request.imageType():"MAIN")
                .fileSize(request.fileSize())
                .mimeType(request.mimeType())
                .dimensions(request.width(), request.height())
                .build();

        ProductImage savedImage = imageRepository.save(image);
        log.info("Image added successfully to product: {}", request.productId());

        return mapToDto(savedImage);
    }

    @Transactional(readOnly = true)
    public List<ProductImageResponseDto> getProductImages(UUID productId) {
        if (!productRepository.existsById(productId)) {
            throw new EntityNotFoundException("Product not found with id: " + productId);
        }

        List<ProductImage> images = imageRepository.findByProductIdOrderByDisplayOrderAsc(productId);

        return images.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<ProductImageResponseDto> setPrimaryImage(UUID productId, UUID imageId) {
        if (!productRepository.existsById(productId)) {
            throw new EntityNotFoundException("Product not found with id: " + productId);
        }

        ProductImage image = imageRepository.findByIdAndProductId(imageId, productId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Image not found with id: " + imageId + " for product: " + productId));

        image.markAsPrimary();
        imageRepository.save(image);

        log.info("Image {} set as primary for product: {}", imageId, productId);

        return getProductImages(productId);
    }

    public List<ProductImageResponseDto> reorderImages(UUID productId, ImageOrderRequestDto request) {
        if (!productRepository.existsById(productId)) {
            throw new EntityNotFoundException("Product not found with id: " + productId);
        }

        Map<UUID, Integer> orderMap = request.getImageOrders().stream()
                .collect(Collectors.toMap(
                        ImageOrderRequestDto.ImageOrder::getImageId,
                        ImageOrderRequestDto.ImageOrder::getDisplayOrder
                ));

        List<ProductImage> images = imageRepository.findAllById(orderMap.keySet());

        for (ProductImage image : images) {
            if (!image.getProduct().getId().equals(productId)) {
                throw new IllegalArgumentException(
                        "Image " + image.getId() + " does not belong to product: " + productId);
            }
        }

        images.forEach(image -> {
            Integer newOrder = orderMap.get(image.getId());
            if (newOrder != null) {
                image.updateDisplayOrder(newOrder);
            }
        });

        imageRepository.saveAll(images);
        log.info("Images reordered successfully for product: {}", productId);

        return getProductImages(productId);
    }

    public void deleteProductImage(UUID productId, UUID imageId) {
        ProductImage image = imageRepository.findByIdAndProductId(imageId, productId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Image not found with id: " + imageId + " for product: " + productId));

        imageRepository.delete(image);
        log.info("Image {} deleted successfully from product: {}", imageId, productId);
    }

    private ProductImageResponseDto mapToDto(ProductImage image) {
        return ProductImageResponseDto.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .isPrimary(image.isPrimary())
                .displayOrder(image.getDisplayOrder())
                .altText(image.getAltText())
                .imageType(image.getImageType())
                .dimensions(image.getImageDimensions())
                .fileSize(image.getFileSize())
                .mimeType(image.getMimeType())
                .width(image.getWidth())
                .height(image.getHeight())
                .aspectRatio(image.getAspectRatio())
                .createdAt(image.getCreatedAt())
                .updatedAt(image.getUpdatedAt())
                .build();
    }
}
