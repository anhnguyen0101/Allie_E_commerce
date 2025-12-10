package com.example.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Product;

import java.math.BigDecimal;

/**
 * Repository for Product entities.
 *
 * Main concept:
 * - Expose query methods for paginated search over products using Spring Data JPA method names.
 *
 * Responsibilities:
 * - Provide methods to search by product name (contains, case-insensitive).
 * - Provide methods to search by category name and price range with pagination.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find products whose name contains the given text (case-insensitive) with pagination
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Find products whose category name contains the given text (case-insensitive) with pagination
    Page<Product> findByCategory_NameContainingIgnoreCase(String categoryName, Pageable pageable);

    // Find products by category id with pagination
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    // Find products by category id and price range with pagination
    Page<Product> findByCategoryIdAndPriceBetween(Long categoryId, BigDecimal min, BigDecimal max, Pageable pageable);

    // Find products by category name (contains) and price between min and max with pagination
    Page<Product> findByCategory_NameContainingIgnoreCaseAndPriceBetween(String categoryName, BigDecimal min,
            BigDecimal max, Pageable pageable);

    // Find products whose price is between min and max with pagination
    Page<Product> findByPriceBetween(BigDecimal min, BigDecimal max, Pageable pageable);

    // Find all products with their categories in one query
    @EntityGraph(attributePaths = {"category"})
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.category")
    Page<Product> findAllWithCategory(Pageable pageable);

    // Find products by name, category, price range with pagination
    @EntityGraph(attributePaths = {"category"})
    @Query("SELECT p FROM Product p " +
           "WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Product> searchWithCategory(
        @Param("name") String name,
        @Param("categoryId") Long categoryId,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        Pageable pageable
    );

    @EntityGraph(attributePaths = {"category"})
    @Override
    Page<Product> findAll(Pageable pageable);
}
