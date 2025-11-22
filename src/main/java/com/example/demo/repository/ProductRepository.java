package com.example.demo.repository;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Product;

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
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find products whose name contains the given text (case-insensitive) with pagination
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Find products whose category name contains the given text (case-insensitive) with pagination
    Page<Product> findByCategory_NameContainingIgnoreCase(String categoryName, Pageable pageable);

    // Find products by category name (contains) and price between min and max with pagination
    Page<Product> findByCategory_NameContainingIgnoreCaseAndPriceBetween(String categoryName, BigDecimal min,
            BigDecimal max, Pageable pageable);

    // Find products whose price is between min and max with pagination
    Page<Product> findByPriceBetween(BigDecimal min, BigDecimal max, Pageable pageable);

}
