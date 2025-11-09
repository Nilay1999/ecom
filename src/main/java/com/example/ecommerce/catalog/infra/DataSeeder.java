package com.example.ecommerce.catalog.infra;

import com.example.ecommerce.catalog.domain.Brand;
import com.example.ecommerce.catalog.domain.Category;
import com.example.ecommerce.catalog.domain.Product;
import com.example.ecommerce.catalog.domain.ProductImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * DataSeeder with UNIQUE products + product images.
 * Set TOTAL_PRODUCTS_TO_GENERATE between 500 and 1000.
 * 
 * This component no longer runs automatically on startup.
 * Use SeedDataScript to run seeding manually.
 */
@Component
@Profile("local")
public class DataSeeder {
    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    private static final int TOTAL_PRODUCTS_TO_GENERATE = 800;
    private static final int BATCH_SIZE = 100;

    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final Environment environment;

    private int brandsCreated = 0;
    private int categoriesCreated = 0;
    private int productsCreated = 0;

    public DataSeeder(BrandRepository brandRepository,
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            Environment environment) {
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.environment = environment;
    }

    public void seedData() {
        if (!isLocalProfile()) {
            logger.info("Seeding skipped - not running in 'local' profile");
            return;
        }

        logger.info("Starting realistic UNIQUE data seeding with images. Target products: {}",
                TOTAL_PRODUCTS_TO_GENERATE);
        long start = System.currentTimeMillis();

        List<Brand> brands = ensureBrands();
        List<Category> categories = ensureCategories();

        generateUniqueProductsWithImages(brands, categories, TOTAL_PRODUCTS_TO_GENERATE);

        long elapsed = System.currentTimeMillis() - start;
        logger.info("Seeding finished in {} ms — Brands created: {}, Categories created: {}, Products created: {}",
                elapsed, brandsCreated, categoriesCreated, productsCreated);
    }

    private boolean isLocalProfile() {
        return Arrays.asList(environment.getActiveProfiles()).contains("local");
    }

    private List<Brand> ensureBrands() {
        List<String[]> brandData = Arrays.asList(
                new String[] { "Apple", "Consumer electronics" },
                new String[] { "Samsung", "Electronics & appliances" },
                new String[] { "Sony", "Audio & visual products" },
                new String[] { "Nike", "Sportswear & footwear" },
                new String[] { "Adidas", "Sporting goods" },
                new String[] { "IKEA", "Home furniture" },
                new String[] { "Dell", "Computers & laptops" },
                new String[] { "HP", "Computers & printers" },
                new String[] { "Levi's", "Denim & apparel" },
                new String[] { "Gucci", "Luxury fashion" });

        List<Brand> toSave = new ArrayList<>();
        List<Brand> existing = brandRepository.findAll();

        for (String[] b : brandData) {
            String name = b[0];
            boolean exists = existing.stream().anyMatch(br -> br.getName().equalsIgnoreCase(name));
            if (!exists) {
                Brand brand = new Brand.Builder()
                        .setName(name)
                        .setDescription(b[1])
                        .setSlug(name.toLowerCase().replaceAll("[^a-z0-9]+", "-"))
                        .setActive(true)
                        .build();
                toSave.add(brand);
            }
        }

        if (!toSave.isEmpty()) {
            brandRepository.saveAll(toSave);
            brandsCreated = toSave.size();
            logger.info("Saved {} new brands", brandsCreated);
        } else {
            logger.info("Brands already present, nothing to add");
        }
        return brandRepository.findAll();
    }

    // --- ensure curated categories ---
    private List<Category> ensureCategories() {
        List<String> catNames = Arrays.asList(
                "Smartphones", "Laptops", "Headphones", "Televisions",
                "Watches", "Shoes", "Clothing", "Furniture", "Gaming", "Accessories");

        List<Category> toSave = new ArrayList<>();
        for (String name : catNames) {
            if (!categoryRepository.existsByName(name)) {
                Category c = new Category.Builder()
                        .setName(name)
                        .setDescription("Category: " + name)
                        .build();
                toSave.add(c);
            }
        }

        if (!toSave.isEmpty()) {
            categoryRepository.saveAll(toSave);
            categoriesCreated = toSave.size();
            logger.info("Saved {} new categories", categoriesCreated);
        } else {
            logger.info("Categories already present, nothing to add");
        }
        return categoryRepository.findAll();
    }

