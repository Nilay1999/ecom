package com.example.ecommerce.catalog.web.controller;

import com.example.ecommerce.catalog.app.ProductVariantService;
import com.example.ecommerce.catalog.domain.ProductVariant;
import com.example.ecommerce.catalog.web.dto.mapper.ProductVariantMapper;
import com.example.ecommerce.catalog.web.dto.product.ProductVariantRequestDTO;
import com.example.ecommerce.catalog.web.dto.product.ProductVariantResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/product-variants")
public class ProductVariantController {

    private final ProductVariantService productVariantService;

    public ProductVariantController(ProductVariantService productVariantService) {
        this.productVariantService = productVariantService;
    }

    // -------------------- CRUD Operations --------------------

    @PostMapping
    public ResponseEntity<ProductVariantResponseDTO> createProductVariant(
            @Valid @RequestBody ProductVariantRequestDTO request) {
        ProductVariant.Status status = ProductVariant.Status.valueOf(request.getStatus());

        ProductVariant productVariant = productVariantService.create(
                request.getProductId(),
                request.getVariantName(),
                request.getPriceOverride(),
                request.getStockQuantity(),
                request.getAttributes(),
                request.getSku());

        // Update status if different from default
        if (status != ProductVariant.Status.ACTIVE) {
            productVariant = productVariantService.updateStatus(productVariant.getId(), status);
        }

        ProductVariantResponseDTO response = ProductVariantMapper.toResponseDTO(productVariant);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductVariantResponseDTO> getProductVariant(@PathVariable UUID id) {
        ProductVariant productVariant = productVariantService.findById(id);
        ProductVariantResponseDTO response = ProductVariantMapper.toResponseDTO(productVariant);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductVariantResponseDTO> updateProductVariant(
            @PathVariable UUID id,
            @Valid @RequestBody ProductVariantRequestDTO request) {

        ProductVariant.Status status = ProductVariant.Status.valueOf(request.getStatus());

        ProductVariant productVariant = productVariantService.update(
                id,
                request.getVariantName(),
                request.getPriceOverride(),
                status,
                request.getSku());

        // Update attributes separately
        if (request.getAttributes() != null && !request.getAttributes().isEmpty()) {
            productVariant = productVariantService.updateAttributes(id, request.getAttributes());
        }

        // Update stock if provided
        if (request.getStockQuantity() != null) {
            productVariant = productVariantService.updateStock(id, request.getStockQuantity());
        }

        ProductVariantResponseDTO response = ProductVariantMapper.toResponseDTO(productVariant);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductVariant(@PathVariable UUID id) {
        productVariantService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------- Product Variant Management --------------------

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductVariantResponseDTO>> getProductVariants(@PathVariable UUID productId) {
        List<ProductVariant> productVariants = productVariantService.getProductVariants(productId);
        List<ProductVariantResponseDTO> response = ProductVariantMapper.toResponseDTOList(productVariants);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/{productId}/active")
    public ResponseEntity<List<ProductVariantResponseDTO>> getActiveVariants(@PathVariable UUID productId) {
        List<ProductVariant> activeVariants = productVariantService.getActiveVariants(productId);
        List<ProductVariantResponseDTO> response = ProductVariantMapper.toResponseDTOList(activeVariants);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/{productId}/available")
    public ResponseEntity<List<ProductVariantResponseDTO>> getAvailableVariants(@PathVariable UUID productId) {
        List<ProductVariant> availableVariants = productVariantService.getAvailableVariants(productId);
        List<ProductVariantResponseDTO> response = ProductVariantMapper.toResponseDTOList(availableVariants);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/{productId}/variant/{variantName}")
    public ResponseEntity<ProductVariantResponseDTO> getVariantByName(
            @PathVariable UUID productId,
            @PathVariable String variantName) {

        Optional<ProductVariant> variant = productVariantService.findByProductAndVariantName(productId, variantName);

        if (variant.isPresent()) {
            ProductVariantResponseDTO response = ProductVariantMapper.toResponseDTO(variant.get());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductVariantResponseDTO> getVariantBySku(@PathVariable String sku) {
        Optional<ProductVariant> variant = productVariantService.findBySku(sku);

        if (variant.isPresent()) {
            ProductVariantResponseDTO response = ProductVariantMapper.toResponseDTO(variant.get());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // -------------------- Stock Management --------------------

    @PutMapping("/{id}/stock")
    public ResponseEntity<ProductVariantResponseDTO> updateStock(
            @PathVariable UUID id,
            @RequestBody Map<String, Long> request) {

        Long stockQuantity = request.get("stockQuantity");
        if (stockQuantity == null) {
            return ResponseEntity.badRequest().build();
        }

        ProductVariant productVariant = productVariantService.updateStock(id, stockQuantity);
        ProductVariantResponseDTO response = ProductVariantMapper.toResponseDTO(productVariant);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/stock/add")
    public ResponseEntity<ProductVariantResponseDTO> addStock(
            @PathVariable UUID id,
            @RequestBody Map<String, Long> request) {

        Long quantity = request.get("quantity");
        if (quantity == null) {
            return ResponseEntity.badRequest().build();
        }

        ProductVariant productVariant = productVariantService.addStock(id, quantity);
        ProductVariantResponseDTO response = ProductVariantMapper.toResponseDTO(productVariant);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/stock/remove")
    public ResponseEntity<ProductVariantResponseDTO> removeStock(
            @PathVariable UUID id,
            @RequestBody Map<String, Long> request) {

        Long quantity = request.get("quantity");
        if (quantity == null) {
            return ResponseEntity.badRequest().build();
        }

        ProductVariant productVariant = productVariantService.removeStock(id, quantity);
        ProductVariantResponseDTO response = ProductVariantMapper.toResponseDTO(productVariant);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/{productId}/stock/total")
    public ResponseEntity<Map<String, Long>> getTotalStock(@PathVariable UUID productId) {
        long totalStock = productVariantService.getTotalStock(productId);
        return ResponseEntity.ok(Map.of("totalStock", totalStock));
    }

    @GetMapping("/product/{productId}/stock/available")
    public ResponseEntity<Map<String, Long>> getTotalAvailableStock(@PathVariable UUID productId) {
        long availableStock = productVariantService.getTotalAvailableStock(productId);
        return ResponseEntity.ok(Map.of("availableStock", availableStock));
    }

    @GetMapping("/stock/low")
    public ResponseEntity<List<ProductVariantResponseDTO>> getLowStockVariants(@RequestParam long threshold) {
        List<ProductVariant> lowStockVariants = productVariantService.getLowStockVariants(threshold);
        List<ProductVariantResponseDTO> response = ProductVariantMapper.toResponseDTOList(lowStockVariants);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stock/out-of-stock")
    public ResponseEntity<List<ProductVariantResponseDTO>> getOutOfStockVariants() {
        List<ProductVariant> outOfStockVariants = productVariantService.getOutOfStockVariants();
        List<ProductVariantResponseDTO> response = ProductVariantMapper.toResponseDTOList(outOfStockVariants);
        return ResponseEntity.ok(response);
    }

    // -------------------- Price Management --------------------

    @PutMapping("/{id}/price")
    public ResponseEntity<ProductVariantResponseDTO> updatePriceOverride(
            @PathVariable UUID id,
            @RequestBody Map<String, BigDecimal> request) {

        BigDecimal priceOverride = request.get("priceOverride");
        if (priceOverride == null) {
            return ResponseEntity.badRequest().build();
        }

        ProductVariant productVariant = productVariantService.updatePriceOverride(id, priceOverride);
        ProductVariantResponseDTO response = ProductVariantMapper.toResponseDTO(productVariant);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/price")
    public ResponseEntity<ProductVariantResponseDTO> removePriceOverride(@PathVariable UUID id) {
        ProductVariant productVariant = productVariantService.removePriceOverride(id);
        ProductVariantResponseDTO response = ProductVariantMapper.toResponseDTO(productVariant);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/price/with-override")
    public ResponseEntity<List<ProductVariantResponseDTO>> getVariantsWithPriceOverride() {
        List<ProductVariant> variants = productVariantService.getVariantsWithPriceOverride();
        List<ProductVariantResponseDTO> response = ProductVariantMapper.toResponseDTOList(variants);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/price/range")
    public ResponseEntity<List<ProductVariantResponseDTO>> getVariantsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {

        List<ProductVariant> variants = productVariantService.getVariantsByPriceRange(minPrice, maxPrice);
        List<ProductVariantResponseDTO> response = ProductVariantMapper.toResponseDTOList(variants);
        return ResponseEntity.ok(response);
    }

    // -------------------- Attribute Management --------------------

    @PutMapping("/{id}/attributes/{attributeName}")
    public ResponseEntity<ProductVariantResponseDTO> addAttribute(
            @PathVariable UUID id,
            @PathVariable String attributeName,
            @RequestBody Map<String, String> request) {

        String attributeValue = request.get("attributeValue");
        if (attributeValue == null) {
            return ResponseEntity.badRequest().build();
        }

        ProductVariant productVariant = productVariantService.addAttribute(id, attributeName, attributeValue);
        ProductVariantResponseDTO response = ProductVariantMapper.toResponseDTO(productVariant);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/attributes/{attributeName}")
    public ResponseEntity<ProductVariantResponseDTO> removeAttribute(
            @PathVariable UUID id,
            @PathVariable String attributeName) {

        ProductVariant productVariant = productVariantService.removeAttribute(id, attributeName);
        ProductVariantResponseDTO response = ProductVariantMapper.toResponseDTO(productVariant);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/attributes")
    public ResponseEntity<ProductVariantResponseDTO> updateAttributes(
            @PathVariable UUID id,
            @RequestBody Map<String, String> attributes) {

        ProductVariant productVariant = productVariantService.updateAttributes(id, attributes);
        ProductVariantResponseDTO response = ProductVariantMapper.toResponseDTO(productVariant);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/attributes/{attributeName}/{attributeValue}")
    public ResponseEntity<List<ProductVariantResponseDTO>> findByAttribute(
            @PathVariable String attributeName,
            @PathVariable String attributeValue) {

        List<ProductVariant> variants = productVariantService.findByAttribute(attributeName, attributeValue);
        List<ProductVariantResponseDTO> response = ProductVariantMapper.toResponseDTOList(variants);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/attributes/{attributeName}")
    public ResponseEntity<List<ProductVariantResponseDTO>> findByAttributeName(@PathVariable String attributeName) {
        List<ProductVariant> variants = productVariantService.findByAttributeName(attributeName);
        List<ProductVariantResponseDTO> response = ProductVariantMapper.toResponseDTOList(variants);
        return ResponseEntity.ok(response);
    }

    // -------------------- Status Management --------------------

    @PutMapping("/{id}/status")
    public ResponseEntity<ProductVariantResponseDTO> updateStatus(
            @PathVariable UUID id,
            @RequestBody Map<String, String> request) {

        String statusStr = request.get("status");
        if (statusStr == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            ProductVariant.Status status = ProductVariant.Status.valueOf(statusStr);
            ProductVariant productVariant = productVariantService.updateStatus(id, status);
            ProductVariantResponseDTO response = ProductVariantMapper.toResponseDTO(productVariant);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<ProductVariantResponseDTO> activateVariant(@PathVariable UUID id) {
        ProductVariant productVariant = productVariantService.activateVariant(id);
        ProductVariantResponseDTO response = ProductVariantMapper.toResponseDTO(productVariant);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ProductVariantResponseDTO> deactivateVariant(@PathVariable UUID id) {
        ProductVariant productVariant = productVariantService.deactivateVariant(id);
        ProductVariantResponseDTO response = ProductVariantMapper.toResponseDTO(productVariant);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/discontinue")
    public ResponseEntity<ProductVariantResponseDTO> discontinueVariant(@PathVariable UUID id) {
        ProductVariant productVariant = productVariantService.discontinueVariant(id);
        ProductVariantResponseDTO response = ProductVariantMapper.toResponseDTO(productVariant);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ProductVariantResponseDTO>> getVariantsByStatus(@PathVariable String status) {
        try {
            ProductVariant.Status variantStatus = ProductVariant.Status.valueOf(status);
            List<ProductVariant> variants = productVariantService.getVariantsByStatus(variantStatus);
            List<ProductVariantResponseDTO> response = ProductVariantMapper.toResponseDTOList(variants);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // -------------------- Validation and Utility --------------------

    @GetMapping("/sku/{sku}/available")
    public ResponseEntity<Map<String, Boolean>> isSkuAvailable(@PathVariable String sku) {
        boolean isAvailable = productVariantService.isSkuAvailable(sku);
        return ResponseEntity.ok(Map.of("isAvailable", isAvailable));
    }

    @GetMapping("/product/{productId}/variant-name/{variantName}/available")
    public ResponseEntity<Map<String, Boolean>> isVariantNameAvailable(
            @PathVariable UUID productId,
            @PathVariable String variantName) {

        boolean isAvailable = productVariantService.isVariantNameAvailable(productId, variantName);
        return ResponseEntity.ok(Map.of("isAvailable", isAvailable));
    }

    @GetMapping("/{id}/can-remove-stock")
    public ResponseEntity<Map<String, Boolean>> canRemoveStock(
            @PathVariable UUID id,
            @RequestParam long quantity) {

        boolean canRemove = productVariantService.canRemoveStock(id, quantity);
        return ResponseEntity.ok(Map.of("canRemove", canRemove));
    }

    @GetMapping("/product/{productId}/count")
    public ResponseEntity<Map<String, Long>> getVariantCount(@PathVariable UUID productId) {
        long count = productVariantService.getVariantCount(productId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @GetMapping("/product/{productId}/count/active")
    public ResponseEntity<Map<String, Long>> getActiveVariantCount(@PathVariable UUID productId) {
        long count = productVariantService.getActiveVariantCount(productId);
        return ResponseEntity.ok(Map.of("activeCount", count));
    }

    @GetMapping("/product/{productId}/has-variants")
    public ResponseEntity<Map<String, Boolean>> hasVariants(@PathVariable UUID productId) {
        boolean hasVariants = productVariantService.hasVariants(productId);
        return ResponseEntity.ok(Map.of("hasVariants", hasVariants));
    }

    @GetMapping("/product/{productId}/has-active-variants")
    public ResponseEntity<Map<String, Boolean>> hasActiveVariants(@PathVariable UUID productId) {
        boolean hasActiveVariants = productVariantService.hasActiveVariants(productId);
        return ResponseEntity.ok(Map.of("hasActiveVariants", hasActiveVariants));
    }

    // -------------------- Bulk Operations --------------------

    @PostMapping("/product/{productId}/bulk")
    public ResponseEntity<List<ProductVariantResponseDTO>> createMultipleVariants(
            @PathVariable UUID productId,
            @Valid @RequestBody List<ProductVariantService.VariantCreationRequest> requests) {

        List<ProductVariant> productVariants = productVariantService.createMultipleVariants(productId, requests);
        List<ProductVariantResponseDTO> response = ProductVariantMapper.toResponseDTOList(productVariants);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/product/{productId}/all")
    public ResponseEntity<Void> deleteAllProductVariants(@PathVariable UUID productId) {
        productVariantService.deleteAllProductVariants(productId);
        return ResponseEntity.noContent().build();
    }
}