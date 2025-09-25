package com.example.ecommerce.catalog.web.controller;

import com.example.ecommerce.catalog.app.ProductService;
import com.example.ecommerce.catalog.domain.Product;
import com.example.ecommerce.catalog.web.dto.mapper.ProductMapper;
import com.example.ecommerce.catalog.web.dto.product.CreateProductRequestDTO;
import com.example.ecommerce.catalog.web.dto.product.CreateProductResponseDTO;
import com.example.ecommerce.catalog.web.dto.product.ProductResponseDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // -------------------- CRUD Operations --------------------

    @PostMapping
    public ResponseEntity<CreateProductResponseDTO> createProduct(@Valid @RequestBody CreateProductRequestDTO request) {
        CreateProductResponseDTO createdProduct;
        if (request.getProductImageList() != null && !request.getProductImageList().isEmpty()) {
            createdProduct = productService.create(request.getProductName(), request.getDescription(),
                    request.getPrice(), request.getBrandName(), request.getWeight(),
                    request.getCategoryId(), request.getProductImageList());
        } else {
            createdProduct = productService.create(request.getProductName(), request.getDescription(),
                    request.getPrice(), request.getBrandName(), request.getWeight(),
                    request.getCategoryId());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable UUID id) {
        Product product = productService.getProductById(id);
        ProductResponseDTO response = ProductMapper.toResponseDTO(product);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody CreateProductRequestDTO request) {

        // Note: This would require an update method in ProductService
        // For now, we'll return a method not implemented response
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------- Product Listing and Pagination --------------------

    @GetMapping
    public ResponseEntity<Page<Product>> getPaginatedProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Page<Product> products = productService.getPaginatedProducts(page, size);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        List<ProductResponseDTO> response = ProductMapper.toResponseDTOList(products);
        return ResponseEntity.ok(response);
    }

    // -------------------- Search and Filtering --------------------

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDTO>> searchProducts(@RequestParam String searchTerm) {
        List<Product> products = productService.searchProducts(searchTerm);
        List<ProductResponseDTO> response = ProductMapper.toResponseDTOList(products);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/paginated")
    public ResponseEntity<Page<Product>> searchProductsPaginated(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Product> products = productService.searchProducts(searchTerm, page, size);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/filter/price")
    public ResponseEntity<List<ProductResponseDTO>> getProductsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {

        List<Product> products = productService.findByPriceRange(minPrice, maxPrice);
        List<ProductResponseDTO> response = ProductMapper.toResponseDTOList(products);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter/category/{categoryId}")
    public ResponseEntity<List<ProductResponseDTO>> getProductsByCategory(@PathVariable UUID categoryId) {
        List<Product> products = productService.findByCategory(categoryId);
        List<ProductResponseDTO> response = ProductMapper.toResponseDTOList(products);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter/brand/{brandName}")
    public ResponseEntity<List<ProductResponseDTO>> getProductsByBrand(@PathVariable String brandName) {
        List<Product> products = productService.findByBrand(brandName);
        List<ProductResponseDTO> response = ProductMapper.toResponseDTOList(products);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter/status/{status}")
    public ResponseEntity<List<ProductResponseDTO>> getProductsByStatus(@PathVariable String status) {
        try {
            Product.Status productStatus = Product.Status.valueOf(status);
            List<Product> products = productService.findByStatus(productStatus);
            List<ProductResponseDTO> response = ProductMapper.toResponseDTOList(products);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/active")
    public ResponseEntity<List<ProductResponseDTO>> getActiveProducts() {
        List<Product> products = productService.findActiveProducts();
        List<ProductResponseDTO> response = ProductMapper.toResponseDTOList(products);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/available")
    public ResponseEntity<List<ProductResponseDTO>> getAvailableProducts() {
        List<Product> products = productService.findAvailableProducts();
        List<ProductResponseDTO> response = ProductMapper.toResponseDTOList(products);
        return ResponseEntity.ok(response);
    }

    // -------------------- Stock Management --------------------

    @PutMapping("/{id}/stock")
    public ResponseEntity<ProductResponseDTO> updateStock(
            @PathVariable UUID id,
            @RequestBody Map<String, Long> request) {

        Long stockQuantity = request.get("stockQuantity");
        if (stockQuantity == null) {
            return ResponseEntity.badRequest().build();
        }

        Product product = productService.updateStock(id, stockQuantity);
        ProductResponseDTO response = ProductMapper.toResponseDTO(product);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/stock/add")
    public ResponseEntity<ProductResponseDTO> addStock(
            @PathVariable UUID id,
            @RequestBody Map<String, Long> request) {

        Long quantity = request.get("quantity");
        if (quantity == null) {
            return ResponseEntity.badRequest().build();
        }

        Product product = productService.addStock(id, quantity);
        ProductResponseDTO response = ProductMapper.toResponseDTO(product);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/stock/remove")
    public ResponseEntity<ProductResponseDTO> removeStock(
            @PathVariable UUID id,
            @RequestBody Map<String, Long> request) {

        Long quantity = request.get("quantity");
        if (quantity == null) {
            return ResponseEntity.badRequest().build();
        }

        Product product = productService.removeStock(id, quantity);
        ProductResponseDTO response = ProductMapper.toResponseDTO(product);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/stock")
    public ResponseEntity<Map<String, Long>> getStockQuantity(@PathVariable UUID id) {
        long stockQuantity = productService.getStockQuantity(id);
        return ResponseEntity.ok(Map.of("stockQuantity", stockQuantity));
    }

    @GetMapping("/{id}/stock/in-stock")
    public ResponseEntity<Map<String, Boolean>> isInStock(@PathVariable UUID id) {
        boolean inStock = productService.isInStock(id);
        return ResponseEntity.ok(Map.of("inStock", inStock));
    }

    @GetMapping("/stock/low")
    public ResponseEntity<List<ProductResponseDTO>> getLowStockProducts(@RequestParam long threshold) {
        List<Product> products = productService.findLowStockProducts(threshold);
        List<ProductResponseDTO> response = ProductMapper.toResponseDTOList(products);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stock/out-of-stock")
    public ResponseEntity<List<ProductResponseDTO>> getOutOfStockProducts() {
        List<Product> products = productService.findOutOfStockProducts();
        List<ProductResponseDTO> response = ProductMapper.toResponseDTOList(products);
        return ResponseEntity.ok(response);
    }

    // -------------------- Status Management --------------------

    @PutMapping("/{id}/status")
    public ResponseEntity<ProductResponseDTO> updateStatus(
            @PathVariable UUID id,
            @RequestBody Map<String, String> request) {

        String statusStr = request.get("status");
        if (statusStr == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Product.Status status = Product.Status.valueOf(statusStr);
            Product product = productService.updateStatus(id, status);
            ProductResponseDTO response = ProductMapper.toResponseDTO(product);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<ProductResponseDTO> activateProduct(@PathVariable UUID id) {
        Product product = productService.activateProduct(id);
        ProductResponseDTO response = ProductMapper.toResponseDTO(product);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ProductResponseDTO> deactivateProduct(@PathVariable UUID id) {
        Product product = productService.deactivateProduct(id);
        ProductResponseDTO response = ProductMapper.toResponseDTO(product);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/mark-out-of-stock")
    public ResponseEntity<ProductResponseDTO> markAsOutOfStock(@PathVariable UUID id) {
        Product product = productService.markAsOutOfStock(id);
        ProductResponseDTO response = ProductMapper.toResponseDTO(product);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/active")
    public ResponseEntity<Map<String, Boolean>> isActive(@PathVariable UUID id) {
        boolean active = productService.isActive(id);
        return ResponseEntity.ok(Map.of("active", active));
    }

    @GetMapping("/{id}/available")
    public ResponseEntity<Map<String, Boolean>> isAvailable(@PathVariable UUID id) {
        boolean available = productService.isAvailable(id);
        return ResponseEntity.ok(Map.of("available", available));
    }

    // -------------------- Product Analytics and Statistics --------------------

    @GetMapping("/top-rated")
    public ResponseEntity<List<ProductResponseDTO>> getTopRatedProducts(@RequestParam(defaultValue = "10") int limit) {
        List<Product> products = productService.getTopRatedProducts(limit);
        List<ProductResponseDTO> response = ProductMapper.toResponseDTOList(products);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/newest")
    public ResponseEntity<List<ProductResponseDTO>> getNewestProducts(@RequestParam(defaultValue = "10") int limit) {
        List<Product> products = productService.getNewestProducts(limit);
        List<ProductResponseDTO> response = ProductMapper.toResponseDTOList(products);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{categoryId}/count")
    public ResponseEntity<Map<String, Long>> countProductsByCategory(@PathVariable UUID categoryId) {
        long count = productService.countProductsByCategory(categoryId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @GetMapping("/category/{categoryId}/count/active")
    public ResponseEntity<Map<String, Long>> countActiveProductsByCategory(@PathVariable UUID categoryId) {
        long count = productService.countActiveProductsByCategory(categoryId);
        return ResponseEntity.ok(Map.of("activeCount", count));
    }

    // -------------------- Bulk Operations --------------------
    // Note: These would require additional service methods to be implemented

    @PostMapping("/bulk/activate")
    public ResponseEntity<Map<String, String>> bulkActivateProducts(@RequestBody List<UUID> productIds) {
        // This would require a bulk activate method in ProductService
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(Map.of("message", "Bulk activate not yet implemented"));
    }

    @PostMapping("/bulk/deactivate")
    public ResponseEntity<Map<String, String>> bulkDeactivateProducts(@RequestBody List<UUID> productIds) {
        // This would require a bulk deactivate method in ProductService
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(Map.of("message", "Bulk deactivate not yet implemented"));
    }

    @PostMapping("/bulk/update-stock")
    public ResponseEntity<Map<String, String>> bulkUpdateStock(
            @RequestBody Map<UUID, Long> stockUpdates) {
        // This would require a bulk stock update method in ProductService
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(Map.of("message", "Bulk stock update not yet implemented"));
    }

    @DeleteMapping("/bulk")
    public ResponseEntity<Map<String, String>> bulkDeleteProducts(@RequestBody List<UUID> productIds) {
        // This would require a bulk delete method in ProductService
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(Map.of("message", "Bulk delete not yet implemented"));
    }
}
