package com.example.ecommerce.catalog.web.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.ecommerce.catalog.app.ProductImageService;
import com.example.ecommerce.catalog.dto.common.ApiResponse;
import com.example.ecommerce.catalog.dto.image.ImageOrderRequestDto;
import com.example.ecommerce.catalog.dto.image.ProductImageRequestDto;
import com.example.ecommerce.catalog.dto.image.ProductImageResponseDto;
import com.example.ecommerce.common.config.S3Service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("product-images")
@RequiredArgsConstructor
@Tag(name = "Product Images", description = "APIs for managing product images")
public class ProductImageController {

    private final ProductImageService productImageService;
    private final S3Service s3Service;

    @PostMapping(value = "/upload/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload product image")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadFile(
            @Parameter(description = "Product ID", required = true) @PathVariable("productId") UUID productId,

            @Parameter(description = "Image file to upload", required = true, schema = @Schema(type = "string", format = "binary")) @RequestPart("file") MultipartFile file)
            throws IOException {

        Map<String, String> response = s3Service.generateProductImageUploadUrl(file, productId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Image uploaded successfully", response));
    }

    @PostMapping
    @Operation(summary = "Add Image to Product", description = "Add a new image to a product")
    public ResponseEntity<ApiResponse<ProductImageResponseDto>> addImageToProduct(
            @Valid @RequestBody ProductImageRequestDto request) {

        ProductImageResponseDto createdImage = productImageService.addImageToProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Image added to product successfully", createdImage));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Product Images", description = "Retrieve all images for a specific product")
    public ResponseEntity<ApiResponse<List<ProductImageResponseDto>>> getProductImages(
            @PathVariable("productId") UUID id) {
        List<ProductImageResponseDto> images = productImageService.getProductImages(id);
        return ResponseEntity.ok(ApiResponse.success("Product images retrieved successfully", images));
    }

    @PatchMapping("/{imageId}/primary")
    @Operation(summary = "Set Primary Image", description = "Mark an image as primary for the product")
    public ResponseEntity<ApiResponse<List<ProductImageResponseDto>>> setPrimaryImage(
            @PathVariable("productId") UUID productId,
            @PathVariable("imageId") UUID imageId) {

        List<ProductImageResponseDto> updatedImages = productImageService.setPrimaryImage(productId, imageId);
        return ResponseEntity.ok(ApiResponse.accepted("Primary image set successfully", updatedImages));
    }

    @PutMapping("/order")
    @Operation(summary = "Reorder Images", description = "Update the display order of multiple images")
    public ResponseEntity<ApiResponse<List<ProductImageResponseDto>>> reorderImages(
            @PathVariable("productId") UUID productId,
            @Valid @RequestBody ImageOrderRequestDto request) {

        List<ProductImageResponseDto> updatedImages = productImageService.reorderImages(productId, request);
        return ResponseEntity.ok(ApiResponse.accepted("Images reordered successfully", updatedImages));
    }

    @DeleteMapping("/{imageId}")
    @Operation(summary = "Delete Product Image", description = "Delete a specific product image")
    public ResponseEntity<ApiResponse<Void>> deleteProductImage(
            @PathVariable("productId") UUID productId,
            @PathVariable("imageId") UUID imageId) {

        productImageService.deleteProductImage(productId, imageId);
        return ResponseEntity.ok(ApiResponse.success("Product image deleted successfully", null));
    }
}