    // --- unique product generation with images ---
    private void generateUniqueProductsWithImages(List<Brand> brands, List<Category> categories, int total) {
        if (brands.isEmpty() || categories.isEmpty()) {
            logger.warn("Brands or categories missing — aborting product generation");
            return;
        }

        Map<String, String[]> brandModels = new HashMap<>();
        brandModels.put("Apple", new String[] { "iPhone", "MacBook", "iPad", "AirPods", "Watch" });
        brandModels.put("Samsung", new String[] { "Galaxy S", "Galaxy Note", "Galaxy Tab", "QLED TV", "Galaxy Watch" });
        brandModels.put("Sony", new String[] { "WH-Headphones", "Bravia", "Alpha Camera", "PlayStation", "Soundbar" });
        brandModels.put("Nike", new String[] { "Air Max", "Court Vision", "Running Shoe", "Training Shirt" });
        brandModels.put("Adidas", new String[] { "Ultraboost", "Superstar", "Running Tee", "Track Jacket" });
        brandModels.put("IKEA", new String[] { "MALM Bed", "POÄNG Chair", "BILLY Shelf", "LACK Table" });
        brandModels.put("Dell", new String[] { "XPS", "Inspiron", "Latitude", "Alienware" });
        brandModels.put("HP", new String[] { "Envy", "Pavilion", "Omen", "Spectre" });
        brandModels.put("Levi's", new String[] { "501 Jeans", "Trucker Jacket", "T-Shirt", "Slim Jeans" });
        brandModels.put("Gucci", new String[] { "Leather Belt", "Sneakers", "Handbag", "Sunglasses" });

        String[] modifiers = { "Pro", "Max", "Plus", "Lite", "2025", "Edition", "SE", "Ultra" };
        String[] colors = { "Black", "White", "Blue", "Gray", "Red", "Green", "Silver", "Gold", "Brown", "Navy" };
        String[] sizes = { "S", "M", "L", "XL", "Standard", "OneSize", "Compact", "Large" };

        // existing SKUs to avoid collision
        Set<String> existingSkus = productRepository.findAll().stream()
                .map(Product::getSku)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<Product> batch = new ArrayList<>(BATCH_SIZE);
        for (int i = 1; i <= total; i++) {
            Brand brand = brands.get((i - 1) % brands.size());
            Category category = categories.get(ThreadLocalRandom.current().nextInt(categories.size()));

            String brandName = brand.getName();
            String[] models = brandModels.getOrDefault(brandName, new String[] { "Model" });
            String model = models[ThreadLocalRandom.current().nextInt(models.length)];
            String modifier = modifiers[ThreadLocalRandom.current().nextInt(modifiers.length)];
            String color = colors[ThreadLocalRandom.current().nextInt(colors.length)];
            String size = sizes[ThreadLocalRandom.current().nextInt(sizes.length)];
            String idx = String.format("%06d", i);

            // build product name and SKU
            String productName = String.format("%s %s %s %s", brandName, model, modifier, idx).trim();

            String skuPrefix = safeAlnum(brandName).toUpperCase();
            if (skuPrefix.length() > 3)
                skuPrefix = skuPrefix.substring(0, 3);
            String skuMid = safeAlnum(model).toUpperCase();
            if (skuMid.length() > 3)
                skuMid = skuMid.substring(0, 3);
            String sku = String.format("%s-%s-%s", skuPrefix, skuMid, idx);

            int suffix = 0;
            while (existingSkus.contains(sku)) {
                suffix++;
                sku = String.format("%s-%s-%s-%d", skuPrefix, skuMid, idx, suffix);
            }
            existingSkus.add(sku);

            double base = 10 + (Math.abs(brandName.hashCode()) % 1000);
            BigDecimal price = BigDecimal.valueOf(Math.round((base + (i % 500) * 0.73) * 100.0) / 100.0);
            BigDecimal weight = BigDecimal.valueOf(Math.round((0.1 + (i % 200) * 0.11) * 100.0) / 100.0);
            long stock = (i % 10 == 0) ? 0 : (i % 200) + 1;
            Product.Status status = stock > 0 ? Product.Status.ACTIVE : Product.Status.OUT_OF_STOCK;

            String description = String.format("%s — %s %s by %s. Color: %s, Size: %s. Unique id: %s",
                    productName, modifier, model, brandName, color, size, idx);

            Product p = new Product.Builder()
                    .setProductName(productName)
                    .setDescription(description)
                    .setPrice(price)
                    .setWeight(weight)
                    .setStockQuantity(stock)
                    .setBrand(brand)
                    .setCategory(category)
                    .setSku(sku)
                    .setSize(size)
                    .setColor(color)
                    .setStatus(status)
                    .build();

            // --- generate images for this product ---
            // pick 1..4 images
            int imagesCount = 1 + ThreadLocalRandom.current().nextInt(4); // 1..4
            int displayOrder = 0;
            for (int imgIndex = 1; imgIndex <= imagesCount; imgIndex++) {
                // primary = first image
                boolean isPrimary = (imgIndex == 1);

                // choose dimensions (primary larger)
                int width = isPrimary ? 1200 : (imgIndex == 2 ? 800 : 400);
                int height = isPrimary ? 1200 : (imgIndex == 2 ? 800 : 400);

                // Dummy image URL — unique per SKU + imgIndex
                // Example:
                // https://dummyimage.com/1200x1200/cccccc/000000.png&text=APP-IPH-000001-1
                String imageUrl = String.format("https://dummyimage.com/%dx%d/cccccc/000000.png&text=%s-%d",
                        width, height, urlEncode(sku), imgIndex);

                long fileSize = 10_000 + ThreadLocalRandom.current().nextInt(490_000); // 10KB .. 500KB
                String mimeType = "image/png";
                String imageType = (isPrimary ? "MAIN" : (imgIndex == imagesCount ? "THUMBNAIL" : "GALLERY"));
                String altText = String.format("%s - image %d", productName, imgIndex);

                // create ProductImage and attach to product using addImage (maintains both
                // sides)
                ProductImage img = new ProductImage.Builder()
                        .imageUrl(imageUrl)
                        .product(p) // it's fine to set product here; addImage will set it again
                        .isPrimary(isPrimary)
                        .displayOrder(displayOrder++)
                        .altText(altText)
                        .imageType(imageType)
                        .fileSize(fileSize)
                        .mimeType(mimeType)
                        .dimensions(width, height)
                        .build();

                // Keep bidirectional sync
                p.addImage(img);
            }

            batch.add(p);

            if (batch.size() >= BATCH_SIZE) {
                saveBatch(batch);
                batch.clear();
            }
        }

        if (!batch.isEmpty())
            saveBatch(batch);
    }

    private void saveBatch(List<Product> batch) {
        try {
            productRepository.saveAll(batch);
            productsCreated += batch.size();
            logger.info("Saved batch: {} products (total so far: {})", batch.size(), productsCreated);
        } catch (Exception e) {
            logger.error("Failed to save product batch: {}", e.getMessage(), e);
        }
    }

    private static String safeAlnum(String s) {
        if (s == null)
            return "XXX";
        String cleaned = s.replaceAll("[^A-Za-z0-9]", "");
        return cleaned.isEmpty() ? "XXX" : cleaned;
    }

    private static String urlEncode(String s) {
        if (s == null)
            return "";
        return s.replaceAll(" ", "%20").replaceAll("\\+", "%2B");
    }
}
