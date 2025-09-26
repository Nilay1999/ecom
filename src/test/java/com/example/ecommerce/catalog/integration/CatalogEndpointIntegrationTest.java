package com.example.ecommerce.catalog.integration;

import com.example.ecommerce.catalog.domain.Category;
import com.example.ecommerce.catalog.domain.Product;
import com.example.ecommerce.catalog.infra.CategoryRepository;
import com.example.ecommerce.catalog.infra.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for all catalog REST endpoints to verify they work correctly
 * and handle errors appropriately.
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
public class CatalogEndpointIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private Category testCategory;
    private Product testProduct;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Create test data
        testCategory = new Category.Builder()
                .setName("Test Category")
                .setDescription("Test Description")
                .setSlug("test-category")
                .build();
        testCategory = categoryRepository.save(testCategory);

        testProduct = new Product.Builder()
                .productName("Test Product")
                .description("Test Product Description")
                .brandName("Test Brand")
                .price(BigDecimal.valueOf(99.99))
                .weight(BigDecimal.valueOf(1.5))
                .status(Product.Status.ACTIVE)
                .category(testCategory)
                .build();
        testProduct = productRepository.save(testProduct);
    }

    // ==================== Category Endpoint Tests ====================

    @Test
    public void testCreateCategory() throws Exception {
        Map<String, Object> categoryRequest = new HashMap<>();
        categoryRequest.put("categoryName", "New Category");
        categoryRequest.put("description", "New Category Description");

        mockMvc.perform(post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoryName").value("New Category"))
                .andExpect(jsonPath("$.description").value("New Category Description"))
                .andExpect(jsonPath("$.slug").exists());
    }

    @Test
    public void testGetCategoryById() throws Exception {
        mockMvc.perform(get("/api/v1/categories/{id}", testCategory.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryName").value("Test Category"))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    public void testGetCategoryBySlug() throws Exception {
        mockMvc.perform(get("/api/v1/categories/slug/{slug}", testCategory.getSlug()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryName").value("Test Category"));
    }

    @Test
    public void testUpdateCategory() throws Exception {
        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("categoryName", "Updated Category");
        updateRequest.put("description", "Updated Description");

        mockMvc.perform(put("/api/v1/categories/{id}", testCategory.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryName").value("Updated Category"))
                .andExpect(jsonPath("$.description").value("Updated Description"));
    }

    @Test
    public void testGetRootCategories() throws Exception {
        mockMvc.perform(get("/api/v1/categories/root"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void testSearchCategories() throws Exception {
        mockMvc.perform(get("/api/v1/categories/search")
                .param("searchTerm", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // ==================== Product Endpoint Tests ====================

    @Test
    public void testCreateProduct() throws Exception {
        Map<String, Object> productRequest = new HashMap<>();
        productRequest.put("productName", "New Product");
        productRequest.put("description", "New Product Description");
        productRequest.put("brandName", "New Brand");
        productRequest.put("price", 149.99);
        productRequest.put("weight", 2.0);
        productRequest.put("categoryId", testCategory.getId().toString());

        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.brandName").value("New Brand"));
    }

    @Test
    public void testGetProductById() throws Exception {
        mockMvc.perform(get("/api/v1/products/{id}", testProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Test Product"))
                .andExpect(jsonPath("$.brandName").value("Test Brand"));
    }

    @Test
    public void testGetAllProducts() throws Exception {
        mockMvc.perform(get("/api/v1/products/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void testSearchProducts() throws Exception {
        mockMvc.perform(get("/api/v1/products/search")
                .param("searchTerm", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void testGetProductsByCategory() throws Exception {
        mockMvc.perform(get("/api/v1/products/filter/category/{categoryId}", testCategory.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void testGetProductsByBrand() throws Exception {
        mockMvc.perform(get("/api/v1/products/filter/brand/{brandName}", "Test Brand"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void testGetActiveProducts() throws Exception {
        mockMvc.perform(get("/api/v1/products/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void testGetAvailableProducts() throws Exception {
        mockMvc.perform(get("/api/v1/products/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // ==================== Stock Management Tests ====================

    @Test
    public void testUpdateStock() throws Exception {
        Map<String, Long> stockRequest = new HashMap<>();
        stockRequest.put("stockQuantity", 100L);

        mockMvc.perform(put("/api/v1/products/{id}/stock", testProduct.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stockRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity").value(100));
    }

    @Test
    public void testAddStock() throws Exception {
        Map<String, Long> stockRequest = new HashMap<>();
        stockRequest.put("quantity", 50L);

        mockMvc.perform(put("/api/v1/products/{id}/stock/add", testProduct.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stockRequest)))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetStockQuantity() throws Exception {
        mockMvc.perform(get("/api/v1/products/{id}/stock", testProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity").exists());
    }

    @Test
    public void testIsInStock() throws Exception {
        mockMvc.perform(get("/api/v1/products/{id}/stock/in-stock", testProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inStock").exists());
    }

    // ==================== Status Management Tests ====================

    @Test
    public void testUpdateProductStatus() throws Exception {
        Map<String, String> statusRequest = new HashMap<>();
        statusRequest.put("status", "IN_ACTIVE");

        mockMvc.perform(put("/api/v1/products/{id}/status", testProduct.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk());
    }

    @Test
    public void testActivateProduct() throws Exception {
        // First add some stock so activation is allowed
        testProduct.addStock(10);
        productRepository.save(testProduct);

        mockMvc.perform(put("/api/v1/products/{id}/activate", testProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    public void testDeactivateProduct() throws Exception {
        mockMvc.perform(put("/api/v1/products/{id}/deactivate", testProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_ACTIVE"));
    }

    @Test
    public void testIsActive() throws Exception {
        mockMvc.perform(get("/api/v1/products/{id}/active", testProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").exists());
    }

    // ==================== Error Handling Tests ====================

    @Test
    public void testGetNonExistentProduct() throws Exception {
        mockMvc.perform(get("/api/v1/products/{id}", java.util.UUID.randomUUID()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetNonExistentCategory() throws Exception {
        mockMvc.perform(get("/api/v1/categories/{id}", java.util.UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateProductWithInvalidData() throws Exception {
        Map<String, Object> invalidRequest = new HashMap<>();
        invalidRequest.put("productName", ""); // Empty name should fail validation
        invalidRequest.put("price", -10); // Negative price should fail validation

        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateCategoryWithInvalidData() throws Exception {
        Map<String, Object> invalidRequest = new HashMap<>();
        invalidRequest.put("categoryName", ""); // Empty name should fail validation

        mockMvc.perform(post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    // ==================== Pagination Tests ====================

    @Test
    public void testGetPaginatedProducts() throws Exception {
        mockMvc.perform(get("/api/v1/products")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.totalPages").exists());
    }

    @Test
    public void testGetPaginatedCategories() throws Exception {
        mockMvc.perform(get("/api/v1/categories")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.totalPages").exists());
    }
}