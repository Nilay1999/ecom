package com.example.ecommerce.catalog.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.ecommerce.catalog.app.ProductImageService;
import com.example.ecommerce.catalog.dto.image.ImageOrderRequestDto;
import com.example.ecommerce.catalog.dto.image.ProductImageRequestDto;
import com.example.ecommerce.catalog.dto.image.ProductImageResponseDto;

@RestController
@RequestMapping("product-images")
@RequiredArgsConstructor
@Tag(name = "Product Images", description = "APIs for managing product images")
public class ProductImageController {

    private final ProductImageService productImageService;

    @PostMapping
    @Operation(summary = "Add Image to Product", description = "Add a new image to a product")
    public ResponseEntity<ProductImageResponseDto> addImageToProduct(
            @Valid @RequestBody ProductImageRequestDto request) {

        ProductImageResponseDto createdImage = productImageService.addImageToProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdImage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Product Images", description = "Retrieve all images for a specific product")
    public ResponseEntity<List<ProductImageResponseDto>> getProductImages(@PathVariable("productId") UUID id) {
        List<ProductImageResponseDto> images = productImageService.getProductImages(id);
        return ResponseEntity.ok(images);
    }

    @PatchMapping("/{imageId}/primary")
    @Operation(summary = "Set Primary Image", description = "Mark an image as primary for the product")
    public ResponseEntity<List<ProductImageResponseDto>> setPrimaryImage(
            @PathVariable("productId") UUID productId,
            @PathVariable("imageId") UUID imageId) {

        List<ProductImageResponseDto> updatedImages = productImageService.setPrimaryImage(productId, imageId);
        return ResponseEntity.ok(updatedImages);
    }

    @PutMapping("/order")
    @Operation(summary = "Reorder Images", description = "Update the display order of multiple images")
    public ResponseEntity<List<ProductImageResponseDto>> reorderImages(
            @PathVariable("productId") UUID productId,
            @Valid @RequestBody ImageOrderRequestDto request) {

        List<ProductImageResponseDto> updatedImages = productImageService.reorderImages(productId, request);
        return ResponseEntity.ok(updatedImages);
    }

    @DeleteMapping("/{imageId}")
    @Operation(summary = "Delete Product Image", description = "Delete a specific product image")
    public ResponseEntity<Void> deleteProductImage(
            @PathVariable("productId") UUID productId,
            @PathVariable("imageId") UUID imageId) {

        productImageService.deleteProductImage(productId, imageId);
        return ResponseEntity.noContent().build();
    }
}
