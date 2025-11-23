package com.example.ecommerce.catalog.web.controller;

import com.example.ecommerce.catalog.app.ProductService;
import com.example.ecommerce.catalog.domain.Product;
import com.example.ecommerce.catalog.dto.category.PageResponseDto;
import com.example.ecommerce.catalog.dto.common.ApiResponse;
import com.example.ecommerce.catalog.dto.product.*;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@Tag(name = "Products", description = "Products management APIs")
public class ProductController {
        private final ProductService productService;

        public ProductController(ProductService productService) {
                this.productService = productService;
        }

        @GetMapping
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved categories", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class)))
        public ResponseEntity<ApiResponse<PageResponseDto<PaginatedProductListResponseDto>>> getPaginatedProducts(
                        @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0", name = "page") int page,
                        @Parameter(description = "Page size (max 100)", example = "5") @RequestParam(defaultValue = "5", name = "size") int size) {

                PageResponseDto<PaginatedProductListResponseDto> products = productService.getPaginatedProducts(page,
                                size);
                return ResponseEntity.ok(ApiResponse.success("Products retrieved successfully", products));
        }

        @GetMapping("/{id}")
        public ResponseEntity<ApiResponse<Product>> getProductById(@PathVariable(name = "id") UUID id) {
                Product product = productService.getProductById(id);
                return ResponseEntity.ok(ApiResponse.success("Product retrieved successfully", product));
        }

        @GetMapping("/search")
        public ResponseEntity<ApiResponse<PageResponseDto<SearchProductResponseDto>>> search(
                        @RequestParam(name = "searchQuery") String searchQuery,
                        @RequestParam(name = "inStock") Boolean inStock,
                        @RequestParam(name = "page") int page,
                        @RequestParam(name = "limit") int limit,
                        @Parameter(name = "sort", description = "Sort format: `field`. Examples: `price`, `createdAt`", example = "price", required = true) @RequestParam(name = "sort") String sort) {

                PageResponseDto<SearchProductResponseDto> searchResults = productService.searchProducts(searchQuery,
                                inStock, page, limit, sort);
                return ResponseEntity.ok(ApiResponse.success("Search completed successfully", searchResults));
        }

        @PostMapping
        public ResponseEntity<ApiResponse<CreateProductResponseDto>> createProduct(
                        @Valid @RequestBody CreateProductRequestDto request) {
                CreateProductResponseDto createdProduct = productService.createProduct(
                                request.productName(),
                                request.description(),
                                request.price(),
                                request.brandId(),
                                request.categoryId(),
                                request.color(),
                                request.rating(),
                                request.status(),
                                request.size(),
                                request.color(),
                                request.stockQuantity(),
                                request.weight());

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.created("Product created successfully", createdProduct));
        }

        @PutMapping("/{id}")
        public ResponseEntity<ApiResponse<Product>> upsertProduct(
                        @PathVariable(name = "id") UUID id,
                        @Valid @RequestBody UpdateProductRequestDto request) {
                Product product = productService.putProduct(id, request);
                return ResponseEntity.status(HttpStatus.ACCEPTED)
                                .body(ApiResponse.accepted("Product upserted successfully", product));
        }

        @PatchMapping("/{id}")
        public ResponseEntity<ApiResponse<Product>> updateProduct(
                        @PathVariable(name = "id") UUID id,
                        @Valid @RequestBody PartialProductUpdateRequestDto request) {
                Product product = productService.updateProductPartial(id, request);
                return ResponseEntity.status(HttpStatus.ACCEPTED)
                                .body(ApiResponse.accepted("Product updated successfully", product));
        }

        @PatchMapping("/{id}/stock")
        public ResponseEntity<ApiResponse<Product>> updateProductStock(
                        @PathVariable(name = "id") UUID id, @Valid @RequestBody PriceUpdateRequestDto request)
                        throws BadRequestException {
                Product product = productService.updateProductPrice(id, request.price());
                return ResponseEntity.status(HttpStatus.ACCEPTED)
                                .body(ApiResponse.accepted("Product price updated successfully", product));
        }

        @PatchMapping("/{id}/category")
        public ResponseEntity<ApiResponse<Product>> updateProductCategory(
                        @PathVariable(name = "id") UUID id,
                        @Valid @RequestBody UpdateProductCategoryDto request)
                        throws BadRequestException {
                Product product = productService.updateProductCategory(id, request.categoryId());
                return ResponseEntity.status(HttpStatus.ACCEPTED)
                                .body(ApiResponse.accepted("Product category updated successfully", product));
        }
}
