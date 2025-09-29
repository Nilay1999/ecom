package com.example.ecommerce.catalog.infra;

import com.example.ecommerce.catalog.domain.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findBySlug(String slug);

    Optional<Category> findByName(String name);

    boolean existsBySlug(String slug);

    boolean existsByName(String name);

    @Query("SELECT c FROM Category c WHERE EXISTS (SELECT 1 FROM Category child WHERE child.parent = c)")
    Page<Category> findCategoriesWithChildren(Pageable pageable);

    List<Category> findByParent(Category parent);
}
