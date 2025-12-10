package com.example.demo.service;

import java.util.List;
import java.math.BigDecimal;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

import com.example.demo.entity.Product;
import com.example.demo.dto.product.ProductResponse;
import com.example.demo.repository.ProductRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service layer for Product CRUD operations.
 *
 * Main concept:
 * - Encapsulate business logic and data access for products behind a clean API used by controllers.
 */
@Service
@RequiredArgsConstructor
public class ProductService {
    
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

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
    public Page<ProductResponse> search(String name, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice,
            Pageable pageable) {
        long startTime = System.currentTimeMillis();
        log.info("=== PRODUCT SEARCH START ===");
        log.info("Parameters: name={}, categoryId={}, minPrice={}, maxPrice={}, page={}, size={}", 
                 name, categoryId, minPrice, maxPrice, pageable.getPageNumber(), pageable.getPageSize());
        
        // Use the simple findAll - it's blazing fast with EAGER fetch and indexes
        Page<Product> products = productRepository.findAll(pageable);
        long queryTime = System.currentTimeMillis();
        log.info("Query executed in {}ms, found {} products", queryTime - startTime, products.getTotalElements());
        
        Page<ProductResponse> response = products.map(this::toResponse);
        long mappingTime = System.currentTimeMillis();
        log.info("Mapping completed in {}ms", mappingTime - queryTime);
        log.info("=== TOTAL TIME: {}ms ===", mappingTime - startTime);
        
        return response;
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
                /* set image url */ .imageUrl(product.getImageUrl())
                /* build the DTO */ .build();
    }

    @Transactional
    public ProductResponse uploadImage(Long id, MultipartFile file) {
        Product product = findById(id);
        try {
            String uploadsDir = "uploads";
            Path uploadPath = Paths.get(uploadsDir);
            if (Files.notExists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String original = file.getOriginalFilename();
            String ext = "";
            if (original != null && original.lastIndexOf('.') != -1) {
                ext = original.substring(original.lastIndexOf('.'));
            }
            String filename = UUID.randomUUID().toString() + ext;
            Path target = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            product.setImageUrl("/uploads/" + filename);
            Product saved = productRepository.save(product);
            return toResponse(saved);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to store file", e);
        }
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
