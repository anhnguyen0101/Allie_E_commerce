package com.example.demo.controller;

import java.net.URI;
import java.util.List;
import java.math.BigDecimal;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import com.example.demo.service.ProductService;
import com.example.demo.entity.Product;
import com.example.demo.dto.product.ProductResponse;
import com.example.demo.dto.product.ProductRequest;

/**
 * REST controller exposing CRUD endpoints for Products.
 *
 * Endpoints:
 * - GET  /api/products         -> list all products
 * - GET  /api/products/{id}    -> get product by id
 * - POST /api/products         -> create a new product
 * - PUT  /api/products/{id}    -> update existing product
 * - DELETE /api/products/{id}  -> delete product
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final com.example.demo.service.CategoryService categoryService;

    @GetMapping
    public List<ProductResponse> getAll() {
        List<Product> products = productService.findAll();
        return products.stream().map(productService::toResponse).collect(Collectors.toList());
    }

    /**
     * Search products with pagination and optional filters.
     *
     * Query parameters:
     * - name: search by product name (contains, case-insensitive)
     * - category: search by category name (contains, case-insensitive)
     * - minPrice, maxPrice: price range filter
     *
     * Accepts a Spring Data `Pageable` to control page/size/sort.
     * Returns a Page of `ProductResponse` DTOs.
     */
    @GetMapping("/search")
    public Page<ProductResponse> search(
            @RequestParam(required = false) String name,
            @RequestParam(value = "category", required = false) String categoryName,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            Pageable pageable) {
        return productService.search(name, categoryName, minPrice, maxPrice, pageable);
    }

    @GetMapping("/{id}")
    public ProductResponse getById(@PathVariable Long id) {
        Product p = productService.findById(id);
        return productService.toResponse(p);
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        // map request -> entity (minimal mapping; service handles persistence)
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .build();
        if (request.getCategoryId() != null) {
            product.setCategory(categoryService.findById(request.getCategoryId()));
        }

        Product saved = productService.create(product);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(productService.toResponse(saved));
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .build();
        if (request.getCategoryId() != null) {
            product.setCategory(categoryService.findById(request.getCategoryId()));
        }

        Product updated = productService.update(id, product);
        return productService.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
