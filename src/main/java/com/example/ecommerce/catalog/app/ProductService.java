package com.example.ecommerce.catalog.app;

import com.example.ecommerce.catalog.domain.Category;
import com.example.ecommerce.catalog.domain.DummyProduct;
import com.example.ecommerce.catalog.domain.Product;
import com.example.ecommerce.catalog.domain.ProductImage;
import com.example.ecommerce.catalog.infra.CategoryRepository;
import com.example.ecommerce.catalog.infra.ProductRepository;
import com.example.ecommerce.catalog.web.dto.product.CreateProductResponseDTO;
import com.example.ecommerce.catalog.web.dto.product.ProductImagePayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;
    private final DummyProductService dummyService;
    private final CategoryService categoryService;

    public ProductService(ProductRepository productRepo, CategoryRepository categoryRepo,
            DummyProductService dummyService, CategoryService categoryService) {
        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
        this.dummyService = dummyService;
        this.categoryService = categoryService;
    }

    public CreateProductResponseDTO create(String name, String description, BigDecimal price, String brand,
            BigDecimal weight, Long stockQuantity, UUID categoryId, List<ProductImagePayload> productImagelist) {

        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Product product = new Product.Builder().productName(name).description(description).brandName(brand).price(price)
                .weight(weight).status(Product.Status.OUT_OF_STOCK).category(category).build();
        for (ProductImagePayload image : productImagelist) {
            product.addImage(new ProductImage(image.getImageUrl(), image.isPrimary(), product));
        }
        productRepo.save(product);
        return new CreateProductResponseDTO(product.getId(), product.getBrandName(), product.getDescription(),
                product.getPrice(), product.getWeight(), product.getStockQuantity(),
                product.getCategory(), product.getProductImages());
    }

    public CreateProductResponseDTO create(String name, String description, BigDecimal price, String brand,
            BigDecimal weight, Long stockQuantity, UUID categoryId) {
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Product product = new Product.Builder().productName(name).description(description).brandName(brand).price(price)
                .weight(weight).category(category).status(Product.Status.OUT_OF_STOCK).build();
        productRepo.save(product);

        return new CreateProductResponseDTO(product.getId(), product.getBrandName(), product.getDescription(),
                product.getPrice(), product.getWeight(), product.getStockQuantity(),
                product.getCategory());
    }

    public void create(String name, String description, BigDecimal price, String brand, BigDecimal weight,
            Long stockQuantity,
            UUID categoryId, boolean productFlag, List<ProductImagePayload> productImagelist) {
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Product product = new Product.Builder().productName(name).description(description).brandName(brand).price(price)
                .weight(weight).status(Product.Status.OUT_OF_STOCK).category(category).build();
        for (ProductImagePayload image : productImagelist) {
            product.addImage(new ProductImage(image.getImageUrl(), image.isPrimary(), product));
        }
        productRepo.save(product);
    }

    public Page<Product> getPaginatedProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepo.findAll(pageable);
    }

    public Product getProductById(UUID id) {
        return productRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    public List<Product> findByPriceRange(BigDecimal min, BigDecimal max) {
        return productRepo.findAll().stream()
                .filter(p -> p.getPrice().compareTo(min) >= 0 && p.getPrice().compareTo(max) <= 0).toList();
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public void dumpDummyData() throws IOException, InterruptedException {
        List<DummyProduct> dummies = this.dummyService.getAllProducts().stream()
                .filter(distinctByKey(product -> product.getTitle() != null ? product.getTitle() : ""))
                .collect(Collectors.toList());

        // Log all product brand names with null-safe sorting
        logger.info("Starting to process {} dummy products", dummies.size());
        List<String> brandNames = dummies.stream()
                .map(DummyProduct::getBrand)
                .filter(brand -> brand != null)
                .distinct()
                .sorted()
                .toList();

        logger.info("Found {} unique brands: {}", brandNames.size(), brandNames);

        for (DummyProduct dp : dummies) {
            logger.debug("Processing product: {} - Brand: {}", dp.getTitle(), dp.getBrand());

            String categoryName = dp.getCategory();
            String description = dp.getCategory();
            Category category = this.categoryRepo.findByName(categoryName)
                    .orElseGet(() -> this.categoryService.create(categoryName, description));
            ProductImagePayload image = new ProductImagePayload(dp.getImages().get(0), true);
            this.create(dp.getTitle(), dp.getDescription(), dp.getPrice(), dp.getBrand(), dp.getWeight(), dp.getStock(),
                    category.getId(), true, List.of(image));
        }

        logger.info("Completed processing all dummy products");
    }
}
