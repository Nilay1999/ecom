package com.example.ecommerce.catalog.app;

import com.example.ecommerce.catalog.domain.Product;
import com.example.ecommerce.catalog.domain.ProductImage;
import com.example.ecommerce.catalog.infra.ProductImageRepository;
import com.example.ecommerce.catalog.infra.ProductRepository;
import com.example.ecommerce.catalog.web.exception.product.ProductNotFoundException;
import com.example.ecommerce.catalog.web.exception.productimage.InvalidImageDataException;
import com.example.ecommerce.catalog.web.exception.productimage.PrimaryImageConstraintException;
import com.example.ecommerce.catalog.web.exception.productimage.ProductImageNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ProductImageService {

    private final ProductImageRepository productImageRepo;
    private final ProductRepository productRepo;

    public ProductImageService(ProductImageRepository productImageRepo, ProductRepository productRepo) {
        this.productImageRepo = productImageRepo;
        this.productRepo = productRepo;
    }

    // -------------------- CRUD Operations --------------------

    public ProductImage create(UUID productId, String imageUrl, boolean isPrimary, Integer displayOrder,
            String altText) {
        Product product = findProductById(productId);

        try {
            // Validate image data
            ProductImage.validateImageUrl(imageUrl);
            ProductImage.validateDisplayOrder(displayOrder);
            ProductImage.validateAltText(altText);

            // Handle primary image logic
            if (isPrimary) {
                ensureOnlyOnePrimaryImage(product);
            }

            // Set display order if not provided
            if (displayOrder == null) {
                displayOrder = getNextDisplayOrder(productId);
            }

            ProductImage productImage = new ProductImage.Builder()
                    .product(product)
                    .imageUrl(imageUrl)
                    .isPrimary(isPrimary)
                    .displayOrder(displayOrder)
                    .altText(altText)
                    .build();

            return productImageRepo.save(productImage);

        } catch (IllegalArgumentException e) {
            throw new InvalidImageDataException("Invalid image data: " + e.getMessage(), e);
        }
    }

    public ProductImage findById(UUID id) {
        return productImageRepo.findById(id)
                .orElseThrow(() -> new ProductImageNotFoundException("Product image not found with id: " + id));
    }

    public ProductImage update(UUID id, String imageUrl, Boolean isPrimary, Integer displayOrder, String altText) {
        ProductImage productImage = findById(id);

        try {
            // Update image URL if provided
            if (imageUrl != null) {
                productImage.updateImageUrl(imageUrl);
            }

            // Update display order if provided
            if (displayOrder != null) {
                productImage.updateDisplayOrder(displayOrder);
            }

            // Update alt text if provided
            if (altText != null) {
                productImage.updateAltText(altText);
            }

            // Handle primary image logic
            if (isPrimary != null) {
                if (isPrimary) {
                    ensureOnlyOnePrimaryImage(productImage.getProduct());
                    productImage.markAsPrimary();
                } else {
                    productImage.markAsSecondary();
                }
            }

            return productImageRepo.save(productImage);

        } catch (IllegalArgumentException e) {
            throw new InvalidImageDataException("Invalid image data: " + e.getMessage(), e);
        }
    }

    public void delete(UUID id) {
        ProductImage productImage = findById(id);

        // Check if this is the only primary image
        if (productImage.isPrimary()) {
            long imageCount = productImageRepo.countByProduct(productImage.getProduct());
            if (imageCount > 1) {
                throw new PrimaryImageConstraintException(
                        "Cannot delete the primary image when other images exist. Set another image as primary first.");
            }
        }

        productImageRepo.delete(productImage);
    }

    // -------------------- Product Image Management --------------------

    public List<ProductImage> getProductImages(UUID productId) {
        validateProductExists(productId);
        return productImageRepo.findByProductIdOrderByDisplayOrderAsc(productId);
    }

    public Optional<ProductImage> getPrimaryImage(UUID productId) {
        validateProductExists(productId);
        return productImageRepo.findPrimaryImageByProductId(productId);
    }

    public ProductImage setPrimaryImage(UUID imageId) {
        ProductImage productImage = findById(imageId);

        // Remove primary status from other images of the same product
        ensureOnlyOnePrimaryImage(productImage.getProduct());

        // Set this image as primary
        productImage.markAsPrimary();
        return productImageRepo.save(productImage);
    }

    public void removePrimaryStatus(UUID imageId) {
        ProductImage productImage = findById(imageId);

        if (!productImage.isPrimary()) {
            throw new IllegalStateException("Image is not currently set as primary");
        }

        productImage.markAsSecondary();
        productImageRepo.save(productImage);
    }

    // -------------------- Image Ordering --------------------

    public List<ProductImage> reorderImages(UUID productId, List<UUID> imageIds) {
        validateProductExists(productId);
        List<ProductImage> productImages = productImageRepo.findByProductIdOrderByDisplayOrderAsc(productId);

        // Validate that all provided image IDs belong to the product
        if (imageIds.size() != productImages.size()) {
            throw new InvalidImageDataException("Number of image IDs doesn't match the number of product images");
        }

        for (int i = 0; i < imageIds.size(); i++) {
            UUID imageId = imageIds.get(i);
            ProductImage image = productImages.stream()
                    .filter(pi -> pi.getId().equals(imageId))
                    .findFirst()
                    .orElseThrow(() -> new ProductImageNotFoundException(
                            "Image with id " + imageId + " not found for this product"));

            image.updateDisplayOrder(i);
        }

        return productImageRepo.saveAll(productImages);
    }

    public ProductImage moveImageUp(UUID imageId) {
        ProductImage productImage = findById(imageId);
        List<ProductImage> productImages = productImageRepo
                .findByProductIdOrderByDisplayOrderAsc(productImage.getProduct().getId());

        int currentIndex = -1;
        for (int i = 0; i < productImages.size(); i++) {
            if (productImages.get(i).getId().equals(imageId)) {
                currentIndex = i;
                break;
            }
        }

        if (currentIndex <= 0) {
            throw new IllegalStateException("Image is already at the top position");
        }

        // Swap display orders
        ProductImage previousImage = productImages.get(currentIndex - 1);
        Integer tempOrder = productImage.getDisplayOrder();
        productImage.updateDisplayOrder(previousImage.getDisplayOrder());
        previousImage.updateDisplayOrder(tempOrder);

        productImageRepo.saveAll(List.of(productImage, previousImage));
        return productImage;
    }

    public ProductImage moveImageDown(UUID imageId) {
        ProductImage productImage = findById(imageId);
        List<ProductImage> productImages = productImageRepo
                .findByProductIdOrderByDisplayOrderAsc(productImage.getProduct().getId());

        int currentIndex = -1;
        for (int i = 0; i < productImages.size(); i++) {
            if (productImages.get(i).getId().equals(imageId)) {
                currentIndex = i;
                break;
            }
        }

        if (currentIndex >= productImages.size() - 1) {
            throw new IllegalStateException("Image is already at the bottom position");
        }

        // Swap display orders
        ProductImage nextImage = productImages.get(currentIndex + 1);
        Integer tempOrder = productImage.getDisplayOrder();
        productImage.updateDisplayOrder(nextImage.getDisplayOrder());
        nextImage.updateDisplayOrder(tempOrder);

        productImageRepo.saveAll(List.of(productImage, nextImage));
        return productImage;
    }

    // -------------------- Image Validation --------------------

    public boolean isValidImageUrl(String imageUrl) {
        try {
            ProductImage.validateImageUrl(imageUrl);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean hasPrimaryImage(UUID productId) {
        validateProductExists(productId);
        return productImageRepo.existsPrimaryImageByProductId(productId);
    }

    public long getImageCount(UUID productId) {
        validateProductExists(productId);
        return productImageRepo.countByProductId(productId);
    }

    // -------------------- Search and Filtering --------------------

    public List<ProductImage> searchByAltText(String altText) {
        return productImageRepo.findByAltTextContainingIgnoreCase(altText);
    }

    public List<ProductImage> getImagesByDisplayOrderRange(UUID productId, Integer minOrder, Integer maxOrder) {
        validateProductExists(productId);
        return productImageRepo.findByProductIdAndDisplayOrderBetween(productId, minOrder, maxOrder);
    }

    // -------------------- Bulk Operations --------------------

    public void deleteAllProductImages(UUID productId) {
        validateProductExists(productId);
        productImageRepo.deleteByProductId(productId);
    }

    public List<ProductImage> createMultipleImages(UUID productId, List<ImageCreationRequest> requests) {
        Product product = findProductById(productId);

        // Validate all requests first
        for (ImageCreationRequest request : requests) {
            try {
                ProductImage.validateImageUrl(request.imageUrl());
                ProductImage.validateDisplayOrder(request.displayOrder());
                ProductImage.validateAltText(request.altText());
            } catch (IllegalArgumentException e) {
                throw new InvalidImageDataException("Invalid image data in request: " + e.getMessage(), e);
            }
        }

        // Check for multiple primary images in the request
        long primaryCount = requests.stream().mapToLong(r -> r.isPrimary() ? 1 : 0).sum();
        if (primaryCount > 1) {
            throw new PrimaryImageConstraintException("Only one image can be set as primary");
        }

        // If setting a primary image, ensure no existing primary
        if (primaryCount > 0) {
            ensureOnlyOnePrimaryImage(product);
        }

        List<ProductImage> images = requests.stream()
                .map(request -> new ProductImage.Builder()
                        .product(product)
                        .imageUrl(request.imageUrl())
                        .isPrimary(request.isPrimary())
                        .displayOrder(request.displayOrder() != null ? request.displayOrder()
                                : getNextDisplayOrder(productId))
                        .altText(request.altText())
                        .build())
                .toList();

        return productImageRepo.saveAll(images);
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

    private void ensureOnlyOnePrimaryImage(Product product) {
        Optional<ProductImage> existingPrimary = productImageRepo.findByProductAndIsPrimaryTrue(product);
        if (existingPrimary.isPresent()) {
            ProductImage primaryImage = existingPrimary.get();
            primaryImage.markAsSecondary();
            productImageRepo.save(primaryImage);
        }
    }

    private Integer getNextDisplayOrder(UUID productId) {
        return productImageRepo.findMaxDisplayOrderByProductId(productId) + 1;
    }

    // -------------------- DTOs --------------------

    public record ImageCreationRequest(
            String imageUrl,
            boolean isPrimary,
            Integer displayOrder,
            String altText) {
    }
}