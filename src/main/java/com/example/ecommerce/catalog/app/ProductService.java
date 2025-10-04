package com.example.ecommerce.catalog.app;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.ecommerce.catalog.domain.Brand;
import com.example.ecommerce.catalog.domain.Category;
import com.example.ecommerce.catalog.domain.Product;
import com.example.ecommerce.catalog.infra.BrandRepository;
import com.example.ecommerce.catalog.infra.CategoryRepository;
import com.example.ecommerce.catalog.infra.ProductRepository;
import com.example.ecommerce.catalog.web.dto.category.PageResponseDto;
import com.example.ecommerce.catalog.web.dto.product.CreateProductResponseDto;
import com.example.ecommerce.catalog.web.dto.product.PartialProductUpdateRequestDto;
import com.example.ecommerce.catalog.web.dto.product.SearchProductResponseDto;
import com.example.ecommerce.catalog.web.dto.product.UpdateProductRequestDto;
import com.example.ecommerce.common.specification.ProductSpecifications;

@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository,
                          BrandRepository brandRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
    }

    public CreateProductResponseDto createProduct(String productName,
                                                  String description,
                                                  BigDecimal price,
                                                  UUID brandId,
                                                  UUID categoryId,
                                                  String sku,
                                                  BigDecimal rating,
                                                  Product.Status status,
                                                  String size,
                                                  String color,
                                                  long stockQuantity,
                                                  BigDecimal weight) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new EntityNotFoundException("Brand not found: " + brandId));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + categoryId));

        Product product = new Product.Builder().setProductName(productName).setDescription(description).setPrice(price)
                .setBrand(brand).setCategory(category).setSku(sku).setRating(rating).setStatus(status).setSize(size)
                .setColor(color).setStockQuantity(stockQuantity).setWeight(weight).build();

        Product createdProduct = productRepository.save(product);

        return new CreateProductResponseDto(createdProduct.getId(), createdProduct.getProductName(),
                createdProduct.getPrice(), createdProduct.getStockQuantity(),
                createdProduct.getStatus());
    }

    public Page<Product> getPaginatedProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAll(pageable);
    }

    public Product getProductById(UUID id) {
        return productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    public Product putProduct(UUID id, UpdateProductRequestDto payload) {
        Product currentProduct = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));

        Brand brand = brandRepository.findById(payload.brandId())
                .orElseThrow(() -> new EntityNotFoundException("Brand not found: " + payload.brandId()));
        currentProduct.assignToBrand(brand);

        Category category = categoryRepository.findById(payload.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + payload.categoryId()));
        currentProduct.assignToCategory(category);

        currentProduct.updateProductName(payload.productName());
        currentProduct.updateDescription(payload.description());
        currentProduct.updatePrice(payload.price());
        currentProduct.updateWeight(payload.weight());
        currentProduct.updateStock(payload.stockQuantity());
        currentProduct.updateColor(payload.color());
        currentProduct.updateSize(payload.size());
        currentProduct.updateSku(payload.sku());

        return productRepository.save(currentProduct);
    }

    public Product updateProductPartial(UUID id, PartialProductUpdateRequestDto payload) {
        Product currentProduct = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));

        if (payload.brandId() != null) {
            Brand brand = brandRepository.findById(payload.brandId())
                    .orElseThrow(() -> new EntityNotFoundException("Brand not found: " + payload.brandId()));
            currentProduct.assignToBrand(brand);
        }

        if (payload.categoryId() != null) {
            Category category = categoryRepository.findById(payload.categoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found: " + payload.categoryId()));
            currentProduct.assignToCategory(category);
        }

        updateIfPresent(payload.productName(), currentProduct::updateProductName);
        updateIfPresent(payload.description(), currentProduct::updateDescription);
        updateIfPresent(payload.price(), currentProduct::updatePrice);
        updateIfPresent(payload.weight(), currentProduct::updateWeight);

        if (payload.stockQuantity() != null) {
            currentProduct.updateStock(payload.stockQuantity());
        }

        updateIfPresent(payload.color(), currentProduct::updateColor);
        updateIfPresent(payload.size(), currentProduct::updateSize);
        updateIfPresent(payload.sku(), currentProduct::updateSku);

        return productRepository.save(currentProduct);
    }

    public PageResponseDto<SearchProductResponseDto> searchProducts(String searchQuery, boolean inStock, int page,
                                                                    int limit, String sort) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by(sort));
        Specification<Product> spec = Specification.where(null);

        if (searchQuery != null && !searchQuery.isBlank()) {
            spec = spec.and(ProductSpecifications.hasNameOrDescriptionLike(searchQuery));
        }
        if (inStock) {
            spec = spec.and(ProductSpecifications.isInStock(true));
        }
        Page<Product> productPage = productRepository.findAll(spec, pageable);
        List<SearchProductResponseDto> dtoList = productPage.getContent().stream().map(this::toDto).toList();

        PageResponseDto<SearchProductResponseDto> response = new PageResponseDto<>();
        response.setContent(dtoList);
        response.setNumber(productPage.getNumber());
        response.setSize(productPage.getSize());
        response.setTotalElements(productPage.getTotalElements());
        response.setTotalPages(productPage.getTotalPages());
        response.setHasNext(productPage.hasNext());
        response.setHasPrevious(productPage.hasPrevious());
        return response;
    }

    // -------------------- private helpers --------------------

    private SearchProductResponseDto toDto(Product product) {
        return new SearchProductResponseDto(product.getId(), product.getProductName(), product.getDescription(),
                product.getPrice(), product.getStockQuantity(), product.getStatus(),
                product.getPrimaryImage());
    }

    // Utility method for cleaner updates
    private <T> void updateIfPresent(T newValue, java.util.function.Consumer<T> setter) {
        if (newValue != null) {
            setter.accept(newValue);
        }
    }
}
