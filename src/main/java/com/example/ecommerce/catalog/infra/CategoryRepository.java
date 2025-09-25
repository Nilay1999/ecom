package com.example.ecommerce.catalog.infra;

import com.example.ecommerce.catalog.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID>, JpaSpecificationExecutor<Category> {

        // Existing methods
        Optional<Category> findBySlug(String slug);

        Optional<Category> findByName(String name);

        boolean existsBySlug(String slug);

        boolean existsByName(String name);

        /**
         * Find categories by name (case-insensitive partial match)
         */
        @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
        List<Category> findByNameContainingIgnoreCase(@Param("name") String name);

        /**
         * Find root categories (categories without parent)
         */
        @Query("SELECT c FROM Category c WHERE c.parent IS NULL ORDER BY c.name ASC")
        List<Category> findRootCategories();

        /**
         * Find subcategories of a specific category
         */
        List<Category> findByParent(Category parent);

        /**
         * Find subcategories by parent ID
         */
        @Query("SELECT c FROM Category c WHERE c.parent.id = :parentId ORDER BY c.name ASC")
        List<Category> findByParentId(@Param("parentId") UUID parentId);

        /**
         * Find all descendants of a category (recursive)
         */
        @Query(value = "WITH RECURSIVE category_tree AS (" +
                        "SELECT id, name, slug, description, parent_id, 0 as level " +
                        "FROM categories WHERE id = :categoryId " +
                        "UNION ALL " +
                        "SELECT c.id, c.name, c.slug, c.description, c.parent_id, ct.level + 1 " +
                        "FROM categories c " +
                        "INNER JOIN category_tree ct ON c.parent_id = ct.id " +
                        ") " +
                        "SELECT c.* FROM categories c " +
                        "INNER JOIN category_tree ct ON c.id = ct.id " +
                        "WHERE ct.level > 0 " +
                        "ORDER BY ct.level, c.name", nativeQuery = true)
        List<Category> findAllDescendants(@Param("categoryId") UUID categoryId);

        /**
         * Find categories by depth level
         */
        @Query(value = "WITH RECURSIVE category_levels AS (" +
                        "SELECT id, name, slug, parent_id, 0 as level " +
                        "FROM categories WHERE parent_id IS NULL " +
                        "UNION ALL " +
                        "SELECT c.id, c.name, c.slug, c.parent_id, cl.level + 1 " +
                        "FROM categories c " +
                        "INNER JOIN category_levels cl ON c.parent_id = cl.id" +
                        ") " +
                        "SELECT c.* FROM categories c " +
                        "INNER JOIN category_levels cl ON c.id = cl.id " +
                        "WHERE cl.level = :level " +
                        "ORDER BY c.name", nativeQuery = true)
        List<Category> findByDepthLevel(@Param("level") int level);

        /**
         * Find categories that have subcategories
         */
        @Query("SELECT DISTINCT c FROM Category c WHERE SIZE(c.subCategories) > 0")
        List<Category> findCategoriesWithSubcategories();

        /**
         * Find categories that have products
         */
        @Query("SELECT DISTINCT c FROM Category c WHERE SIZE(c.products) > 0")
        List<Category> findCategoriesWithProducts();

        /**
         * Find categories without products (leaf categories with no products)
         */
        @Query("SELECT c FROM Category c WHERE SIZE(c.products) = 0 AND SIZE(c.subCategories) = 0")
        List<Category> findEmptyLeafCategories();

        /**
         * Count subcategories for a category
         */
        @Query("SELECT COUNT(c) FROM Category c WHERE c.parent.id = :parentId")
        long countSubcategoriesByParentId(@Param("parentId") UUID parentId);

        /**
         * Count products in category (direct products only)
         */
        @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId")
        long countProductsByCategoryId(@Param("categoryId") UUID categoryId);

        /**
         * Search categories by name or description
         */
        @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
        List<Category> searchByNameOrDescription(@Param("searchTerm") String searchTerm);

        /**
         * Find category path from root to specified category
         */
        @Query(value = "WITH RECURSIVE category_path AS (" +
                        "SELECT id, name, slug, parent_id, 0 as level, CAST(name AS VARCHAR(1000)) as path " +
                        "FROM categories WHERE id = :categoryId " +
                        "UNION ALL " +
                        "SELECT c.id, c.name, c.slug, c.parent_id, cp.level + 1, CONCAT(c.name, ' > ', cp.path) " +
                        "FROM categories c " +
                        "INNER JOIN category_path cp ON c.id = cp.parent_id" +
                        ") " +
                        "SELECT c.* FROM categories c " +
                        "INNER JOIN category_path cp ON c.id = cp.id " +
                        "ORDER BY cp.level DESC", nativeQuery = true)
        List<Category> findCategoryPath(@Param("categoryId") UUID categoryId);

        /**
         * Check if slug exists for different category (for updates)
         */
        @Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.slug = :slug AND c.id != :categoryId")
        boolean existsBySlugAndIdNot(@Param("slug") String slug, @Param("categoryId") UUID categoryId);

        /**
         * Check if name exists for different category (for updates)
         */
        @Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.name = :name AND c.id != :categoryId")
        boolean existsByNameAndIdNot(@Param("name") String name, @Param("categoryId") UUID categoryId);

        /**
         * Find categories ordered by product count (descending)
         */
        @Query("SELECT c FROM Category c LEFT JOIN c.products p GROUP BY c ORDER BY COUNT(p) DESC")
        List<Category> findCategoriesOrderByProductCountDesc();

        /**
         * Find categories with minimum product count
         */
        @Query("SELECT c FROM Category c LEFT JOIN c.products p GROUP BY c HAVING COUNT(p) >= :minCount")
        List<Category> findCategoriesWithMinProductCount(@Param("minCount") long minCount);
}
