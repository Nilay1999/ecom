package com.example.ecommerce.catalog.app;

import com.example.ecommerce.catalog.domain.Category;
import com.example.ecommerce.catalog.domain.Product;
import com.example.ecommerce.catalog.domain.ProductImage;
import com.example.ecommerce.catalog.domain.specification.ProductSpecification;
import com.example.ecommerce.catalog.infra.CategoryRepository;
import com.example.ecommerce.catalog.infra.ProductRepository;
import com.example.ecommerce.catalog.web.dto.product.CreateProductResponseDTO;
import com.example.ecommerce.catalog.web.dto.product.ProductImagePayload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;nal;
import java.util.UUID;

@Service
@Transactional
public class ProductService {
        private final ProductRepository productRepo;
        private final CategoryRepository categoryRepo;

        public ProductService(ProductRepository productRepo, CategoryRepository categoryRepo) {
                this.productRepo = productRepo;
                this.categoryRepo = categoryRepo;
        }

        // ===== CRUD Operations with Business Logic =====

        /**
         * Create a new product with images
         */
        public CreateProductResponseDTO create(String name, String description, BigDecimal price, String brand,
                        BigDecimal weight, UUID categoryId, List<ProductImagePayload> productImagelist) {

                // Validate business rules
                validateProductCreation(name, description, price, brand, weight, categoryId);

                Category category = categoryRepo.findById(categoryId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Category not found with ID: " + categoryId));

                // Check for duplicate product name
                if (productRepo.existsByProductName(name.trim())) {
                        throw new IllegalArgumentException("Product with name '" + name + "' already exists");
                }

                Product product = new Product.Builder()
                                .productName(name.trim())
                                .description(description != null ? description.trim() : null)
                                .brandName(brand.trim())
                                .price(price)
                                .weight(weight)
                                .status(Product.Status.OUT_OF_STOCK)
                                .category(category)
                                .build();

                // Add images with validation
                if (productImagelist != null && !productImagelist.isEmpty()) {
                        for (ProductImagePayload image : productImagelist) {
                                product.addImage(new ProductImage(image.getImageUrl(), image.isPrimary(), product));
                        }
                }

                Product savedProduct = productRepo.save(product);
                return new CreateProductResponseDTO(savedProduct.getId(), savedProduct.getBrandName(),
                                savedProduct.getDescription(), savedProduct.getPrice(),
                                savedProduct.getWeight(), savedProduct.getStockQuantity(),
                                savedProduct.getCategory(), savedProduct.getProductImages());
        }

        /**
         * Create a new product without images
         */
        public CreateProductResponseDTO create(String name, String description, BigDecimal price, String brand,
                        BigDecimal weight, UUID categoryId) {
                return create(name, description, price, brand, weight, categoryId, null);
        }

        /**
         * Update an existing product
         */
        public Product updateProduct(UUID productId, String name, String description, BigDecimal price,
                        String brand, BigDecimal weight, UUID categoryId) {

                Product product = getProductById(productId);

                // Validate business rules
                validateProductUpdate(productId, name, description, price, brand, weight, categoryId);

                // Update fields with business logic
                if (name != null && !name.trim().isEmpty()) {
                        product.updateProductName(name.trim());
                }

                if (description != null) {
                        product.updateDescription(description.trim());
                }

                if (price != null) {
                        product.updatePrice(price);
                }

                if (brand != null && !brand.trim().isEmpty()) {
                        product.updateBrandName(brand.trim());
                }

                if (weight != null) {
                        product.updateWeight(weight);
                }

                if (categoryId != null) {
                        Category category = categoryRepo.findById(categoryId)
                                        .orElseThrow(() -> new IllegalArgumentException(
                                                        "Category not found with ID: " + categoryId));
                        product.updateCategory(category);
                }

                return productRepo.save(product);
        }

        /**
         * Delete a product by ID
         */
        public void deleteProduct(UUID productId) {
                Product product = getProductById(productId);

                // Business rule: Can only delete inactive products
                if (product.getStatus() == Product.Status.ACTIVE) {
                        throw new IllegalStateException("Cannot delete active product. Please deactivate first.");
                }

                productRepo.delete(product);
        }

        /**
         * Get product by ID with proper exception handling
         */
        @Transactional(readOnly = true)
        public Product getProductById(UUID id) {
                if (id == null) {
                        throw new IllegalArgumentException("Product ID cannot be null");
                }

                return productRepo.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + id));
        }

        /**
         * Get all products with pagination
         */
        @Transactional(readOnly = true)
        public Page<Product> getPaginatedProducts(int page, int size) {
                if (page < 0) {
                        throw new IllegalArgumentException("Page number cannot be negative");
                }
                if (size <= 0) {
                        throw new IllegalArgumentException("Page size must be positive");
                }

                Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
                return productRepo.findAll(pageable);
        }

        /**
         * Get all products
         */
        @Transactional(readOnly = true)
        public List<Product> getAllProducts() {
                return productRepo.findAll();
        }

        // ===== Search and Filtering Methods =====

        /**
         * Search products by name or description
         */
        @Transactional(readOnly = true)
        public List<Product> searchProducts(String searchTerm) {
                if (searchTerm == null || searchTerm.trim().isEmpty()) {
                        return getAllProducts();
                }
                return productRepo.searchByNameOrDescription(searchTerm.trim());
        }

        /**
         * Search products with pagination
         */
        @Transactional(readOnly = true)
        public Page<Product> searchProducts(String searchTerm, int page, int size) {
                if (page < 0) {
                        throw new IllegalArgumentException("Page number cannot be negative");
                }
                if (size <= 0) {
                        throw new IllegalArgumentException("Page size must be positive");
                }

                Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

                if (searchTerm == null || searchTerm.trim().isEmpty()) {
                        return productRepo.findAll(pageable);
                }

                return productRepo.searchByNameOrDescription(searchTerm.trim(), pageable);
        }

        /**
         * Filter products using specifications
         */
        @Transactional(readOnly = true)
        public List<Product> filterProducts(String name, String brand, UUID categoryId, Product.Status status,
                        BigDecimal minPrice, BigDecimal maxPrice, BigDecimal minRating,
                        Boolean inStock) {

                Specification<Product> spec = ProductSpecification.withFilters(
                                name, brand, categoryId, status, minPrice, maxPrice, minRating, inStock);

                return productRepo.findAll(spec);
        }

        /**
         * Filter products with pagination
         */
        @Transactional(readOnly = true)
        public Page<Product> filterProducts(String name, String brand, UUID categoryId, Product.Status status,
                        BigDecimal minPrice, BigDecimal maxPrice, BigDecimal minRating,
                        Boolean inStock, int page, int size) {

                if (page < 0) {
                        throw new IllegalArgumentException("Page number cannot be negative");
                }
                if (size <= 0) {
                        throw new IllegalArgumentException("Page size must be positive");
                }

                Specification<Product> spec = ProductSpecification.withFilters(
                                name, brand, categoryId, status, minPrice, maxPrice, minRating, inStock);

                Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
                return productRepo.findAll(spec, pageable);
        }

        /**
         * Find products by price range
         */
        @Transactional(readOnly = true)
        public List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
                if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
                        throw new IllegalArgumentException("Minimum price cannot be greater than maximum price");
                }

                return productRepo.findByPriceBetween(
                                minPrice != null ? minPrice : BigDecimal.ZERO,
                                maxPrice != null ? maxPrice : BigDecimal.valueOf(Double.MAX_VALUE));
        }

        /**
         * Find products by category
         */
        @Transactional(readOnly = true)
        public List<Product> findByCategory(UUID categoryId) {
                if (categoryId == null) {
                        throw new IllegalArgumentException("Category ID cannot be null");
                }

                return productRepo.findByCategoryId(categoryId);
        }

        /**
         * Find products by brand
         */
        @Transactional(readOnly = true)
        public List<Product> findByBrand(String brandName) {
                if (brandName == null || brandName.trim().isEmpty()) {
                        throw new IllegalArgumentException("Brand name cannot be null or empty");
                }

                return productRepo.findByBrandNameContainingIgnoreCase(brandName.trim());
        }

        /**
         * Find products by status
         */
        @Transactional(readOnly = true)
        public List<Product> findByStatus(Product.Status status) {
                if (status == null) {
                        throw new IllegalArgumentException("Status cannot be null");
                }

                return productRepo.findByStatus(status);
        }

        /**
         * Find active products
         */
        @Transactional(readOnly = true)
        public List<Product> findActiveProducts() {
                return productRepo.findActiveProducts();
        }

        /**
         * Find available products (active and in stock)
         */
        @Transactional(readOnly = true)
        public List<Product> findAvailableProducts() {
                return productRepo.findAvailableProducts();
        }

        // ===== Stock Management Operations =====

        /**
         * Add stock to a product
         */
        public Product addStock(UUID productId, long quantity) {
                if (quantity <= 0) {
                        throw new IllegalArgumentException("Stock quantity to add must be positive");
                }

                Product product = getProductById(productId);
                product.addStock(quantity);

                return productRepo.save(product);
        }

        /**
         * Remove stock from a product
         */
        public Product removeStock(UUID productId, long quantity) {
                if (quantity <= 0) {
                        throw new IllegalArgumentException("Stock quantity to remove must be positive");
                }

                Product product = getProductById(productId);
                product.removeStock(quantity);

                return productRepo.save(product);
        }

        /**
         * Update stock quantity
         */
        public Product updateStock(UUID productId, long newQuantity) {
                if (newQuantity < 0) {
                        throw new IllegalArgumentException("Stock quantity cannot be negative");
                }

                Product product = getProductById(productId);
                product.updateStockQuantity(newQuantity);

                return productRepo.save(product);
        }

        /**
         * Check if product is in stock
         */
        @Transactional(readOnly = true)
        public boolean isInStock(UUID productId) {
                Product product = getProductById(productId);
                return product.isInStock();
        }

        /**
         * Get current stock quantity
         */
        @Transactional(readOnly = true)
        public long getStockQuantity(UUID productId) {
                Product product = getProductById(productId);
                return product.getStockQuantity();
        }

        /**
         * Find products with low stock
         */
        @Transactional(readOnly = true)
        public List<Product> findLowStockProducts(long threshold) {
                if (threshold < 0) {
                        throw new IllegalArgumentException("Stock threshold cannot be negative");
                }

                return productRepo.findLowStockProducts(threshold);
        }

        /**
         * Find out of stock products
         */
        @Transactional(readOnly = true)
        public List<Product> findOutOfStockProducts() {
                return productRepo.findOutOfStockProducts();
        }

        // ===== Product Status Management =====

        /**
         * Activate a product
         */
        public Product activateProduct(UUID productId) {
                Product product = getProductById(productId);

                // Business rule: Can only activate products that have stock
                if (product.getStockQuantity() == 0) {
                        throw new IllegalStateException(
                                        "Cannot activate product with zero stock. Please add stock first.");
                }

                product.updateStatus(Product.Status.ACTIVE);
                return productRepo.save(product);
        }

        /**
         * Deactivate a product
         */
        public Product deactivateProduct(UUID productId) {
                Product product = getProductById(productId);
                product.updateStatus(Product.Status.IN_ACTIVE);
                return productRepo.save(product);
        }

        /**
         * Mark product as out of stock
         */
        public Product markAsOutOfStock(UUID productId) {
                Product product = getProductById(productId);
                product.updateStatus(Product.Status.OUT_OF_STOCK);
                // Also set stock to 0 for consistency
                product.updateStockQuantity(0);
                return productRepo.save(product);
        }

        /**
         * Update product status
         */
        public Product updateStatus(UUID productId, Product.Status newStatus) {
                if (newStatus == null) {
                        throw new IllegalArgumentException("Status cannot be null");
                }

                Product product = getProductById(productId);

                // Business rule validation for status transitions
                validateStatusTransition(product, newStatus);

                product.updateStatus(newStatus);
                return productRepo.save(product);
        }

        /**
         * Check if product is active
         */
        @Transactional(readOnly = true)
        public boolean isActive(UUID productId) {
                Product product = getProductById(productId);
                return product.isActive();
        }

        /**
         * Check if product is available (active and in stock)
         */
        @Transactional(readOnly = true)
        public boolean isAvailable(UUID productId) {
                Product product = getProductById(productId);
                return product.isAvailable();
        }

        // ===== Additional Business Operations =====

        /**
         * Get top-rated products
         */
        @Transactional(readOnly = true)
        public List<Product> getTopRatedProducts(int limit) {
                if (limit <= 0) {
                        throw new IllegalArgumentException("Limit must be positive");
                }

                Pageable pageable = PageRequest.of(0, limit);
                return productRepo.findTopRatedProducts(pageable);
        }

        /**
         * Get newest products
         */
        @Transactional(readOnly = true)
        public List<Product> getNewestProducts(int limit) {
                if (limit <= 0) {
                        throw new IllegalArgumentException("Limit must be positive");
                }

                Pageable pageable = PageRequest.of(0, limit);
                return productRepo.findNewestProducts(pageable);
        }

        /**
         * Count products by category
         */
        @Transactional(readOnly = true)
        public long countProductsByCategory(UUID categoryId) {
                if (categoryId == null) {
                        throw new IllegalArgumentException("Category ID cannot be null");
                }

                return productRepo.countByCategoryId(categoryId);
        }

        /**
         * Count active products by category
         */
        @Transactional(readOnly = true)
        public long countActiveProductsByCategory(UUID categoryId) {
                if (categoryId == null) {
                        throw new IllegalArgumentException("Category ID cannot be null");
                }

                return productRepo.countActiveByCategoryId(categoryId);
        }

        // ===== Private Helper Methods =====

        private void validateProductCreation(String name, String description, BigDecimal price,
                        String brand, BigDecimal weight, UUID categoryId) {
                if (name == null || name.trim().isEmpty()) {
                        throw new IllegalArgumentException("Product name cannot be null or empty");
                }
                if (brand == null || brand.trim().isEmpty()) {
                        throw new IllegalArgumentException("Brand name cannot be null or empty");
                }
                if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new IllegalArgumentException("Price must be greater than zero");
                }
                if (weight == null || weight.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new IllegalArgumentException("Weight must be greater than zero");
                }
                if (categoryId == null) {
                        throw new IllegalArgumentException("Category ID cannot be null");
                }
        }

        private void validateProductUpdate(UUID productId, String name, String description, BigDecimal price,
                        String brand, BigDecimal weight, UUID categoryId) {
                if (productId == null) {
                        throw new IllegalArgumentException("Product ID cannot be null");
                }

                // Check for duplicate name if name is being updated
                if (name != null && !name.trim().isEmpty()) {
                        if (productRepo.existsByProductNameAndIdNot(name.trim(), productId)) {
                                throw new IllegalArgumentException("Product with name '" + name + "' already exists");
                        }
                }

                if (price != null && price.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new IllegalArgumentException("Price must be greater than zero");
                }
                if (weight != null && weight.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new IllegalArgumentException("Weight must be greater than zero");
                }
        }

        private void validateStatusTransition(Product product, Product.Status newStatus) {
                Product.Status currentStatus = product.getStatus();

                // Business rules for status transitions
                if (newStatus == Product.Status.ACTIVE && product.getStockQuantity() == 0) {
                        throw new IllegalStateException("Cannot activate product with zero stock");
                }

                // Log status transition for audit purposes
                // In a real application, you might want to use a proper logging framework
                System.out.println("Product " + product.getId() + " status changing from " +
                                currentStatus + " to " + newStatus);
        }
}
