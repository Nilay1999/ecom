package com.example.ecommerce.catalog.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.coyote.BadRequestException;
import com.example.ecommerce.catalog.app.ProductService;
import com.example.ecommerce.catalog.domain.Product;
import com.example.ecommerce.catalog.dto.category.PageResponseDto;
import com.example.ecommerce.catalog.dto.product.*;

@RestController
@RequestMapping("/products")
@Tag(name = "Products", description = "Products management APIs")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "Get paginated list of categories", description = "Returns a page of " + "categories with "
            + "configurable page number and size.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved categories", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class)))
    public ResponseEntity<Page<Product>> getPaginatedProducts(
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0", name = "page") int page,
            @Parameter(description = "Page size (max 100)", example = "5") @RequestParam(defaultValue = "5", name = "size") int size) {

        Page<Product> products = productService.getPaginatedProducts(page, size);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable(name = "id") UUID id) {
        Product product = productService.getProductById(id);
        return new ResponseEntity<>(product, HttpStatus.FOUND);
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponseDto<SearchProductResponseDto>> search(
            @RequestParam(name = "searchQuery") String searchQuery, @RequestParam(name = "inStock") Boolean inStock,
            @RequestParam(name = "page") int page, @RequestParam(name = "limit") int limit,
            @RequestParam(name = "sort") String sort) {

        return ResponseEntity.ok(productService.searchProducts(searchQuery, inStock, page, limit, sort));
    }

    @PostMapping
    public ResponseEntity<CreateProductResponseDto> createProduct(@Valid @RequestBody CreateProductRequestDto request) {
        CreateProductResponseDto createdProduct = productService.createProduct(request.productName(),
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

        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> upsertProduct(@PathVariable(name = "id") UUID id,
                                                 @Valid @RequestBody UpdateProductRequestDto request) {
        Product product = productService.putProduct(id, request);
        return new ResponseEntity<>(product, HttpStatus.ACCEPTED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable(name = "id") UUID id,
                                                 @Valid @RequestBody PartialProductUpdateRequestDto request) {
        Product product = productService.updateProductPartial(id, request);
        return new ResponseEntity<>(product, HttpStatus.ACCEPTED);
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<Product> updateProductStock(@PathVariable(name = "id") UUID id,
                                                      @Valid @RequestBody PriceUpdateRequestDto request) throws BadRequestException {
        Product product = productService.updateProductPrice(id, request.price());
        return new ResponseEntity<>(product, HttpStatus.ACCEPTED);
    }

    @PatchMapping("/{id}/category")
    public ResponseEntity<Product> updateProductCategory(@PathVariable(name = "id") UUID id,
                                                         @Valid @RequestBody UpdateProductCategoryDto request) throws BadRequestException {
        Product product = productService.updateProductCategory(id, request.categoryId());
        return new ResponseEntity<>(product, HttpStatus.ACCEPTED);
    }
}
