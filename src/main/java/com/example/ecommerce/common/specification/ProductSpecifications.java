package com.example.ecommerce.common.specification;

import com.example.ecommerce.catalog.domain.Product;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecifications {
    public static Specification<Product> hasNameOrDescriptionLike(String query) {
        return (root, cq, cb) -> cb.or(cb.like(cb.lower(root.get("productName")), "%" + query.toLowerCase() + "%"),
                                       cb.like(cb.lower(root.get("description")), "%" + query.toLowerCase() + "%"));
    }

    public static Specification<Product> isInStock(boolean inStock) {
        return (root, cq, cb) -> {
            if (inStock) {
                return cb.greaterThan(root.get("stockQuantity"), 0);
            } else {
                return cb.conjunction();
            }
        };
    }
}

