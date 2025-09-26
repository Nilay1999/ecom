package com.example.ecommerce.catalog.integration;

import com.example.ecommerce.catalog.app.CategoryService;
import com.example.ecommerce.catalog.app.ProductImageService;
import com.example.ecommerce.catalog.app.ProductService;
import com.example.ecommerce.catalog.app.ProductVariantService;
import com.example.ecommerce.catalog.domain.Category;
import com.example.ecommerce.catalog.domain.Product;
import com.example.ecommerce.catalog.domain.ProductImage;
import com.example.ecommerce.catalog.domain.ProductVariant;
import com.example.ecommerce.catalog.infra.CategoryRepository;
import com.example.ecommerce.catalog.infra.ProductImageRepository;
import com.example.ecommerce.catalog.infra.ProductRepository;
import com.example.ecommerce.catalog.infra.ProductVariantRepository;
import com.example.ecommerce.catalog.web.controller.CategoryController;
import com.example.ecommerce.catalog.web.controller.ProductController;
import com.example.ecommerce.catalog.web.controller.ProductImageController;
import com.example.ecommerce.catalog.web.controller.ProductVariantController;
import com.example.ecommerce.catalog.web.exception.GlobalExceptionHandler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test to verify that all catalog components are properly wired
 * together
 * and can work end-to-end.
 */
@SpringBootTest
@ActiveProfiles("test")
public class CatalogIntegrationTest {

    // Services
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductImageService productImageService;

    @Autowired
    private ProductVariantService productVariantService;

    // Repositories
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    // Controllers
    @Autowired
    private ProductController productController;

    @Autowired
    private CategoryController categoryController;

    @Autowired
    private ProductImageController productImageController;

    @Autowired
    private ProductVariantController productVariantController;

    // Exception Handler
    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    public void testAllComponentsAreWired() {
        // Verify all services are properly injected
        assertThat(productService).isNotNull();
        assertThat(categoryService).isNotNull();
        assertThat(productImageService).isNotNull();
        assertThat(productVariantService).isNotNull();

        // Verify all repositories are properly injected
        assertThat(productRepository).isNotNull();
        assertThat(categoryRepository).isNotNull();
        assertThat(productImageRepository).isNotNull();
        assertThat(productVariantRepository).isNotNull();

        // Verify all controllers are properly injected
        assertThat(productController).isNotNull();
        assertThat(categoryController).isNotNull();
        assertThat(productImageController).isNotNull();
        assertThat(productVariantController).isNotNull();

        // Verify exception handler is properly injected
        assertThat(globalExceptionHandler).isNotNull();
    }

    @Test
    public void testBasicCatalogOperationsWorkTogether() {
        // Test basic category creation
        Category category = categoryService.create("Test Category", "Test Description");
        assertThat(category).isNotNull();
        assertThat(category.getName()).isEqualTo("Test Category");
        assertThat(category.getSlug()).isNotNull();

        // Test basic product creation with category relationship
        Product product = new Product.Builder()
                .productName("Test Product")
                .description("Test Description")
                .brandName("Test Brand")
                .price(BigDecimal.valueOf(99.99))
                .weight(BigDecimal.valueOf(1.5))
                .status(Product.Status.ACTIVE)
                .category(category)
                .build();

        Product savedProduct = productRepository.save(product);
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getCategory()).isEqualTo(category);

        // Test product image creation with product relationship
        ProductImage productImage = new ProductImage("https://example.com/image.jpg", true, savedProduct);
        ProductImage savedImage = productImageRepository.save(productImage);
        assertThat(savedImage).isNotNull();
        assertThat(savedImage.getProduct()).isEqualTo(savedProduct);

        // Test product variant creation with product relationship
        ProductVariant variant = new ProductVariant.Builder()
                .variantName("Test Variant")
                .sku("TEST-VAR-001")
                .priceOverride(BigDecimal.valueOf(89.99))
                .stockQuantity(10L)
                .product(savedProduct)
                .build();

        ProductVariant savedVariant = productVariantRepository.save(variant);
        assertThat(savedVariant).isNotNull();
        assertThat(savedVariant.getProduct()).isEqualTo(savedProduct);

        // Verify relationships work both ways
        Product retrievedProduct = productRepository.findById(savedProduct.getId()).orElse(null);
        assertThat(retrievedProduct).isNotNull();
        assertThat(retrievedProduct.getProductImages()).hasSize(1);
        assertThat(retrievedProduct.getProductVariants()).hasSize(1);
    }

    @Test
    public void testServiceLayerIntegration() {
        // Create category through service
        Category category = categoryService.create("Service Test Category", "Service Test Description");

        // Create product through service using the category
        var productResponse = productService.create(
                "Service Test Product",
                "Service Test Description",
                BigDecimal.valueOf(149.99),
                "Service Test Brand",
                BigDecimal.valueOf(2.0),
                category.getId());

        assertThat(productResponse).isNotNull();

        // Verify the product was created with proper relationships
        Product retrievedProduct = productService.getProductById(productResponse.getId());
        assertThat(retrievedProduct).isNotNull();
        assertThat(retrievedProduct.getCategory().getId()).isEqualTo(category.getId());

        // Test stock management
        productService.addStock(retrievedProduct.getId(), 50L);
        long stockQuantity = productService.getStockQuantity(retrievedProduct.getId());
        assertThat(stockQuantity).isEqualTo(50L);

        // Test status management
        Product activatedProduct = productService.activateProduct(retrievedProduct.getId());
        assertThat(activatedProduct.getStatus()).isEqualTo(Product.Status.ACTIVE);
        assertThat(productService.isActive(retrievedProduct.getId())).isTrue();
    }

    @Test
    public void testErrorHandlingIntegration() {
        // Test that proper exceptions are thrown for invalid operations
        try {
            productService.getProductById(java.util.UUID.randomUUID());
            assertThat(false).as("Should have thrown exception for non-existent product").isTrue();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("Product not found");
        }

        try {
            categoryService.findById(java.util.UUID.randomUUID());
            assertThat(false).as("Should have thrown exception for non-existent category").isTrue();
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("not found");
        }
    }
}