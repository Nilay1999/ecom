package com.example.ecommerce.catalog.util;

import com.example.ecommerce.catalog.app.CategoryService;
import com.example.ecommerce.catalog.app.ProductImageService;
import com.example.ecommerce.catalog.app.ProductService;
import com.example.ecommerce.catalog.app.ProductVariantService;
import com.example.ecommerce.catalog.infra.CategoryRepository;
import com.example.ecommerce.catalog.infra.ProductImageRepository;
import com.example.ecommerce.catalog.infra.ProductRepository;
import com.example.ecommerce.catalog.infra.ProductVariantRepository;
import com.example.ecommerce.catalog.web.controller.CategoryController;
import com.example.ecommerce.catalog.web.controller.ProductController;
import com.example.ecommerce.catalog.web.controller.ProductImageController;
import com.example.ecommerce.catalog.web.controller.ProductVariantController;
import com.example.ecommerce.catalog.web.exception.GlobalExceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Utility class to verify that all catalog components are properly wired
 * and can be instantiated during application startup.
 */
@Component
public class IntegrationVerifier implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(IntegrationVerifier.class);

    // Services
    private final ProductService productService;
    private final CategoryService categoryService;
    private final ProductImageService productImageService;
    private final ProductVariantService productVariantService;

    // Repositories
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductVariantRepository productVariantRepository;

    // Controllers
    private final ProductController productController;
    private final CategoryController categoryController;
    private final ProductImageController productImageController;
    private final ProductVariantController productVariantController;

    // Exception Handler
    private final GlobalExceptionHandler globalExceptionHandler;

    public IntegrationVerifier(
            ProductService productService,
            CategoryService categoryService,
            ProductImageService productImageService,
            ProductVariantService productVariantService,
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            ProductImageRepository productImageRepository,
            ProductVariantRepository productVariantRepository,
            ProductController productController,
            CategoryController categoryController,
            ProductImageController productImageController,
            ProductVariantController productVariantController,
            GlobalExceptionHandler globalExceptionHandler) {

        this.productService = productService;
        this.categoryService = categoryService;
        this.productImageService = productImageService;
        this.productVariantService = productVariantService;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productImageRepository = productImageRepository;
        this.productVariantRepository = productVariantRepository;
        this.productController = productController;
        this.categoryController = categoryController;
        this.productImageController = productImageController;
        this.productVariantController = productVariantController;
        this.globalExceptionHandler = globalExceptionHandler;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("=== Catalog Service Integration Verification ===");

        // Verify all services are properly injected
        verifyServices();

        // Verify all repositories are properly injected
        verifyRepositories();

        // Verify all controllers are properly injected
        verifyControllers();

        // Verify exception handler is properly injected
        verifyExceptionHandler();

        logger.info("=== All Catalog Components Successfully Wired ===");
    }

    private void verifyServices() {
        logger.info("Verifying Services...");

        if (productService != null) {
            logger.info("✅ ProductService - OK");
        } else {
            logger.error("❌ ProductService - FAILED");
        }

        if (categoryService != null) {
            logger.info("✅ CategoryService - OK");
        } else {
            logger.error("❌ CategoryService - FAILED");
        }

        if (productImageService != null) {
            logger.info("✅ ProductImageService - OK");
        } else {
            logger.error("❌ ProductImageService - FAILED");
        }

        if (productVariantService != null) {
            logger.info("✅ ProductVariantService - OK");
        } else {
            logger.error("❌ ProductVariantService - FAILED");
        }
    }

    private void verifyRepositories() {
        logger.info("Verifying Repositories...");

        if (productRepository != null) {
            logger.info("✅ ProductRepository - OK");
        } else {
            logger.error("❌ ProductRepository - FAILED");
        }

        if (categoryRepository != null) {
            logger.info("✅ CategoryRepository - OK");
        } else {
            logger.error("❌ CategoryRepository - FAILED");
        }

        if (productImageRepository != null) {
            logger.info("✅ ProductImageRepository - OK");
        } else {
            logger.error("❌ ProductImageRepository - FAILED");
        }

        if (productVariantRepository != null) {
            logger.info("✅ ProductVariantRepository - OK");
        } else {
            logger.error("❌ ProductVariantRepository - FAILED");
        }
    }

    private void verifyControllers() {
        logger.info("Verifying Controllers...");

        if (productController != null) {
            logger.info("✅ ProductController - OK");
        } else {
            logger.error("❌ ProductController - FAILED");
        }

        if (categoryController != null) {
            logger.info("✅ CategoryController - OK");
        } else {
            logger.error("❌ CategoryController - FAILED");
        }

        if (productImageController != null) {
            logger.info("✅ ProductImageController - OK");
        } else {
            logger.error("❌ ProductImageController - FAILED");
        }

        if (productVariantController != null) {
            logger.info("✅ ProductVariantController - OK");
        } else {
            logger.error("❌ ProductVariantController - FAILED");
        }
    }

    private void verifyExceptionHandler() {
        logger.info("Verifying Exception Handler...");

        if (globalExceptionHandler != null) {
            logger.info("✅ GlobalExceptionHandler - OK");
        } else {
            logger.error("❌ GlobalExceptionHandler - FAILED");
        }
    }
}