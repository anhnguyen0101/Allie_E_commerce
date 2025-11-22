package com.example.demo.service;

import java.util.List;
import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

import com.example.demo.entity.Product;
import com.example.demo.dto.product.ProductResponse;
import com.example.demo.repository.ProductRepository;

/**
 * Service layer for Product CRUD operations.
 *
 * Main concept:
 * - Encapsulate business logic and data access for products behind a clean API used by controllers.
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    public Product create(Product product) {
        return productRepository.save(product);
    }

    /**
     * Search products with pagination and optional filters.
     *
     * Main concept:
     * - Provide a single entry point for paged product searches that maps repository Page<Product>
     *   to Page<ProductResponse>.
     *
     * Priority of filters (simple approach):
     * - If `name` is provided, performs a name-based search.
     * - Else if `categoryName` and both min/max prices are provided, searches by category+price range.
     * - Else if `categoryName` only is provided, searches by category name.
     * - Else if both min/max provided, searches by price range.
     * - Otherwise returns all products paged.
     */
    public Page<ProductResponse> search(String name, String categoryName, BigDecimal minPrice, BigDecimal maxPrice,
            Pageable pageable) {
        // name-based search
        if (name != null && !name.isBlank()) {
            return productRepository.findByNameContainingIgnoreCase(name, pageable).map(this::toResponse);
        }

        // category + price range search
        if (categoryName != null && !categoryName.isBlank() && minPrice != null && maxPrice != null) {
            return productRepository.findByCategory_NameContainingIgnoreCaseAndPriceBetween(categoryName, minPrice,
                    maxPrice, pageable).map(this::toResponse);
        }

        // category-only search
        if (categoryName != null && !categoryName.isBlank()) {
            return productRepository.findByCategory_NameContainingIgnoreCase(categoryName, pageable).map(this::toResponse);
        }

        // price-range only search
        if (minPrice != null && maxPrice != null) {
            return productRepository.findByPriceBetween(minPrice, maxPrice, pageable).map(this::toResponse);
        }

        // default: return all paged
        return productRepository.findAll(pageable).map(this::toResponse);
    }

    /* create a ProductResponse DTO from a Product entity */
    public ProductResponse toResponse(Product product) {
        /* return a built ProductResponse using fields from the product */
        return ProductResponse.builder()
                /* set id on response */ .id(product.getId())
                /* set name on response */ .name(product.getName())
                /* set description on response */ .description(product.getDescription())
                /* set price on response */ .price(product.getPrice())
                /* set categoryName on response; handle null category */ .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                /* build the DTO */ .build();
    }

    public Product update(Long id, Product product) {
        Product existing = findById(id);
        existing.setName(product.getName());
        existing.setPrice(product.getPrice());
        existing.setDescription(product.getDescription());
        existing.setCategory(product.getCategory());
        return productRepository.save(existing);
    }

    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        productRepository.deleteById(id);
    }
}
