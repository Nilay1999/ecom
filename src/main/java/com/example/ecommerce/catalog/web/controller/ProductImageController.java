package com.example.ecommerce.catalog.web.controller;

import com.example.ecommerce.catalog.app.ProductImageService;
import com.example.ecommerce.catalog.domain.ProductImage;
import com.example.ecommerce.catalog.web.dto.mapper.ProductImageMapper;
import com.example.ecommerce.catalog.web.dto.product.ProductImageRequestDTO;
import com.example.ecommerce.catalog.web.dto.product.ProductImageResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/product-images")
public class ProductImageController {

    private final ProductImageService productImageService;

    public ProductImageController(ProductImageService productImageService) {
        this.productImageService = productImageService;
    }

    // -------------------- CRUD Operations --------------------

    @PostMapping
    public ResponseEntity<ProductImageResponseDTO> createProductImage(
            @Valid @RequestBody ProductImageRequestDTO request) {
        ProductImage productImage = productImageService.create(
                request.getProductId(),
                request.getImageUrl(),
                request.getIsPrimary(),
                request.getDisplayOrder(),
                request.getAltText());

        ProductImageResponseDTO response = ProductImageMapper.toResponseDTO(productImage);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductImageResponseDTO> getProductImage(@PathVariable UUID id) {
        ProductImage productImage = productImageService.findById(id);
        ProductImageResponseDTO response = ProductImageMapper.toResponseDTO(productImage);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductImageResponseDTO> updateProductImage(
            @PathVariable UUID id,
            @Valid @RequestBody ProductImageRequestDTO request) {

        ProductImage productImage = productImageService.update(
                id,
                request.getImageUrl(),
                request.getIsPrimary(),
                request.getDisplayOrder(),
                request.getAltText());

        ProductImageResponseDTO response = ProductImageMapper.toResponseDTO(productImage);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductImage(@PathVariable UUID id) {
        productImageService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------- Product Image Management --------------------

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductImageResponseDTO>> getProductImages(@PathVariable UUID productId) {
        List<ProductImage> productImages = productImageService.getProductImages(productId);
        List<ProductImageResponseDTO> response = ProductImageMapper.toResponseDTOList(productImages);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/{productId}/primary")
    public ResponseEntity<ProductImageResponseDTO> getPrimaryImage(@PathVariable UUID productId) {
        Optional<ProductImage> primaryImage = productImageService.getPrimaryImage(productId);

        if (primaryImage.isPresent()) {
            ProductImageResponseDTO response = ProductImageMapper.toResponseDTO(primaryImage.get());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // -------------------- Primary Image Management --------------------

    @PutMapping("/{id}/set-primary")
    public ResponseEntity<ProductImageResponseDTO> setPrimaryImage(@PathVariable UUID id) {
        ProductImage productImage = productImageService.setPrimaryImage(id);
        ProductImageResponseDTO response = ProductImageMapper.toResponseDTO(productImage);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/remove-primary")
    public ResponseEntity<ProductImageResponseDTO> removePrimaryStatus(@PathVariable UUID id) {
        productImageService.removePrimaryStatus(id);
        ProductImage productImage = productImageService.findById(id);
        ProductImageResponseDTO response = ProductImageMapper.toResponseDTO(productImage);
        return ResponseEntity.ok(response);
    }

    // -------------------- Image Ordering --------------------

    @PutMapping("/product/{productId}/reorder")
    public ResponseEntity<List<ProductImageResponseDTO>> reorderImages(
            @PathVariable UUID productId,
            @RequestBody List<UUID> imageIds) {

        List<ProductImage> reorderedImages = productImageService.reorderImages(productId, imageIds);
        List<ProductImageResponseDTO> response = ProductImageMapper.toResponseDTOList(reorderedImages);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/move-up")
    public ResponseEntity<ProductImageResponseDTO> moveImageUp(@PathVariable UUID id) {
        ProductImage productImage = productImageService.moveImageUp(id);
        ProductImageResponseDTO response = ProductImageMapper.toResponseDTO(productImage);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/move-down")
    public ResponseEntity<ProductImageResponseDTO> moveImageDown(@PathVariable UUID id) {
        ProductImage productImage = productImageService.moveImageDown(id);
        ProductImageResponseDTO response = ProductImageMapper.toResponseDTO(productImage);
        return ResponseEntity.ok(response);
    }

    // -------------------- Search and Filtering --------------------

    @GetMapping("/search")
    public ResponseEntity<List<ProductImageResponseDTO>> searchByAltText(@RequestParam String altText) {
        List<ProductImage> productImages = productImageService.searchByAltText(altText);
        List<ProductImageResponseDTO> response = ProductImageMapper.toResponseDTOList(productImages);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/{productId}/display-order-range")
    public ResponseEntity<List<ProductImageResponseDTO>> getImagesByDisplayOrderRange(
            @PathVariable UUID productId,
            @RequestParam Integer minOrder,
            @RequestParam Integer maxOrder) {

        List<ProductImage> productImages = productImageService.getImagesByDisplayOrderRange(productId, minOrder,
                maxOrder);
        List<ProductImageResponseDTO> response = ProductImageMapper.toResponseDTOList(productImages);
        return ResponseEntity.ok(response);
    }

    // -------------------- Validation and Utility --------------------

    @GetMapping("/validate-url")
    public ResponseEntity<Map<String, Boolean>> validateImageUrl(@RequestParam String imageUrl) {
        boolean isValid = productImageService.isValidImageUrl(imageUrl);
        return ResponseEntity.ok(Map.of("isValid", isValid));
    }

    @GetMapping("/product/{productId}/has-primary")
    public ResponseEntity<Map<String, Boolean>> hasPrimaryImage(@PathVariable UUID productId) {
        boolean hasPrimary = productImageService.hasPrimaryImage(productId);
        return ResponseEntity.ok(Map.of("hasPrimary", hasPrimary));
    }

    @GetMapping("/product/{productId}/count")
    public ResponseEntity<Map<String, Long>> getImageCount(@PathVariable UUID productId) {
        long count = productImageService.getImageCount(productId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    // -------------------- Bulk Operations --------------------

    @PostMapping("/product/{productId}/bulk")
    public ResponseEntity<List<ProductImageResponseDTO>> createMultipleImages(
            @PathVariable UUID productId,
            @Valid @RequestBody List<ProductImageService.ImageCreationRequest> requests) {

        List<ProductImage> productImages = productImageService.createMultipleImages(productId, requests);
        List<ProductImageResponseDTO> response = ProductImageMapper.toResponseDTOList(productImages);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/product/{productId}/all")
    public ResponseEntity<Void> deleteAllProductImages(@PathVariable UUID productId) {
        productImageService.deleteAllProductImages(productId);
        return ResponseEntity.noContent().build();
    }
}