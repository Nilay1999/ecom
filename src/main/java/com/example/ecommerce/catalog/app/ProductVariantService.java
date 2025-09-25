package com.example.ecommerce.catalog.app;

import com.example.ecommerce.catalog.domain.Product;
import com.example.ecommerce.catalog.domain.ProductVariant;
import com.example.ecommerce.catalog.infra.ProductRepository;
import com.example.ecommerce.catalog.infra.ProductVariantRepository;
import com.example.ecommerce.catalog.web.exception.product.ProductNotFoundException;
import com.example.ecommerce.catalog.web.exception.productvariant.DuplicateVariantException;
import com.example.ecommerce.catalog.web.exception.productvariant.InsufficientStockException;
import com.example.ecommerce.catalog.web.exception.productvariant.InvalidVariantDataException;
import com.example.ecommerce.catalog.web.exception.productvariant.ProductVariantNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ProductVariantService {

    private final ProductVariantRepository productVariantRepo;
    private final ProductRepository productRepo;

    public ProductVariantService(ProductVariantRepository productVariantRepo, ProductRepository productRepo) {
        this.productVariantRepo = productVariantRepo;
        this.productRepo = productRepo;
    }

    // -------------------- CRUD Operations --------------------

    public ProductVariant create(UUID productId, String variantName, BigDecimal priceOverride,
            long stockQuantity, Map<String, String> attributes, String sku) {
        Product product = findProductById(productId);

        try {
            // Validate variant data
            ProductVariant.validateVariantName(variantName);
            ProductVariant.validatePriceOverride(priceOverride);
            ProductVariant.validateStockQuantity(stockQuantity);
            ProductVariant.validateSku(sku);

            // Check for duplicate variant name within the product
            if (productVariantRepo.existsByProductIdAndVariantName(productId, variantName)) {
                throw new DuplicateVariantException(
                        "A variant with name '" + variantName + "' already exists for this product");
            }

            // Check for duplicate SKU if provided
            if (sku != null && !sku.trim().isEmpty() && productVariantRepo.existsBySku(sku)) {
                throw new DuplicateVariantException("A variant with SKU '" + sku + "' already exists");
            }

            // Validate attributes if provided
            if (attributes != null) {
                for (Map.Entry<String, String> entry : attributes.entrySet()) {
                    ProductVariant.validateAttributeName(entry.getKey());
                    ProductVariant.validateAttributeValue(entry.getValue());
                }
            }

            ProductVariant.Builder builder = new ProductVariant.Builder()
                    .product(product)
                    .variantName(variantName)
                    .stockQuantity(stockQuantity);

            if (priceOverride != null) {
                builder.priceOverride(priceOverride);
            }

            if (attributes != null) {
                builder.attributes(attributes);
            }

            if (sku != null && !sku.trim().isEmpty()) {
                builder.sku(sku);
            }

            ProductVariant variant = builder.build();
            return productVariantRepo.save(variant);

        } catch (IllegalArgumentException e) {
            throw new InvalidVariantDataException("Invalid variant data: " + e.getMessage(), e);
        }
    }

    public ProductVariant findById(UUID id) {
        return productVariantRepo.findById(id)
                .orElseThrow(() -> new ProductVariantNotFoundException("Product variant not found with id: " + id));
    }

    public ProductVariant update(UUID id, String variantName, BigDecimal priceOverride,
            ProductVariant.Status status, String sku) {
        ProductVariant variant = findById(id);

        try {
            // Update variant name if provided
            if (variantName != null && !variantName.equals(variant.getVariantName())) {
                if (productVariantRepo.existsByProductIdAndVariantName(variant.getProduct().getId(), variantName)) {
                    throw new DuplicateVariantException(
                            "A variant with name '" + variantName + "' already exists for this product");
                }
                variant.updateVariantName(variantName);
            }

            // Update price override if provided
            if (priceOverride != null) {
                variant.updatePriceOverride(priceOverride);
            }

            // Update status if provided
            if (status != null) {
                variant.updateStatus(status);
            }

            // Update SKU if provided
            if (sku != null) {
                if (!sku.trim().isEmpty() && !sku.equals(variant.getSku()) && productVariantRepo.existsBySku(sku)) {
                    throw new DuplicateVariantException("A variant with SKU '" + sku + "' already exists");
                }
                variant.updateSku(sku);
            }

            return productVariantRepo.save(variant);

        } catch (IllegalArgumentException e) {
            throw new InvalidVariantDataException("Invalid variant data: " + e.getMessage(), e);
        }
    }

    public void delete(UUID id) {
        ProductVariant variant = findById(id);
        productVariantRepo.delete(variant);
    }

    // -------------------- Variant Management --------------------

    public List<ProductVariant> getProductVariants(UUID productId) {
        validateProductExists(productId);
        return productVariantRepo.findByProductId(productId);
    }

    public List<ProductVariant> getActiveVariants(UUID productId) {
        validateProductExists(productId);
        return productVariantRepo.findActiveVariantsByProductId(productId);
    }

    public List<ProductVariant> getAvailableVariants(UUID productId) {
        validateProductExists(productId);
        return productVariantRepo.findAvailableVariantsByProductId(productId);
    }

    public Optional<ProductVariant> findByProductAndVariantName(UUID productId, String variantName) {
        validateProductExists(productId);
        return productVariantRepo.findByProductIdAndVariantName(productId, variantName);
    }

    public Optional<ProductVariant> findBySku(String sku) {
        return productVariantRepo.findBySku(sku);
    }

    // -------------------- Stock Management --------------------

    public ProductVariant updateStock(UUID variantId, long newStockQuantity) {
        ProductVariant variant = findById(variantId);

        try {
            variant.updateStockQuantity(newStockQuantity);
            return productVariantRepo.save(variant);
        } catch (IllegalArgumentException e) {
            throw new InvalidVariantDataException("Invalid stock quantity: " + e.getMessage(), e);
        }
    }

    public ProductVariant addStock(UUID variantId, long quantity) {
        ProductVariant variant = findById(variantId);

        try {
            variant.addStock(quantity);
            return productVariantRepo.save(variant);
        } catch (IllegalArgumentException e) {
            throw new InvalidVariantDataException("Invalid stock quantity: " + e.getMessage(), e);
        }
    }

    public ProductVariant removeStock(UUID variantId, long quantity) {
        ProductVariant variant = findById(variantId);

        try {
            if (quantity > variant.getStockQuantity()) {
                throw new InsufficientStockException("Insufficient stock. Available: " +
                        variant.getStockQuantity() + ", Requested: " + quantity);
            }
            variant.removeStock(quantity);
            return productVariantRepo.save(variant);
        } catch (IllegalArgumentException e) {
            throw new InvalidVariantDataException("Invalid stock operation: " + e.getMessage(), e);
        }
    }

    public long getTotalStock(UUID productId) {
        validateProductExists(productId);
        return productVariantRepo.getTotalStockByProductId(productId);
    }

    public long getTotalAvailableStock(UUID productId) {
        validateProductExists(productId);
        return productVariantRepo.getTotalAvailableStockByProductId(productId);
    }

    public List<ProductVariant> getLowStockVariants(long threshold) {
        return productVariantRepo.findLowStockVariants(threshold);
    }

    public List<ProductVariant> getOutOfStockVariants() {
        return productVariantRepo.findOutOfStockVariants();
    }

    // -------------------- Price Management --------------------

    public ProductVariant updatePriceOverride(UUID variantId, BigDecimal priceOverride) {
        ProductVariant variant = findById(variantId);

        try {
            variant.updatePriceOverride(priceOverride);
            return productVariantRepo.save(variant);
        } catch (IllegalArgumentException e) {
            throw new InvalidVariantDataException("Invalid price override: " + e.getMessage(), e);
        }
    }

    public ProductVariant removePriceOverride(UUID variantId) {
        ProductVariant variant = findById(variantId);
        variant.updatePriceOverride(BigDecimal.ZERO);
        return productVariantRepo.save(variant);
    }

    public List<ProductVariant> getVariantsWithPriceOverride() {
        return productVariantRepo.findVariantsWithPriceOverride();
    }

    public List<ProductVariant> getVariantsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productVariantRepo.findByPriceOverrideBetween(minPrice, maxPrice);
    }

    // -------------------- Attribute Management --------------------

    public ProductVariant addAttribute(UUID variantId, String attributeName, String attributeValue) {
        ProductVariant variant = findById(variantId);

        try {
            variant.addAttribute(attributeName, attributeValue);
            return productVariantRepo.save(variant);
        } catch (IllegalArgumentException e) {
            throw new InvalidVariantDataException("Invalid attribute: " + e.getMessage(), e);
        }
    }

    public ProductVariant removeAttribute(UUID variantId, String attributeName) {
        ProductVariant variant = findById(variantId);

        try {
            variant.removeAttribute(attributeName);
            return productVariantRepo.save(variant);
        } catch (IllegalArgumentException e) {
            throw new InvalidVariantDataException("Invalid attribute name: " + e.getMessage(), e);
        }
    }

    public ProductVariant updateAttributes(UUID variantId, Map<String, String> attributes) {
        ProductVariant variant = findById(variantId);

        try {
            // Validate all attributes first
            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                ProductVariant.validateAttributeName(entry.getKey());
                ProductVariant.validateAttributeValue(entry.getValue());
            }

            // Clear existing attributes and add new ones
            variant.getAttributes().clear();
            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                variant.addAttribute(entry.getKey(), entry.getValue());
            }

            return productVariantRepo.save(variant);
        } catch (IllegalArgumentException e) {
            throw new InvalidVariantDataException("Invalid attributes: " + e.getMessage(), e);
        }
    }

    public List<ProductVariant> findByAttribute(String attributeName, String attributeValue) {
        return productVariantRepo.findByAttribute(attributeName, attributeValue);
    }

    public List<ProductVariant> findByAttributeName(String attributeName) {
        return productVariantRepo.findByAttributeName(attributeName);
    }

    // -------------------- Status Management --------------------

    public ProductVariant updateStatus(UUID variantId, ProductVariant.Status status) {
        ProductVariant variant = findById(variantId);
        variant.updateStatus(status);
        return productVariantRepo.save(variant);
    }

    public ProductVariant activateVariant(UUID variantId) {
        return updateStatus(variantId, ProductVariant.Status.ACTIVE);
    }

    public ProductVariant deactivateVariant(UUID variantId) {
        return updateStatus(variantId, ProductVariant.Status.INACTIVE);
    }

    public ProductVariant discontinueVariant(UUID variantId) {
        return updateStatus(variantId, ProductVariant.Status.DISCONTINUED);
    }

    public List<ProductVariant> getVariantsByStatus(ProductVariant.Status status) {
        return productVariantRepo.findByStatus(status);
    }

    // -------------------- Bulk Operations --------------------

    public void deleteAllProductVariants(UUID productId) {
        validateProductExists(productId);
        productVariantRepo.deleteByProductId(productId);
    }

    public List<ProductVariant> createMultipleVariants(UUID productId, List<VariantCreationRequest> requests) {
        Product product = findProductById(productId);

        // Validate all requests first
        for (VariantCreationRequest request : requests) {
            try {
                ProductVariant.validateVariantName(request.variantName());
                ProductVariant.validatePriceOverride(request.priceOverride());
                ProductVariant.validateStockQuantity(request.stockQuantity());
                ProductVariant.validateSku(request.sku());

                if (request.attributes() != null) {
                    for (Map.Entry<String, String> entry : request.attributes().entrySet()) {
                        ProductVariant.validateAttributeName(entry.getKey());
                        ProductVariant.validateAttributeValue(entry.getValue());
                    }
                }

                // Check for duplicate variant names
                if (productVariantRepo.existsByProductIdAndVariantName(productId, request.variantName())) {
                    throw new DuplicateVariantException(
                            "A variant with name '" + request.variantName() + "' already exists for this product");
                }

                // Check for duplicate SKUs
                if (request.sku() != null && !request.sku().trim().isEmpty()
                        && productVariantRepo.existsBySku(request.sku())) {
                    throw new DuplicateVariantException("A variant with SKU '" + request.sku() + "' already exists");
                }

            } catch (IllegalArgumentException e) {
                throw new InvalidVariantDataException("Invalid variant data in request: " + e.getMessage(), e);
            }
        }

        List<ProductVariant> variants = requests.stream()
                .map(request -> {
                    ProductVariant.Builder builder = new ProductVariant.Builder()
                            .product(product)
                            .variantName(request.variantName())
                            .stockQuantity(request.stockQuantity());

                    if (request.priceOverride() != null) {
                        builder.priceOverride(request.priceOverride());
                    }

                    if (request.attributes() != null) {
                        builder.attributes(request.attributes());
                    }

                    if (request.sku() != null && !request.sku().trim().isEmpty()) {
                        builder.sku(request.sku());
                    }

                    return builder.build();
                })
                .toList();

        return productVariantRepo.saveAll(variants);
    }

    // -------------------- Statistics and Reporting --------------------

    public long getVariantCount(UUID productId) {
        validateProductExists(productId);
        return productVariantRepo.countByProductId(productId);
    }

    public long getActiveVariantCount(UUID productId) {
        validateProductExists(productId);
        return productVariantRepo.countActiveVariantsByProductId(productId);
    }

    public boolean hasVariants(UUID productId) {
        validateProductExists(productId);
        return productVariantRepo.countByProductId(productId) > 0;
    }

    public boolean hasActiveVariants(UUID productId) {
        validateProductExists(productId);
        return productVariantRepo.countActiveVariantsByProductId(productId) > 0;
    }

    // -------------------- Validation Methods --------------------

    public boolean isSkuAvailable(String sku) {
        return !productVariantRepo.existsBySku(sku);
    }

    public boolean isVariantNameAvailable(UUID productId, String variantName) {
        validateProductExists(productId);
        return !productVariantRepo.existsByProductIdAndVariantName(productId, variantName);
    }

    public boolean canRemoveStock(UUID variantId, long quantity) {
        ProductVariant variant = findById(variantId);
        return variant.getStockQuantity() >= quantity;
    }

    // -------------------- Helper Methods --------------------

    private Product findProductById(UUID productId) {
        return productRepo.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
    }

    private void validateProductExists(UUID productId) {
        if (!productRepo.existsById(productId)) {
            throw new ProductNotFoundException("Product not found with id: " + productId);
        }
    }

    // -------------------- DTOs --------------------

    public record VariantCreationRequest(
            String variantName,
            BigDecimal priceOverride,
            long stockQuantity,
            Map<String, String> attributes,
            String sku) {
    }
}